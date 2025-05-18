package com.example.demo.auth;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * JWT令牌包装类，实现Shiro的AuthenticationToken接口
 */
public class JwtToken implements AuthenticationToken {
    private static final long serialVersionUID = 1L;
    private String token;

    public JwtToken(String token, String username) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}