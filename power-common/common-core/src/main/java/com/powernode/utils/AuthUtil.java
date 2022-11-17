package com.powernode.utils;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * ClassName:AuthUtil
 * Package:com.powernode.utils
 * Description:
 * Date:2022/10/27 12:04
 * author:abc
 */
public class AuthUtil {

    public static String getLoginUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
}
