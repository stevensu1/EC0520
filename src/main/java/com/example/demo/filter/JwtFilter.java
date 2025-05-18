package com.example.demo.filter;

import com.example.demo.auth.JwtToken;
import com.example.demo.util.JwtUtil;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JwtFilter 类是用于处理 JWT 令牌认证的过滤器。
 * 该类继承自 Shiro 的 AccessControlFilter，负责拦截请求并验证 JWT 令牌的有效性。
 */
public class JwtFilter extends AccessControlFilter {
    
    // HTTP 请求头中用于携带 JWT 令牌的字段名称
    private static final String AUTHORIZATION_HEADER = "Authorization";
    // JWT 令牌前缀标识
    private static final String BEARER_PREFIX = "Bearer ";
    
    /**
     * 判断当前请求是否允许访问受保护资源。
     * 
     * @param request   当前 ServletRequest 对象
     * @param response  当前 ServletResponse 对象
     * @param mappedValue 映射值（未使用）
     * @return 如果允许访问返回 true，否则返回 false
     * @throws Exception 如果在处理过程中发生异常
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
            throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // 处理跨域请求的预检请求（OPTIONS 方法）
        if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            return true;
        }
        
        // 从请求头中获取 JWT 令牌
        String authorization = httpRequest.getHeader(AUTHORIZATION_HEADER);
        
        // 如果没有找到令牌，返回 false 触发 onAccessDenied 方法
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            return false;
        }
        
        // 去除 Bearer 前缀，提取出 JWT 字符串
        String jwt = authorization.substring(BEARER_PREFIX.length()).trim();
        
        try {
            // 使用 JwtUtil 工具类验证 JWT 令牌的有效性
            if (!JwtUtil.verify(jwt)) {
                return false;
            }
            
            // 获取用户名信息
            String username = JwtUtil.getUsername(jwt);
            
            // 创建自定义的 JwtToken 对象，包含令牌和用户名
            JwtToken token = new JwtToken(jwt, username);
            
            // 调用 getSubject 登录，这会触发 Realm 进行身份验证
            getSubject(request, response).login(token);
            
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 当访问被拒绝时执行的方法。
     * 此方法负责设置响应头，并向客户端发送相应的错误信息。
     * 
     * @param request   当前 ServletRequest 对象
     * @param response  当前 ServletResponse 对象
     * @return 始终返回 false，表示访问被拒绝
     * @throws Exception 如果在处理过程中发生异常
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // 设置 CORS 响应头以支持跨域请求
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        
        // 获取 Authorization 头信息用于错误提示
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authorization = httpRequest.getHeader(AUTHORIZATION_HEADER);
        
        // 返回 401 未授权状态码和具体错误信息
        httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        if (authorization == null) {
            httpResponse.getWriter().write("未提供认证令牌");
        } else if (!authorization.startsWith(BEARER_PREFIX)) {
            httpResponse.getWriter().write("无效的认证令牌格式，需要 Bearer 前缀");
        } else {
            httpResponse.getWriter().write("认证令牌无效或已过期");
        }
        
        return false;
    }
}