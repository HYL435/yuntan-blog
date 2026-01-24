package com.yuntan.user.check;

import com.yuntan.common.constant.MessageConstant;
import com.yuntan.common.exception.BusinessException;
import com.yuntan.user.domain.po.User;
import org.springframework.util.StringUtils;

public class UserInfoCheck {

    /**
     * 参数基础校验
     */
    public static boolean validateRegisterUser(User user) {
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

    public static boolean validateLoginUser(User user) {

        if (user == null) return false;

        // 必须提供密码
        if (!StringUtils.hasText(user.getPassword())) {
            return false;
        }
        // 必须提供用户名或邮箱
        if (!StringUtils.hasText(user.getEmail()) && !StringUtils.hasText(user.getUsername())) {
            return false;
        }

        // 格式验证
        return isValidEmail(user.getEmail()) && isValidUsername(user.getUsername());
    }

    public static boolean validateReviseUserInfo(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }

        // 必须提供邮箱
        if (!StringUtils.hasText(user.getEmail())) {
            return false;
        }

        if (!isValidEmail(user.getEmail())) {
            throw BusinessException.badRequest(MessageConstant.EMAIL_FORMAT_ERROR);
        }
        if (!isValidPhone(user.getPhone())) {
            throw BusinessException.badRequest(MessageConstant.PHONE_FORMAT_ERROR);
        }
        if (!isValidNickname(user.getNickname())) {
            throw BusinessException.badRequest(MessageConstant.NICKNAME_FORMAT_INVALID);
        }
        return true;

    }


    /**
     * 邮箱格式验证
     */
    private static boolean isValidEmail(String email) {
        if (!StringUtils.hasText(email)) return true; // 为空时不验证格式
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    /**
     * 用户名格式验证
     */
    private static boolean isValidUsername(String username) {
        if (!StringUtils.hasText(username)) return true; // 为空时不验证格式
        return username.length() >= 3 &&
                username.length() <= 20 &&
                username.matches("^[a-zA-Z0-9_]+$");
    }

    /**
     * 手机号格式验证
     */
    private static boolean isValidPhone(String phone) {
        if (!StringUtils.hasText(phone)) return true; // 为空时不验证格式
        return phone.matches("^\\+?[1-9]\\d{1,14}$");
    }

    /**
     * 密码强度验证（至少同时包含数字和字母）
     */
    public static boolean isValidPassword(String password) {
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
     * 昵称格式验证
     */
    public static boolean isValidNickname(String nickname1) {
        // 昵称格式验证
        if (StringUtils.hasText(nickname1)) {
            String nickname = nickname1.trim();
            return !nickname.isEmpty() && nickname.length() <= 30;
        }
        return true;
    }



}
