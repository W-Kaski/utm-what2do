# 开发环境设置指南

## 📦 使用Docker Compose（推荐）

最简单的方式是使用Docker Compose同时启动MySQL和Redis：

### 1. 启动服务
```bash
# 在项目根目录运行
cd /path/to/utm-what2do
docker-compose up -d
```

### 2. 验证服务
```bash
# 检查MySQL
docker exec -it utm-what2do-mysql mysql -uroot -p123456 -e "SHOW DATABASES;"

# 检查Redis
docker exec -it utm-what2do-redis redis-cli ping
```

### 3. 停止服务
```bash
docker-compose down
```

### 4. 完全清理（包括数据）
```bash
docker-compose down -v
```

---

## 🛠️ 手动安装MySQL

### Windows
1. 下载MySQL安装程序：https://dev.mysql.com/downloads/installer/
2. 安装MySQL Server 8.0+
3. 设置root密码为 `123456`
4. 启动MySQL服务：
   ```cmd
   net start MySQL80
   ```

### macOS
```bash
# 使用Homebrew安装
brew install mysql@8.0
brew services start mysql@8.0

# 设置root密码
mysql_secure_installation
```

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql
sudo mysql_secure_installation
```

---

## 🗄️ 创建数据库

连接到MySQL后，创建项目数据库：

```sql
CREATE DATABASE IF NOT EXISTS utm_what2do
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- 创建用户（可选）
CREATE USER IF NOT EXISTS 'utm_user'@'localhost' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON utm_what2do.* TO 'utm_user'@'localhost';
FLUSH PRIVILEGES;
```

---

## 🚀 启动应用

### 方式1：使用Maven
```bash
cd utm-what2do-backend
mvn spring-boot:run
```

### 方式2：使用IDE
在IntelliJ IDEA或Eclipse中运行 `UtmWhat2DoBackendApplication` 主类

### 方式3：打包后运行
```bash
mvn clean package -DskipTests
java -jar target/utm-what2do-backend-0.0.1-SNAPSHOT.jar
```

---

## 🔧 配置说明

### 开发环境配置
`src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/utm_what2do?useUnicode=true&characterEncoding=utf8
    username: root
    password: 123456

  data:
    redis:
      host: localhost
      port: 6379
```

### 如果MySQL使用不同端口或密码
修改 `application.yml` 中的配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:YOUR_PORT/utm_what2do
    username: YOUR_USERNAME
    password: YOUR_PASSWORD
```

---

## 📝 验证服务

### 1. 检查应用启动
访问：http://localhost:8080

### 2. 查看API文档
访问：http://localhost:8080/doc.html
或：http://localhost:8080/swagger-ui.html

### 3. 测试用户注册
```bash
curl -X POST http://localhost:8080/api/v1/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@utm.utoronto.ca",
    "role": "USER"
  }'
```

---

## ⚠️ 常见问题

### 问题1：端口被占用
**错误**: `Port 8080 was already in use`

**解决**:
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/macOS
lsof -ti:8080 | xargs kill -9
```

### 问题2：Redis连接失败
**错误**: `Unable to connect to Redis`

**解决**:
- 确保Redis正在运行
- 或在 `application.yml` 中临时注释掉Redis配置

### 问题3：数据库连接失败
**错误**: `Communications link failure`

**解决**:
1. 确认MySQL正在运行
2. 检查端口3306是否开放
3. 验证用户名和密码
4. 确认数据库 `utm_what2do` 已创建

---

## 🔍 调试技巧

### 查看日志
```bash
# 应用日志会输出到控制台
# 或查看日志文件（如果配置了）
tail -f logs/spring.log
```

### 连接MySQL查看数据
```bash
docker exec -it utm-what2do-mysql mysql -uroot -p123456 utm_what2do

# 或使用本地MySQL
mysql -uroot -p utm_what2do
```

### 查看表结构
```sql
SHOW TABLES;
DESCRIBE users;
DESCRIBE events;
```

---

## 📚 下一步

1. ✅ 启动MySQL和Redis
2. ✅ 运行Spring Boot应用
3. ✅ 访问Swagger文档测试API
4. 🔜 导入测试数据
5. 🔜 开始前端开发

如有问题，请查看 `BACKEND_SETUP_SUMMARY.md` 或提Issue。
