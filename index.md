使用 Spring Initializr 来生成一个 Spring Boot 项目
- 使用 Maven 作为构建工具
- Java 17
- SpringBoot 3.2.2 版本
- 包含 Spring Web 依赖
- 项目的基本信息：
- GroupId: com.example
- ArtifactId: demo
- Package: com.example.demo

curl -X POST "http://localhost:8080/login?username=admin&password=password"
curl -H "Authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTc0NzYyOTc0MywiaWF0IjoxNzQ3NTQzMzQzfQ.SfsONYlc-DR_zwRs0KJ_WAx36g1d8PUCQeZvwmljOw4" http://localhost:8080/protected

curl -H "Authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTc0NzYyOTc0MywiaWF0IjoxNzQ3NTQzMzQzfQ.SfsONYlc-DR_zwRs0KJ_WAx36g1d8PUCQeZvwmljOw4" http://localhost:8080/role-protected