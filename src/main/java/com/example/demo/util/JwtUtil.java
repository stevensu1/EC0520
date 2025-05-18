package com.example.demo.util;


import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * JWT工具类，提供Token的生成、解析和验证功能
 * 
 * @author YourName
 * @version 1.0
 */
public class JwtUtil {
    /**
     * Token过期时间（毫秒）
     * 默认设置为24小时
     */
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000; // 24小时

    /**
     * 签名密钥
     * 用于签名和验证JWT的HMAC算法密钥
     */
    private static final String SECRET = "your-secret-key";

    /**
     * 创建JWT Token
     * 
     * @param username 需要写入Token的主题（用户名）
     * @return 生成的Token字符串
     */
    public static String createToken(String username) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(date)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    /**
     * 从Token中提取用户名
     * 
     * @param token JWT Token字符串
     * @return 提取的用户名，如果解析失败返回null
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getSubject();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 验证Token有效性
     * 
     * @param token JWT Token字符串
     * @return 验证结果，true表示有效，false表示无效
     */
    public static boolean verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWT.require(algorithm).build().verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}