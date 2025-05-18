# Spring Boot JWT认证示例项目

## 项目简介
这是一个基于Spring Boot的REST API示例项目，集成了JWT（JSON Web Token）认证和Shiro权限管理框架，并使用Swagger/OpenAPI进行API文档管理。

## 技术栈
- Spring Boot 2.7.18
- Apache Shiro 1.12.0
- JWT (java-jwt 4.4.0)
- SpringDoc OpenAPI (Swagger) 1.6.15
- Java 8

## 主要功能
1. JWT令牌认证
   - 用户登录获取JWT令牌
   - 基于JWT的无状态会话管理
   
2. 权限管理
   - 基于Shiro的角色权限控制
   - 支持注解式权限控制（@RequiresRoles, @RequiresPermissions）
   
3. API文档
   - 集成Swagger UI接口文档
   - 支持JWT认证的API测试

![image](https://github.com/user-attachments/assets/f61baa4c-095d-419b-99f0-5669d36531bb)


## API接口说明

### 1. 登录接口
POST /login
参数：

- username: 用户名
- password: 密码
  返回：
- token: JWT令牌
- msg: 响应消息

### 2. 受保护资源
GET /protected
要求：需要JWT认证

GET /role-protected
要求：需要'user'角色权限
## 快速开始

### 1. 环境要求
- JDK 8+
- Maven 3.6+

### 2. 构建和运行
```bash
# 克隆项目
git clone [项目地址]

# 进入项目目录
cd EC0520

# 编译打包
mvn clean package

# 运行项目
java -jar target/demo-0.0.1-SNAPSHOT.jar
Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI文档: http://localhost:8080/api-docs

## 安全配置
- JWT令牌默认有效期：24小时
- 所有API接口（除登录和Swagger文档外）都需要JWT认证
- 支持角色基础的访问控制
## 开发说明
1. 认证流程
   
   - 用户通过/login接口获取JWT令牌
   - 后续请求在Header中携带令牌： Authorization: Bearer <token>
2. 权限控制
   
   - 使用@RequiresAuthentication注解要求认证
   - 使用@RequiresRoles注解要求特定角色
   - 使用@RequiresPermissions注解要求特定权限
## 注意事项
1. 项目中的密钥（JWT_SECRET）仅用于演示，生产环境请修改为安全的密钥
2. 默认用户验证逻辑需要根据实际需求对接数据库
3. 生产环境部署时需要配置CORS和其他安全设置

这个README.md文件全面介绍了项目的主要功能、技术栈、项目结构、API接口、使用方法等信息，可以帮助其他开发者快速理解和使用这个项目。您可以根据实际需求对内容进行调整和补充。
