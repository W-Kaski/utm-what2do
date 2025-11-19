# UTM What2Do 后端架构搭建总结

## ✅ 已完成的工作

### 1. 核心技术栈集成
- ✅ **Spring Boot 3.2.5** - 核心框架
- ✅ **MyBatis Plus 3.5.6** - 持久层框架
- ✅ **Sa-Token 1.37.0** - 权限认证框架
- ✅ **Redis** - 分布式缓存
- ✅ **Caffeine** - 本地缓存
- ✅ **Knife4j 4.5.0** - API文档工具
- ✅ **Hutool** - Java工具库

### 2. 项目结构搭建

#### 2.1 配置文件 (`application.yml`)
- ✅ 数据源配置（MySQL）
- ✅ Redis配置
- ✅ Sa-Token配置
- ✅ Caffeine缓存配置
- ✅ MyBatis Plus配置
- ✅ Knife4j文档配置
- ✅ CORS跨域配置

#### 2.2 常量定义 (`constant/`)
- ✅ `RoleConstants` - 角色常量（USER, CLUB_MANAGER, ADMIN）
- ✅ `CacheConstants` - 缓存Key常量
- ✅ `EventConstants` - 活动相关常量

#### 2.3 自定义注解 (`annotation/`)
- ✅ `@CheckRole` - 权限校验注解

#### 2.4 AOP切面 (`aop/`)
- ✅ `AuthCheckAspect` - 权限校验切面，拦截@CheckRole注解

#### 2.5 公共模块 (`common/`)

**异常处理:**
- ✅ `BusinessException` - 业务异常基类
- ✅ `GlobalExceptionHandler` - 全局异常处理器
  - Sa-Token异常处理
  - 参数校验异常处理
  - 业务异常处理

**统一响应:**
- ✅ `ResultVO` - 统一API返回结果类
- ✅ `StatusCode` - 状态码枚举

**工具类:**
- ✅ `SaTokenUtil` - Sa-Token封装工具
- ✅ `ThrowUtils` - 异常抛出工具
- ✅ `DateUtils` - 日期时间工具

#### 2.6 配置类 (`config/`)
- ✅ `SaTokenConfig` - Sa-Token权限配置
  - 过滤器配置
  - 路由拦截配置
  - 权限接口实现
- ✅ `RedisConfig` - Redis序列化配置
- ✅ `CaffeineCacheConfig` - Caffeine缓存配置
- ✅ `WebConfig` - Web MVC配置（CORS、异步支持）

#### 2.7 数据传输对象 (`model/dto/`)
- ✅ `UserRegisterDTO` - 用户注册请求
- ✅ `UserLoginDTO` - 用户登录请求
- ✅ `EventCreateDTO` - 活动创建请求
- ✅ `EventFilterDTO` - 活动筛选请求
- ✅ `PostCreateDTO` - 帖子发布请求

#### 2.8 视图对象 (`model/vo/`)
- ✅ `UserInfoVO` - 用户信息展示
- ✅ `EventCardVO` - 活动卡片展示（列表）
- ✅ `EventDetailVO` - 活动详情展示
- ✅ `BuildingCountVO` - 建筑活动计数展示
- ✅ `ClubDetailVO` - 社团详情展示

#### 2.9 控制器层 (`controller/`)
- ✅ `UserController` - 用户管理API
  - POST `/api/v1/users/register` - 用户注册
  - POST `/api/v1/users/login` - 用户登录
  - POST `/api/v1/users/logout` - 用户登出
  - GET `/api/v1/users/me` - 获取个人档案
  - GET `/api/v1/users/{id}` - 获取用户档案
  - PUT `/api/v1/users/me` - 更新个人档案

---

## 🚧 需要完善的部分

### 1. Service层业务逻辑实现

需要在现有Service接口的基础上，补充以下业务逻辑：

#### `UsersService` - 用户服务
```java
- register(UserRegisterDTO) - 用户注册逻辑
  - 检查用户名/邮箱是否已存在
  - 密码加密（使用BCrypt）
  - 创建用户记录
  - Sa-Token登录并设置角色

- login(UserLoginDTO) - 用户登录逻辑
  - 验证用户名密码
  - Sa-Token登录
  - 返回Token和用户信息

- getUserInfo(Long userId) - 获取用户信息
  - 从数据库查询
  - 转换为VO对象

- updateProfile(Long userId, UserInfoVO) - 更新用户档案
```

#### `EventsService` - 活动服务
```java
- createEvent(EventCreateDTO) - 创建活动
  - 验证组织者权限
  - 保存活动信息
  - 保存活动标签关联
  - 清除相关Redis缓存

- getEventList(EventFilterDTO) - 获取活动列表
  - 根据时间筛选（Today/Week/Month）
  - 根据建筑筛选
  - 根据标签筛选
  - 搜索关键词
  - 排序
  - 分页
  - 优先从Redis缓存读取

- getEventDetail(Long eventId) - 获取活动详情
  - 优先从Redis缓存读取
  - 关联查询建筑、组织者信息
  - 查询活动标签
```

#### `BuildingsService` - 建筑服务
```java
- getBuildingList() - 获取建筑列表
  - 从Caffeine本地缓存读取

- getBuildingEventCounts() - 获取建筑活动计数
  - 统计每个建筑今日活动数量
  - 使用Redis缓存（5分钟）
  - 返回BuildingCountVO列表
```

