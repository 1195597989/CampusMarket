# 校园二手交易平台 (CampusMarket)

## 项目简介

CampusMarket是一个专为大学生打造的校园二手交易平台，提供简洁易用的商品发布、浏览、搜索和留言功能。

## 技术栈

- **后端**: Spring Boot 3.5.0 + Spring Security + Spring Data JPA
- **数据库**: MySQL 8.0
- **认证**: JWT Token
- **文档**: Swagger/OpenAPI 3
- **构建工具**: Maven

## 功能特性

### 用户管理
- 用户注册/登录
- JWT Token认证
- 密码修改
- 个人信息管理

### 商品管理
- 商品发布（支持图片上传）
- 商品浏览（分页展示）
- 商品搜索（按标题）
- 商品分类筛选
- 我的商品管理
- 商品状态管理（在售/已售出/已下架）

### 交易功能
- 商品留言功能
- 留言管理
- 商品收藏（待实现）

## 快速开始

### 环境要求
- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 数据库配置

#### 方式一：自动创建（推荐）
1. 确保MySQL服务正在运行
2. 运行项目提供的SQL脚本：
```bash
mysql -u root -p < database_init.sql
```

#### 方式二：手动创建
1. 创建数据库：
```sql
CREATE DATABASE campus_market CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 运行简化脚本：
```bash
mysql -u root -p campus_market < database_simple.sql
```

3. 修改 `application.properties` 中的数据库配置：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campus_market?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 运行项目

1. 克隆项目：
```bash
git clone <your-repo-url>
cd CampusMarket
```

2. 安装依赖：
```bash
mvn clean install
```

3. 运行项目：
```bash
mvn spring-boot:run
```

4. 访问应用：
- API根路径: http://localhost:8080
- Swagger文档: http://localhost:8080/swagger-ui/index.html

## API文档

### 认证接口

#### 用户注册
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456",
  "email": "test@example.com",
  "phone": "13888888888"
}
```

#### 用户登录
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456"
}
```

### 商品接口

#### 发布商品
```
POST /api/products
Authorization: Bearer <jwt_token>
Content-Type: multipart/form-data

title: "商品标题"
description: "商品描述"
price: 100.00
category: "数码产品"
image: <文件>
```

#### 获取商品列表
```
GET /api/products?page=0&size=10&category=数码产品
```

#### 搜索商品
```
GET /api/products/search?keyword=手机&page=0&size=10
```

#### 获取商品详情
```
GET /api/products/{id}
```

### 留言接口

#### 发送留言
```
POST /api/messages/product/{productId}
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "content": "这个商品还在吗？"
}
```

#### 获取商品留言
```
GET /api/messages/product/{productId}
```

## 商品分类

系统预设的商品分类包括：
- 数码产品
- 图书教材
- 生活用品
- 服装配饰
- 运动器材
- 学习用品
- 其他

## 文件上传

- 支持的图片格式：jpg, jpeg, png, gif
- 最大文件大小：2MB
- 上传路径：/uploads/products/

## 部署说明

### 本地部署
1. 确保MySQL服务运行
2. 配置数据库连接信息
3. 运行 `mvn spring-boot:run`

### 生产环境部署
1. 修改 `application.properties` 中的生产环境配置
2. 打包项目：`mvn clean package`
3. 运行jar文件：`java -jar target/CampusMarket-0.0.1-SNAPSHOT.jar`

## 项目结构

```
src/main/java/org/example/campusmarket/
├── config/          # 配置类
├── controller/      # 控制器层
├── dto/            # 数据传输对象
├── entity/         # 实体类
├── repository/     # 数据访问层
├── security/       # 安全配置
├── service/        # 业务逻辑层
└── util/           # 工具类
```

## 开发规范

### 代码规范
- 使用统一的代码格式化规则
- 遵循RESTful API设计原则
- 添加适当的注释和文档

### 提交规范
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 代码重构
- test: 测试相关

## 待实现功能

- [ ] 商品收藏功能
- [ ] 用户评价系统
- [ ] 站内消息通知
- [ ] 商品举报功能
- [ ] 数据统计面板
- [ ] 邮件通知功能

## 贡献指南

1. Fork 本仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

如有问题或建议，请通过以下方式联系：
- Email: your-email@example.com
- GitHub Issues: [项目Issues页面]

---

**注意**: 这是一个学习项目，请勿用于商业用途。在生产环境使用前，请确保进行充分的安全性测试和代码审查。
