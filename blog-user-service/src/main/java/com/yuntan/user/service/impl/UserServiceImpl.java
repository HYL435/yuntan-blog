package com.yuntan.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuntan.common.constant.*;
import com.yuntan.common.exception.BusinessException;
import com.yuntan.user.domain.dto.front.UserLoginDTO;
import com.yuntan.user.domain.dto.front.UserRegisterDTO;
import com.yuntan.user.domain.po.User;
import com.yuntan.user.domain.vo.front.UserLoginVO;
import com.yuntan.user.domain.vo.front.UserVO;
import com.yuntan.user.mapper.UserMapper;
import com.yuntan.user.service.IUserService;
import com.yuntan.user.utils.JwtUtil;
import com.yuntan.user.utils.UserCheckUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    // 密码加密工具
    private final BCryptPasswordEncoder passwordEncoder;
    // 用户校验工具
    private final UserCheckUtil userCheckUtil;
    // 令牌校验工具
    private final JwtUtil jwtUtil;
    // mapper
    private final UserMapper userMapper;

    /**
     * 用户注册
     */
    @Override
    public UserLoginVO registerUser(UserRegisterDTO userRegisterDTO) {

        User user = BeanUtil.copyProperties(userRegisterDTO, User.class);

        // 参数校验
        userCheckUtil.userRegisterChack(user);

        // 唯一性验证
        if (isEmailExist(user.getEmail())) {
            throw BusinessException.emailExist();
        }
        // 校验用户名是否已存在
        if (isUsernameExist(user.getUsername())) {
            throw BusinessException.usernameExist();
        }

        // 密码加密（使用 BCrypt）
        user.setPassword(encryptedPassword(user.getPassword()));

        // 设置默认值（默认昵称为邮箱）
        setDefaultValues(user);

        // 数据清洗
        user.setUsername(user.getUsername().trim());
        user.setEmail(user.getEmail().trim());

        // 落库
        this.save(user);

        // 生成jwt令牌返回给前端，自动登录
        String token = jwtUtil.createToken(user.getId());

        // 将用户注册信息转换为VO返回
        UserLoginVO userLoginVO = BeanUtil.copyProperties(user, UserLoginVO.class);
        userLoginVO.setId(user.getId());
        userLoginVO.setToken(token);

        // TODO 发送注册成功的邮件通知

        return userLoginVO;
    }

    /**
     * 用户登录
     */
    @Override
    public UserLoginVO loginUser(UserLoginDTO userLoginDTO) {

        User user = BeanUtil.copyProperties(userLoginDTO, User.class);
        // 参数校验
        userCheckUtil.userLoginChack(user);

        // 根据用户名或邮箱查询用户
        UserLoginVO userLoginVO = selectUserByInfo(user);

        // 账号状态校验
        if (Objects.equals(userLoginVO.getStatus(), StatusConstant.DISABLE)) {
            throw new BusinessException(MessageConstant.ACCOUNT_LOCKED);
        }

        return userLoginVO;
    }

    /**
     * 根据用户ID获取用户信息
     */
    @Override
    public UserVO getUserById(Long id) {

        User user = new User();
        user.setId(id);

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId, id)
                .eq(User::getDeleted, DeleteStatusConstant.NOT_DELETED);

        User userInfo = this.getOne(wrapper);

        return BeanUtil.copyProperties(userInfo, UserVO.class);
    }

    // 根据用户名或邮箱查询用户
    private UserLoginVO selectUserByInfo(User user) {
        String usernameOrEmail = StringUtils.hasText(user.getUsername())
                ? user.getUsername()
                : user.getEmail();

        if (!StringUtils.hasText(usernameOrEmail)) {
            throw BusinessException.badRequest("用户名或邮箱不能为空");
        }

        // 1. 先根据用户名或邮箱查询用户（不比较密码）
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(i -> i.eq(User::getUsername, usernameOrEmail.trim())
                        .or()
                        .eq(User::getEmail, usernameOrEmail.trim()))
                .eq(User::getDeleted, DeleteStatusConstant.NOT_DELETED);

        User loginUser = this.getOne(wrapper);
        if (loginUser == null) {
            throw BusinessException.badRequest(MessageConstant.ACCOUNT_OR_PASSWORD_ERROR);
        }

        // 2. 使用 matches() 方法验证密码
        if (!passwordEncoder.matches(user.getPassword(), loginUser.getPassword())) {
            throw BusinessException.badRequest(MessageConstant.ACCOUNT_OR_PASSWORD_ERROR);
        }

        // 3. 账号状态校验
        if (Objects.equals(loginUser.getStatus(), StatusConstant.DISABLE)) {
            throw new BusinessException(MessageConstant.ACCOUNT_LOCKED);
        }

        // 4. 生成令牌
        String token = jwtUtil.createToken(loginUser.getId());

        // 5. 转换为VO
        UserLoginVO userLoginVO = BeanUtil.copyProperties(loginUser, UserLoginVO.class);
        userLoginVO.setToken(token);

        return userLoginVO;
    }

    /**
     * 设置默认值
     */
    private void setDefaultValues(User user) {
        // 首次注册会自动将昵称设置为邮箱
        if (!StringUtils.hasText(user.getNickname())) {
                // 策略A：直接用完整邮箱
                user.setNickname(user.getEmail().trim());

                // 策略B（可选）：只取邮箱 @ 前面的部分，如果需要可以取消注释下面这行
                // user.setNickname(user.getEmail().substring(0, user.getEmail().indexOf("@")));
        }
        // 设置默认角色为普通用户
        user.setRole(RoleConstant.ROLE_USER);
        // 设置默认状态为正常
        user.setStatus(StatusConstant.ENABLE);
        // 设置用户默认头像
        user.setImage(DefaultImageURLConstant.DEFAULT_AVATAR_URL);

    }
    // 密码加密（使用 BCrypt）
    public String encryptedPassword(String password) {
        return passwordEncoder.encode(password);
    }

    // 用户名是否存在
    public boolean isUsernameExist(String username) {
        // 非空校验
        if (!StringUtils.hasText(username)) return false;
        // mybatis-plus 查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 添加判断条件
        wrapper.eq(User::getUsername, username.trim());
        // 查询结果
        return this.count(wrapper) > 0;
    }

    // 邮箱是否存在
    public boolean isEmailExist(String email) {
        if (!StringUtils.hasText(email)) return false;
        //   lambda 方式添加查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 添加判断条件
        wrapper.eq(User::getEmail, email.trim());
        return this.count(wrapper) > 0;
    }
}