#### `ClubsService` - 社团服务
```java
- getClubDetail(String slug) - 获取社团详情
  - 查询社团基本信息
  - 查询社团即将举办的活动
  - 组装ClubDetailVO
```

#### `PostsService` - 帖子服务
```java
- createPost(PostCreateDTO) - 发布帖子
- getPostList() - 获取帖子列表
- getPostDetail(Long postId) - 获取帖子详情
```

### 2. Controller层补充

创建以下Controller：

#### `EventController`
```java
GET  /api/v1/events - 获取活动列表（支持筛选）
GET  /api/v1/events/{id} - 获取活动详情
POST /api/v1/events - 发布新活动 [需要CLUB_MANAGER权限]
PUT  /api/v1/events/{id} - 更新活动 [需要CLUB_MANAGER权限]
POST /api/v1/events/{id}/interested - 标记感兴趣
GET  /api/v1/tags/event - 获取活动标签列表
```

#### `MapController`
```java
GET /api/v1/buildings - 获取建筑列表
GET /api/v1/buildings/counts - 获取建筑活动计数（地图气泡）
GET /api/v1/buildings/{id} - 获取建筑详情
```

#### `ClubController`
```java
GET  /api/v1/clubs - 获取社团列表
GET  /api/v1/clubs/{slug} - 获取社团详情
PUT  /api/v1/clubs/{id} - 更新社团信息 [需要CLUB_MANAGER权限]
POST /api/v1/users/follow/club/{id} - 关注社团
DELETE /api/v1/users/follow/club/{id} - 取消关注社团
```

#### `PostController`
```java
POST   /api/v1/posts - 发布新帖子
GET    /api/v1/posts - 获取帖子列表
GET    /api/v1/posts/{id} - 获取帖子详情
DELETE /api/v1/posts/{id} - 删除帖子
POST   /api/v1/posts/{id}/comments - 发布评论
GET    /api/v1/posts/{id}/comments - 获取评论列表
DELETE /api/v1/comments/{id} - 删除评论
```

### 3. MyBatis XML映射文件

需要创建以下XML文件用于复杂查询：

#### `EventsMapper.xml`
```xml
- selectEventListWithFilters - 活动列表复杂筛选查询
  - 多表JOIN（events, buildings, clubs）
  - 动态WHERE条件
  - 时间范围筛选
  - 标签筛选
  - 关键词搜索
  - 排序和分页

- selectEventDetailById - 活动详情关联查询
  - 关联建筑信息
  - 关联组织者信息
  - 关联标签信息
```

#### `BuildingsMapper.xml`
```xml
- selectBuildingEventCounts - 统计各建筑今日活动数量
  - GROUP BY building_id
  - WHERE start_time BETWEEN today
```

### 4. 数据库初始化

#### 建筑数据导入
需要将UTM校园建筑数据导入`buildings`表：
```sql
INSERT INTO buildings (name, code, latitude, longitude, address) VALUES
  ('Communication Culture and Technology Building', 'CCT', 43.5482, -79.6628, '...'),
  ('Instructional Building', 'IB', 43.5485, -79.6632, '...'),
  ('Davis Building', 'DV', 43.5490, -79.6635, '...'),
  ('Recreation Athletic Wellness Centre', 'RAWC', 43.5475, -79.6640, '...'),
  ('Hazel McCallion Academic Learning Centre', 'Library', 43.5488, -79.6630, '...');
```

#### 初始标签数据
```sql
INSERT INTO tags (name, type, color) VALUES
  ('Academic', 'EVENT', '#3B82F6'),
  ('Social', 'EVENT', '#F59E0B'),
  ('Fitness', 'EVENT', '#10B981'),
  ('Workshop', 'EVENT', '#8B5CF6'),
  ('Career', 'EVENT', '#EF4444');
```

---

## 📋 下一步工作清单

### 高优先级
1. ✅ 完成Service层核心业务逻辑实现
2. ✅ 创建剩余Controller（Event, Map, Club, Post）
3. ✅ 编写MyBatis XML复杂查询
4. ✅ 添加Redis缓存逻辑到Service层
5. ✅ 初始化建筑和标签数据

### 中优先级
6. 编写单元测试
7. 完善API文档注释
8. 添加日志记录
9. 性能优化（查询优化、缓存策略）
10. 添加数据库索引

### 低优先级
11. 添加限流功能
12. 添加监控指标
13. Docker化部署配置
14. CI/CD流程配置

---

## 🎯 核心特性总结

### 权限系统（Sa-Token）
- ✅ 基于注解的权限校验 `@CheckRole`
- ✅ AOP切面自动拦截
- ✅ 支持多角色验证（AND/OR）
- ✅ Session存储用户角色

### 缓存策略
- ✅ **Caffeine本地缓存** - 建筑等静态数据（24小时）
- ✅ **Redis分布式缓存** - 活动列表、计数等动态数据（5-60分钟）
- 缓存Key设计：
  - `building:*` - 建筑信息
  - `building:event:count` - 建筑活动计数
  - `event:detail:{id}` - 活动详情
  - `event:list:{filter}` - 活动列表

### API设计规范
- ✅ RESTful风格
- ✅ 统一返回格式 `ResultVO<T>`
- ✅ 统一异常处理
- ✅ Swagger/Knife4j文档
- ✅ 参数校验（Jakarta Validation）

---

## 🔧 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

---

## 📞 后续支持

如需进一步完善：
1. Service层业务逻辑实现
2. Controller层补充
3. 单元测试编写
4. 性能优化建议

请随时提出需求！
