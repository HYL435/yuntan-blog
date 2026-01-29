package com.yuntan.common.context;

import jakarta.servlet.http.HttpServletRequest;

public class BaseContext {

    private static final ThreadLocal<HttpServletRequest> REQUEST_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();

    public static void setRequest(HttpServletRequest request) {
        REQUEST_HOLDER.set(request);
    }

    public static HttpServletRequest getRequest() {
        return REQUEST_HOLDER.get();
    }

    public static void setUserId(Long userId) {
        USER_ID_HOLDER.set(userId);
    }

    public static Long getUserId() {
        return USER_ID_HOLDER.get();
    }

    public static void remove() {
        REQUEST_HOLDER.remove();
        USER_ID_HOLDER.remove();
    }

}
