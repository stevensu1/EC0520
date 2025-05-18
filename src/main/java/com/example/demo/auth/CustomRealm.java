package com.example.demo.auth;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.util.JwtUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * 自定义Realm，处理JWT的认证和授权
 */
@Component
public class CustomRealm extends AuthorizingRealm {
    private static final Logger logger = LoggerFactory.getLogger(CustomRealm.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        
        // TODO: 从数据库或缓存中获取用户的角色和权限
        Set<String> roles = new HashSet<>();
        Set<String> permissions = new HashSet<>();
        
        // 示例：添加一些测试角色和权限
        roles.add("user");
        permissions.add("user:view");
        
        // 如果是管理员账号，添加管理员角色和权限
        if ("admin".equals(username)) {
            roles.add("admin");
            permissions.add("user:*");
        }
        
        authorizationInfo.setRoles(roles);
        authorizationInfo.setStringPermissions(permissions);
        
        return authorizationInfo;
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) 
            throws AuthenticationException {
        String token = (String) authenticationToken.getCredentials();
        
        // 验证JWT令牌
        try {
            if (!jwtUtil.verify(token)) {
                throw new AuthenticationException("JWT令牌无效");
            }
            
            // 从JWT中获取用户名
            String username = jwtUtil.getUsername(token);
            if (username == null) {
                throw new AuthenticationException("JWT令牌中无用户信息");
            }
            
            // TODO: 这里可以添加额外的用户验证逻辑
            // 例如：检查用户是否被禁用、账号是否过期等
            
            return new SimpleAuthenticationInfo(username, token, getName());
        } catch (Exception e) {
            logger.error("JWT认证失败：{}", e.getMessage());
            throw new AuthenticationException("JWT认证失败：" + e.getMessage());
        }
    }
}