package com.yuntan.user.utils;

import cn.hutool.core.bean.BeanUtil;
import com.yuntan.common.constant.MessageConstant;
import com.yuntan.common.exception.BusinessException;
import com.yuntan.user.domain.dto.front.UserDTO;
import com.yuntan.user.domain.dto.front.UserRegisterDTO;
import com.yuntan.user.domain.po.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserCheckUtil {

    public void userRegisterChack(User user) {
        // 1. 参数基础校验
        if (!validateRegisterUser(user)) {
            throw BusinessException.badRequest(MessageConstant.USER_INFO_INCOMPLETE);
        }

        // 2. 密码强度验证
        if (!isValidPassword(user.getPassword())) {
            throw BusinessException.badRequest(MessageConstant.PASSWORD_STRENGTH_INSUFFICIENT);
        }
    }

    /**
     * 参数基础校验
     */
    private boolean validateRegisterUser(User user) {
        if (user == null) return false;

        // 判断用户名和密码、邮箱是否有值
        if (!StringUtils.hasText(user.getUsername()) ||
                !StringUtils.hasText(user.getPassword()) ||
                !StringUtils.hasText(user.getEmail())) {
            return false;
        }

        String username = user.getUsername().trim();
        String email = user.getEmail().trim();
        // 简单正则：只允许字母、数字、下划线，3-20位
        return username.length() >= 3 &&
                username.length() <= 20 &&
                username.matches("^[a-zA-Z0-9_]+$") &&
                email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * 邮箱格式验证
     */
    private boolean isValidEmail(String email) {
        if (!StringUtils.hasText(email)) return true; // 为空时不验证格式
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    /**
     * 用户名格式验证
     */
    private boolean isValidUsername(String username) {
        if (!StringUtils.hasText(username)) return true; // 为空时不验证格式
        return username.length() >= 3 &&
                username.length() <= 20 &&
                username.matches("^[a-zA-Z0-9_]+$");
    }

    /**
     * 密码强度验证（至少同时包含数字和字母）
     */
    private boolean isValidPassword(String password) {
        // 基础判断
        if (!StringUtils.hasText(password)) return false;
        if (password.length() < 8 || password.length() > 20) return false;

        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            // 判断是否包含字母和数字
            if (Character.isLetter(c)) hasLetter = true;
            else if (Character.isDigit(c)) hasDigit = true;
            // 如果同时包含字母和数字，提前退出循环
            if (hasLetter && hasDigit) break;
        }
        return hasLetter && hasDigit;
    }

    /**
     * 用户登录参数校验
     */
    public void userLoginChack(User user) {

        // 参数基础校验
        if (!validateLoginUser(user)) {
            throw BusinessException.badRequest(MessageConstant.USER_INFO_INCOMPLETE);
        }

        //  密码强度验证
        if (!isValidPassword(user.getPassword())) {
            throw BusinessException.badRequest(MessageConstant.PASSWORD_STRENGTH_INSUFFICIENT);
        }
    }

    private boolean validateLoginUser(User user) {

        if (user == null) return false;

        // 必须提供密码
        if (!StringUtils.hasText(user.getPassword())) {
            return false;
        }
        // 必须提供用户名或邮箱
        if (!StringUtils.hasText(user.getEmail()) || !StringUtils.hasText(user.getUsername())) {
            return false;
        }

        // 格式验证
        return isValidEmail(user.getEmail()) && isValidUsername(user.getUsername());
    }
}
