package org.homework.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    // 过期时间72小時
    private static final long EXPIRE_TIME = 72L * 60 * 60 * 1000;

    private static final String TOKEN_SECRET = "CUI_GAI_LIAN";

    // 私钥和加密算法
    static Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);

    /**
     * 获取token中的claims
     *
     * @param token token
     * @return map集合
     */
    public static Map<String, Claim> getClaims(String token) {
        return JWT.require(algorithm).build().verify(token).getClaims();
    }

    /**
     * 验证token的合法性
     *
     * @param token token
     * @return true or false
     */
    public static boolean verify(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT verify = verifier.verify(token);
        return verify != null;
    }

    /**
     * 创建token
     *
     * @param map map集合
     * @return token
     */
    public static String sign(Map<String, Object> map) {
        try {
            // 设置过期时间
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            // 设置头部信息
            Map<String, Object> header = new HashMap<>(2);
            header.put("typ", "jwt");
            // 返回token字符串
            JWTCreator.Builder builder = JWT.create()
                    .withHeader(header)
                    .withIssuedAt(new Date()); // 发证时间
            map.forEach((key, value) -> {
                if (value instanceof Integer) {
                    builder.withClaim(key, (Integer) value);
                } else if (value instanceof Long) {
                    builder.withClaim(key, (Long) value);
                } else if (value instanceof Boolean) {
                    builder.withClaim(key, (Boolean) value);
                } else if (value instanceof String) {
                    builder.withClaim(key, String.valueOf(value));
                } else if (value instanceof Double) {
                    builder.withClaim(key, (Double) value);
                } else if (value instanceof Date) {
                    builder.withClaim(key, (Date) value);
                }
            });
            return builder.sign(algorithm);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
