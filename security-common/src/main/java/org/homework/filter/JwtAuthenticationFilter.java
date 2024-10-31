package org.homework.filter;

import com.auth0.jwt.interfaces.Claim;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.homework.pojo.bo.LoginUser;
import org.homework.utils.JwtUtil;
import org.homework.utils.RedisUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

// TODO 验证逻辑修改
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Map<String, String[]> parameterMap = request.getParameterMap();
        parameterMap.forEach((K, V) -> log.info("k:{} , v:{}", K, V));

        // 获取token
        String token = request.getHeader("Authorization");

        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 解析token获取 redis key
        Map<String, Claim> claims = JwtUtil.getClaims(token);
        String userId = claims.get("adminId").toString();

        // 从redis获取内容
        LoginUser loginAdmin = redisUtil.getObject(redisUtil.LOGIN_KEY + userId, LoginUser.class);

        if (Objects.isNull(loginAdmin)) {
            throw new RuntimeException("用户未登录");
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginAdmin, null, loginAdmin.getAuthorities());

        // 存入SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        // 放行
        filterChain.doFilter(request, response);
    }
}
