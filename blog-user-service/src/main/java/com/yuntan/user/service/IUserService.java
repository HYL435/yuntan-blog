package com.yuntan.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuntan.user.domain.dto.front.*;
import com.yuntan.user.domain.po.User;
import com.yuntan.user.domain.vo.front.UserLoginVO;
import com.yuntan.user.domain.vo.front.UserVO;
import jakarta.servlet.http.HttpServletRequest;

public interface IUserService extends IService<User> {

    /**
     * 用户注册
     */
    UserLoginVO registerUser(UserRegisterDTO userRegisterDTO);

    /**
     * 用户登录
     */
    UserLoginVO loginUser(UserLoginDTO userLoginDTO);

    /**
     * 根据用户ID获取用户信息
     */
    UserVO getUserById(Long id);

    /**
     * 修改用户信息
     */
    void reviseUserInfo(UserDTO userDTO);

    /**
     * 修改用户密码
     */
    void reviseUserPassword(UpdateUserPwdDTO updateUserPwdDTO);

    /**
     * 用户忘记密码
     */
    void forgetUserPassword(ForgetUserPwdDTO forgetUserPwdDTO);

    /**
     * 用户登出
     */
    void logoutUser(HttpServletRequest request);

    /**
     * 启用或禁用用户
     */
    void enableOrDisableUser(Long id, Integer status);

    /**
     * 升级用户为管理员
     */
    void upgradeAdmin(Long id);
}
