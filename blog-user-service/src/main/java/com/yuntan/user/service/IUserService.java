package com.yuntan.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuntan.user.domain.dto.front.UserLoginDTO;
import com.yuntan.user.domain.dto.front.UserRegisterDTO;
import com.yuntan.user.domain.po.User;
import com.yuntan.user.domain.vo.front.UserLoginVO;
import com.yuntan.user.domain.vo.front.UserVO;

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
}
