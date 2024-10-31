package org.homework.utils;

import org.homework.pojo.bo.LoginUser;
import org.homework.pojo.po.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityInfoUtil {

    public static LoginUser getLoginUser() {
        return (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static User getUser() {
        return getLoginUser().getUser();
    }

    public static Long getUserId() {
        return getUser().getId();
    }

}
