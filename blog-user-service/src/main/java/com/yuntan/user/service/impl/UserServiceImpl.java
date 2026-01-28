package com.yuntan.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuntan.common.constant.*;
import com.yuntan.common.exception.BusinessException;
import com.yuntan.common.utils.BeanUtils;
import com.yuntan.common.utils.OssOptionUtil;
import com.yuntan.gateway.utils.JwtUtil;
import com.yuntan.user.domain.dto.front.*;
import com.yuntan.user.domain.po.User;
import com.yuntan.user.domain.vo.front.UserLoginVO;
import com.yuntan.user.domain.vo.front.UserVO;
import com.yuntan.user.mapper.UserMapper;
import com.yuntan.user.service.IUserService;
import com.yuntan.user.utils.TokenBlacklistUtil;
import com.yuntan.user.utils.TokenResolveUtil;
import com.yuntan.user.utils.UserCheckUtil;
import com.yuntan.user.utils.UserOssUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
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
    // OSS 工具类
    private final UserOssUtil userOssUtil;
    // Redis 操作模板
    private final TokenBlacklistUtil tokenBlacklistUtil;
    // token解析工具
    private final TokenResolveUtil tokenResolveUtil;

    /**
     * 用户注册
     */
    @Override
    public UserLoginVO registerUser(UserRegisterDTO userRegisterDTO) {

        User user = BeanUtils.copyBean(userRegisterDTO, User.class);

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
        String token = jwtUtil.createToken(user.getRole(), user.getId());

        // 将用户注册信息转换为VO返回
        UserLoginVO userLoginVO = BeanUtils.copyBean(user, UserLoginVO.class);
        userLoginVO.setId(user.getId());
        userLoginVO.setToken(token);

        // TODO 发送注册成功的邮件通知

        return userLoginVO;
    }

    /**
     * 用户登录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLoginVO loginUser(UserLoginDTO userLoginDTO) {

        User user = BeanUtils.copyBean(userLoginDTO, User.class);
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

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId, id)
                .eq(User::getDeleted, DeleteStatusConstant.NOT_DELETED);

        User userInfo = this.getOne(wrapper);

        UserVO userVO = BeanUtils.copyBean(userInfo, UserVO.class);

        if (userVO == null) {
            throw new BusinessException(MessageConstant.USER_NOT_FOUND);
        }

        return userVO;
    }

    /**
     * 修改用户信息
     */
    @Transactional(rollbackFor = Exception.class) // 开启事务
    @Override
    public void reviseUserInfo(UserDTO userDTO) {

        User user = BeanUtils.copyBean(userDTO, User.class);

        // 参数校验
        userCheckUtil.reviseUserInfoCheck(user);

        // 数据清洗
        if (StringUtils.hasText(user.getNickname())) {
            user.setNickname(user.getNickname().trim());
        }
        if (StringUtils.hasText(user.getImage())) {
            user.setImage(user.getImage().trim());
        }

        // 上传头像到OSS并获取URL，并存入user
        try {
            // 获取原来的用户信息
            User oldUser = this.getById(user.getId());
            if (oldUser == null) {
                throw new BusinessException(MessageConstant.USER_NOT_FOUND);
            }
            // 清理oss上的原头像
            userOssUtil.deleteFile(oldUser.getImage());
            // 上传新头像
            String image = userOssUtil.uploadFile(userDTO.getImageFile(), FilePathConstant.USER_AVATAR_PATH);
            // 将新头像URL存入user
            user.setImage(image);
        } catch (IOException e) {
            throw new BusinessException(MessageConstant.UPLOAD_FAILED);
        }

        // 更新用户信息
        this.updateById(user);
    }

    /**
     * 修改用户密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviseUserPassword(UpdateUserPwdDTO updateUserPwdDTO) {

        // 参数校验
        userCheckUtil.reviseUserPasswordCheck(updateUserPwdDTO);

        // 获取用户信息
        User oldUser = this.getById(updateUserPwdDTO.getId());

        // 校验旧密码是否正确
        if (!passwordEncoder.matches(updateUserPwdDTO.getOldPassword(), oldUser.getPassword())) {
            throw BusinessException.badRequest(MessageConstant.OLD_PASSWORD_INCORRECT);
        }

        // 加密新密码
        String newEncryptedPassword = encryptedPassword(updateUserPwdDTO.getNewPassword());

        // 更新密码
        User updateUser = User.builder()
                .id(updateUserPwdDTO.getId())
                .password(newEncryptedPassword).build();
        this.updateById(updateUser);

    }

    /**
     * TODO 用户忘记密码
     */
    @Override
    public void forgetUserPassword(ForgetUserPwdDTO forgetUserPwdDTO) {

        // 参数校验

        // 验证邮箱是否存在

        // TODO 验证码校验（略）

        // 加密新密码
        String newEncryptedPassword = encryptedPassword(forgetUserPwdDTO.getNewPassword());

        // 更新密码
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, forgetUserPwdDTO.getEmail().trim())
                .eq(User::getDeleted, DeleteStatusConstant.NOT_DELETED);

    }

    /**
     * 用户登出
     */
    @Override
    public void logoutUser(HttpServletRequest request) {

        // 1. 从请求头中提取Token
        String token = tokenResolveUtil.extractTokenFromHeader(request);

        // 连接Redis将token加入黑名单
        tokenBlacklistUtil.addLogoutToken(token);

        // 判断是否存入成功
        if (!tokenBlacklistUtil.isLogoutToken(token)) {
            throw BusinessException.internalError(MessageConstant.LOGOUT_FAILED);
        }

    }

    /**
     * 启用或禁用用户
     */
    @Override
    public void enableOrDisableUser(Long id, Integer status) {

        status = Objects.equals(status, StatusConstant.DISABLE) ? StatusConstant.ENABLE : StatusConstant.DISABLE;


        // 创建用户对象
        User user = User.builder()
                .id(id)
                .status(status)
                .build();

        // 更新用户状态
        this.updateById(user);

    }

    /**
     * 升级用户为管理员
     */
    @Override
    public void upgradeAdmin(Long id) {

        // 创建用户对象
        User user = User.builder()
                .id(id)
                .role(UserRoleConstant.ROLE_ADMIN)
                .build();

        // 更新用户角色
        this.updateById(user);

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

        // 执行查询
        User loginUser = this.getOne(wrapper);

        // 若用户存在，更新最后登录时间
        if (!ObjectUtils.isEmpty(loginUser)) {
            // 设置最后登录时间
            loginUser.setLastLoginTime(LocalDateTime.now());
            // 执行更新（仅更新修改的字段，推荐用updateById）
            this.updateById(loginUser);
        }
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
        String token = jwtUtil.createToken(loginUser.getRole(), loginUser.getId());

        // 5. 转换为VO
        UserLoginVO userLoginVO = BeanUtils.copyBean(loginUser, UserLoginVO.class);
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
        // 设置默认角色
        user.setRole(UserRoleConstant.ROLE_USER);
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