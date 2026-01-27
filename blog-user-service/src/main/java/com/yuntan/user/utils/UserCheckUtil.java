package com.yuntan.user.utils;

import com.yuntan.common.constant.MessageConstant;
import com.yuntan.common.exception.BusinessException;
import com.yuntan.user.check.UserInfoCheck;
import com.yuntan.user.domain.dto.front.UpdateUserPwdDTO;
import com.yuntan.user.domain.po.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserCheckUtil {

    /**
     * 用户登录参数校验
     */
    public void userLoginChack(User user) {

        // 参数基础校验
        if (!UserInfoCheck.validateLoginUser(user)) {
            throw BusinessException.badRequest(MessageConstant.USER_INFO_INCOMPLETE);
        }

        //  密码强度验证
        if (!UserInfoCheck.isValidPassword(user.getPassword())) {
            throw BusinessException.badRequest(MessageConstant.PASSWORD_STRENGTH_INSUFFICIENT);
        }
    }

    public void userRegisterChack(User user) {
        // 1. 参数基础校验
        if (!UserInfoCheck.validateRegisterUser(user)) {
            throw BusinessException.badRequest(MessageConstant.USER_INFO_FORMAT_ERROR);
        }

        // 2. 密码强度验证
        if (!UserInfoCheck.isValidPassword(user.getPassword())) {
            throw BusinessException.badRequest(MessageConstant.PASSWORD_STRENGTH_INSUFFICIENT);
        }
    }



    public void reviseUserInfoCheck(User user) {
        // 参数基础校验
        if (!UserInfoCheck.validateReviseUserInfo(user)) {
            throw BusinessException.badRequest(MessageConstant.USER_INFO_INCOMPLETE);
        }

    }

    /**
     * 修改用户密码参数校验
     */
    public void reviseUserPasswordCheck(UpdateUserPwdDTO updateUserPwdDTO) {

        // 1. 参数基础校验
        if (updateUserPwdDTO == null) {
            throw BusinessException.badRequest(MessageConstant.USER_INFO_INCOMPLETE);
        }
        if (!StringUtils.hasText(updateUserPwdDTO.getOldPassword()) ||
                !StringUtils.hasText(updateUserPwdDTO.getNewPassword())) {
            throw BusinessException.badRequest(MessageConstant.USER_INFO_INCOMPLETE);
        }

        // 2. 密码强度验证
        if (!UserInfoCheck.isValidPassword(updateUserPwdDTO.getNewPassword()) &&
                !UserInfoCheck.isValidPassword(updateUserPwdDTO.getOldPassword())) {
            throw BusinessException.badRequest(MessageConstant.PASSWORD_STRENGTH_INSUFFICIENT);
        }

    }
}
