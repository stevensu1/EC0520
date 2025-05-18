package com.example.demo.controller;

import com.example.demo.util.JwtUtil;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        // 这里简化了验证逻辑，实际应用中需要查询数据库验证用户名密码
        if ("admin".equals(username) && "password".equals(password)) {
            String token = JwtUtil.createToken(username);
            result.put("token", token);
            result.put("msg", "登录成功");
        } else {
            result.put("msg", "用户名或密码错误");
        }
        return result;
    }

    @GetMapping("/protected")
    @RequiresAuthentication
    public String protected_resource() {
        return "这是受保护的资源";
    }

    @GetMapping("/role-protected")
    @RequiresRoles("user")
    public String roleProtected() {
        return "这是需要user角色才能访问的资源";
    }
}