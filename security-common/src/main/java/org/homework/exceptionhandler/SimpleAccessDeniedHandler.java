package org.homework.exceptionhandler;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.homework.utils.ResponseCode;
import org.homework.utils.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * - 自定义授权异常处理类
 */
public class SimpleAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws ServletException, IOException {
        String json = JSON.toJSONString(
                Result.fail("无权限", ResponseCode.STATUS_NOT_LOGGED_IN)
        );
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
