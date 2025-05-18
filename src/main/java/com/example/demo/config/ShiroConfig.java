package com.example.demo.config;

import com.example.demo.auth.CustomRealm;
import com.example.demo.filter.JwtFilter;
import com.example.demo.util.JwtUtil;

import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro安全框架配置类
 * 用于配置Shiro的核心组件，包括安全管理器、过滤器链和JWT集成
 */
@Configuration
public class ShiroConfig {

    /**
     * 配置Shiro生命周期处理器
     * 主要用于管理Shiro bean的生命周期，确保在Spring容器中正确初始化和销毁
     * 这个处理器负责调用Shiro bean的init()和destroy()方法
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 配置DefaultAdvisorAutoProxyCreator
     * 用于开启Spring对Shiro注解的支持，使@RequiresRoles等注解生效
     * @DependsOn注解确保在lifecycleBeanPostProcessor之后创建
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        // 设置代理目标类为true，使用CGLIB代理
        creator.setProxyTargetClass(true);
        return creator;
    }

    /**
     * 配置Shiro的安全管理器SecurityManager
     * SecurityManager是Shiro架构的核心，协调其他组件完成认证和授权
     * @param customRealm 自定义的认证和授权领域实现
     * @return DefaultWebSecurityManager实例
     */
    @Bean
    public DefaultWebSecurityManager securityManager(CustomRealm customRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 设置自定义Realm，用于处理认证和授权
        securityManager.setRealm(customRealm);

        // 禁用Shiro自带的session管理，因为我们使用JWT进行无状态认证
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);

        return securityManager;
    }

    /**
     * 配置Shiro的过滤器工厂
     * 用于设置URL级别的权限控制，定义哪些URL需要认证，哪些可以匿名访问
     * @param securityManager 安全管理器实例
     * @return ShiroFilterFactoryBean实例
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        // 设置安全管理器
        factoryBean.setSecurityManager(securityManager);

        // 注册JWT过滤器，用于处理基于JWT的认证
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("jwt", new JwtFilter());
        factoryBean.setFilters(filterMap);

        // 设置未授权时的跳转URL
        factoryBean.setUnauthorizedUrl("/unauthorized");

        // 配置URL过滤器链，使用LinkedHashMap保证顺序
        Map<String, String> filterRuleMap = new LinkedHashMap<>();
        
        // 配置不需要认证的URL（匿名可访问）
        filterRuleMap.put("/login", "anon");  // 登录接口
        filterRuleMap.put("/unauthorized", "anon");  // 未授权跳转页面
        
        // 配置Swagger相关路径为匿名访问，方便API文档测试
        filterRuleMap.put("/swagger-ui.html", "anon");
        filterRuleMap.put("/swagger-ui/**", "anon");
        filterRuleMap.put("/api-docs/**", "anon");
        filterRuleMap.put("/api-docs.yaml", "anon");
        filterRuleMap.put("/v3/api-docs/**", "anon");
        filterRuleMap.put("/swagger-resources/**", "anon");
        filterRuleMap.put("/webjars/**", "anon");
        
        // 所有其他请求都需要经过JWT过滤器进行认证
        filterRuleMap.put("/**", "jwt");
        
        factoryBean.setFilterChainDefinitionMap(filterRuleMap);
        return factoryBean;
    }

    /**
     * 配置JWT工具类的Bean
     * 用于处理JWT令牌的生成、验证等操作
     */
    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }

    /**
     * 配置Shiro注解支持
     * 启用@RequiresRoles、@RequiresPermissions等注解功能
     * @param securityManager 安全管理器实例
     * @return AuthorizationAttributeSourceAdvisor实例
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}