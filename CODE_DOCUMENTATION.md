# 线上购物与交易系统 — 代码文档

> Spring Boot 3.2.12 · Java 21 · MyBatis 3.0.3 · MySQL · Thymeleaf 3.1.5

---

## 一、项目概览

### 1.1 技术栈

| 层面 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.2.12 |
| 持久层 | MyBatis 3.0.3 + JDBC Template |
| 数据库 | MySQL |
| 模板引擎 | Thymeleaf 3.1.5 |
| 认证 | JWT (HMAC-SHA256) + Session |
| 密码加密 | BCrypt |
| 消息队列 | RabbitMQ (可选) |
| 搜索引擎 | Elasticsearch (可选) |
| AI 平台 | 通义千问 / 文心一言 |
| 缓存 | Redis / Simple Cache |
| 构建工具 | Maven |

### 1.2 项目结构

```
src/main/java/com/qzy/springbootlogin/
├── SpringbootloginApplication.java    # 入口
├── config/                             # 配置层 (8文件)
├── controller/                         # 主控制器 (11文件)
├── service/                            # 服务接口 (7个)
├── service/impl/                       # 服务实现 (7个)
├── mapper/                             # MyBatis 数据层 (8个)
├── pojo/                               # 实体类 (17个)
├── util/                               # 工具类 (9个)
├── module/
│   ├── admin/controller/               # 管理员 API (2个)
│   ├── customer/controller/            # 客户端 API (16个)
│   ├── exchange/controller/            # 交换模块 API (1个)
│   └── merchant/controller/            # 商家模块 API (1个)
└── ai/                                 # AI 模块
    ├── config/                         #   AI 配置
    ├── controller/                     #   AI 控制器 (4个)
    ├── service/                        #   AI 服务接口 (7个)
    ├── service/impl/                   #   AI 服务实现 (10个)
    ├── mapper/                         #   AI 数据层 (3个)
    └── pojo/                           #   AI 实体 (9个)

src/main/resources/
├── templates/                          # Thymeleaf 模板 (28个)
├── static/css/                         # 样式表
├── mapper/                             # MyBatis XML (4个)
└── application.properties              # 主配置
```

---

## 二、架构设计

### 2.1 分层架构

```
Controller (@Controller/@RestController)
    ↓
Service (接口 + @Service 实现)
    ↓
Mapper (@Mapper 注解 + XML)
    ↓
MySQL / Elasticsearch / Redis / RabbitMQ
```

### 2.2 角色权限

| 角色 | roleType | 权限 |
|------|----------|------|
| 顾客 | 0 | 浏览商品、购物、以物易物 |
| 商家 | 1 | 发布商品、管理订单、发货 |
| 管理员 | 2 | 管理用户/商品/日志、查看统计 |

权限控制:
- `RoleInterceptor` 拦截 `/admin/**` (需 roleType=2) 和 `/merchant/**` (需 roleType≥1)
- `JwtFilter` 对 API 路径提取 Bearer Token 设置 Session

### 2.3 认证流程

```
浏览器 → Session 认证 → 页面
小程序/API → JWT Bearer Token → JwtFilter → Session → Controller
公开路径: / /login /register /captcha /css/ /js/ /images/
```

---

## 三、数据库表 (20张)

| 表名 | 说明 | 关键字段 |
|------|------|---------|
| users | 用户 | id, username, password_hash, role_type(0/1/2), status |
| category | 分类 | id, name, parent_id |
| product | 商品 | id, name, price, stock, category_id, merchant_id, status |
| cart | 购物车 | id, user_id, product_id, quantity, selected |
| orders | 订单 | id, order_no, user_id, status(0-4) |
| order_item | 订单项 | id, order_id, product_id, price, quantity |
| exchange_order | 交换订单 | id, order_no, initiator_id, receiver_id, status |
| address | 地址 | id, user_id, receiver_name, is_default |
| user_behavior | 行为 | id, user_id, action(view/search/add_cart/purchase) |
| price_history | 价格历史 | id, product_id, price, recorded_at |
| fraud_alert | 欺诈告警 | id, user_id, alert_type, risk_score |
| exchange_valuation | 估值缓存 | id, product_id, ai_estimated_value |
| favorite | 收藏 | id, user_id, product_id |
| review | 评价 | id, user_id, product_id, rating, content |
| coupon | 优惠券 | id, name, type(fixed/discount), value |
| user_coupon | 用户券 | id, user_id, coupon_id, status |
| notification | 通知 | id, user_id, title, is_read |
| banner | Banner | id, title, image_url, link_url |
| search_history | 搜索 | id, user_id, keyword |
| feedback | 反馈 | id, user_id, content, status, reply |
| operation_log | 日志 | id, user_id, module, operation, result |

---

## 四、完整 API 列表

### 4.1 认证

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/login` | 登录 (captcha 必填) |
| POST | `/register` | 注册 (captcha 必填) |
| POST | `/api/auth/login` | API 登录 (JSON) |
| POST | `/api/auth/register` | API 注册 |
| GET | `/captcha` | 验证码图片 |
| GET | `/logout` | 退出 |

### 4.2 管理后台

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/admin/dashboard` | 仪表盘数据 |
| GET | `/api/admin/users` | 用户列表 (page, pageSize, keyword, roleType) |
| GET | `/api/admin/users/{id}` | 用户详情 |
| PUT | `/api/admin/users/{id}` | 更新用户 |
| PUT | `/api/admin/users/{id}/role` | 改角色 |
| PUT | `/api/admin/users/{id}/status` | 启用/禁用 |
| POST | `/api/admin/users` | 添加用户 |
| DELETE | `/api/admin/users/{id}` | 删除用户 |
| POST | `/api/admin/users/batch-delete` | 批量删除 |
| GET | `/admin/api/products` | 商品列表 (page, pageSize, keyword) |
| POST | `/admin/api/products` | 添加商品 |
| PUT | `/admin/api/products/{id}` | 更新商品 |
| DELETE | `/admin/api/products/{id}` | 删除商品 |
| GET | `/admin/api/logs` | 操作日志 (page, keyword, module, dateStart, dateEnd) |
| DELETE | `/admin/api/logs/clean?days=90` | 清理日志 |
| GET | `/api/admin/fraud/alerts` | 欺诈告警 |
| POST | `/api/admin/fraud/resolve/{id}` | 处理告警 |

### 4.3 商品

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/product/list` | 商品列表页 |
| GET | `/product/detail/{id}` | 详情页 |
| GET | `/product/category/{id}` | 按分类 |
| GET | `/product/search?keyword=` | 搜索 |
| GET | `/product/api/search?keyword=` | 搜索 API |

### 4.4 购物车

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/cart/view` | 页面 |
| GET | `/cart/data` | JSON 数据 |
| POST | `/cart/add` | 添加 |
| POST | `/cart/updateQuantity` | 改数量 |
| DELETE | `/cart/delete/{id}` | 删除 |
| DELETE | `/cart/clear` | 清空 |

### 4.5 订单

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/order/list` | 列表页 |
| GET | `/order/detail/{id}` | 详情页 |
| POST | `/order/create` | 创建 |
| POST | `/order/pay/{id}` | 支付 |
| POST | `/order/cancel/{id}` | 取消 |
| POST | `/order/receive/{id}` | 收货 |

### 4.6 以物易物

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/exchange/market` | 市场页 |
| GET | `/exchange/my-orders` | 我的交换 |
| POST | `/exchange/request` | 发起交换 |
| POST | `/exchange/confirm/{id}` | 确认 |
| POST | `/exchange/reject/{id}` | 拒绝 |
| POST | `/exchange/cancel/{id}` | 取消 |
| POST | `/exchange/complete/{id}` | 完成 |
| GET | `/exchange/valuation/{productId}` | AI 估值 |

### 4.7 AI 模块

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/ai/valuation/evaluate` | 商品估值 |
| GET | `/api/ai/price/predict/{productId}` | 价格预测 |
| GET | `/api/ai/price/history/{productId}` | 价格历史 |
| POST | `/api/ai/image/recognize` | 图片识别 |
| POST | `/api/ai/image/search` | 以图搜商品 |

### 4.8 其他

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/analytics/sales-trend?days=30` | 销售趋势 |
| GET | `/api/analytics/behavior-stats` | 行为统计 |
| GET | `/api/analytics/category-distribution` | 分类分布 |
| GET | `/api/recommend/personalized` | 个性化推荐 |
| GET | `/api/recommend/trending` | 热销商品 |
| POST | `/api/recommend/track` | 行为追踪 |

---

## 五、工具类说明

### JwtUtil — JWT 令牌

```java
JwtUtil.generateToken("admin");                      // 生成
JwtUtil.generateToken("admin", 1L, 2);               // 含 userId+roleType
JwtUtil.validateToken(token);                         // 验证
JwtUtil.getUsernameFromToken(token);                   // 解用户名
JwtUtil.getUserIdFromToken(token);                     // 解用户ID
JwtUtil.getRoleTypeFromToken(token);                   // 解角色
```
Token 有效期: **24 小时**

### PasswordUtil — 密码

```java
PasswordUtil.hash("123456");          // BCrypt 加密
PasswordUtil.matches("123456", hash); // 验证
```

### AdminOperation — 操作日志注解

```java
@AdminOperation(value = "查看用户列表", module = "用户管理")
```
配合 `OperationLogAspect` 切面，自动记录操作人、模块、方法、参数、IP、耗时、结果到 `operation_log` 表。

### CaptchaUtil — 验证码

生成 120×40 PNG，4 位随机字符，含干扰线 + 噪点。

---

## 六、AI 模块架构

### 服务商选择

```
AiProviderSelector
├── TongyiAiProvider (通义千问)
│   ├── qwen-plus (文本)
│   └── qwen-vl-plus (视觉)
└── WenxinAiProvider (文心一言)
    └── ernie-4.0-8k (文本)
```

### 降级策略

| 功能 | AI 方式 | 降级方式 |
|------|--------|---------|
| 商品估值 | LLM 评估 JSON | 统计法: 成色系数×类目均价 |
| 价格预测 | 线性回归 | 取7天均价 |
| 欺诈检测 | 规则引擎 | 无降级 |
| 图片识别 | Vision LLM | 无降级 |
| 商品搜索 | Elasticsearch | MySQL LIKE |

---

## 七、页面路由

### 前台

| 路径 | 页面 |
|------|------|
| `/` | 首页 |
| `/login` | 登录 |
| `/register` | 注册 |
| `/product/list` | 商品列表 |
| `/product/detail/{id}` | 商品详情 |
| `/cart/view` | 购物车 |
| `/order/list` | 订单列表 |
| `/order/detail/{id}` | 订单详情 |
| `/exchange/market` | 交换市场 |
| `/address/list` | 地址管理 |

### 管理后台

| 路径 | 页面 | 功能 |
|------|------|------|
| `/admin/dashboard` | 控制台 | 统计卡片 + 快捷操作 |
| `/admin/users` | 用户管理 | 搜索/分页/增删改/角色/状态 |
| `/admin/products` | 商品管理 | 搜索/分页/添加/编辑/删除 |
| `/admin/logs` | 操作日志 | 搜索/模块/日期/清理 |

### 商家中心

| 路径 | 页面 |
|------|------|
| `/merchant/dashboard` | 控制台 |
| `/merchant/product/add` | 添加商品 |
| `/merchant/product/edit/{id}` | 编辑商品 |

---

## 八、部署

### 方式一: Docker

```bash
docker build -t shoponline .
docker run -p 8080:8080 -e SPRING_DATASOURCE_URL=... shoponline
```

### 方式二: Railway

```bash
railway up --service app
```

### 默认管理员

- 用户名: `admin`
- 密码: `123456`

### 关键配置

```properties
# 数据库
spring.datasource.url=jdbc:mysql://localhost:3306/demo1_db
spring.datasource.username=root
spring.datasource.password=root123

# AI
ai.active-provider=tongyi
ai.tongyi.api-key=your-key

# 可选服务 (默认关闭)
spring.elasticsearch.enabled=false
spring.rabbitmq.auto-startup=false
spring.cache.type=simple
```

---

## 九、Java 源文件完整清单

### config/ (8个)
| 类 | 说明 |
|----|------|
| `AsyncConfig` | 异步线程池 (4核心/8最大) |
| `DatabaseInitializer` | 启动建表 + 种子数据 |
| `ElasticsearchConfig` | ES 条件配置 |
| `ImageFallbackController` | 图片降级服务 |
| `RabbitMQConfig` | 消息队列 + 3队列绑定 |
| `RedisConfig` | Redis 缓存配置 |
| `WebConfig` | 角色拦截器注册 |
| `WebMvcConfig` | CORS 跨域配置 |

### controller/ (11个)
| 类 | 路径 | 说明 |
|----|------|------|
| `LoginController` | `/` | 登录/注册/首页/个人中心 |
| `ProductController` | `/product` | 商品浏览/搜索/CRUD |
| `CartController` | `/cart` | 购物车 |
| `OrderController` | `/order` | 订单管理 |
| `AdminController` | `/admin` | 管理页面+商品/日志API |
| `CaptchaController` | (root) | 验证码生成 |
| `ExchangeController` | `/exchange` | 以物易物 |
| `AddressWebController` | `/address` | 地址管理 |
| `AnalyticsController` | `/api/analytics` | 数据统计 |
| `MerchantController` | `/merchant` | 商家页面+商品CRUD |
| `RecommendationController` | `/api/recommend` | 推荐引擎 |

### service/ (7接口 + 7实现)
| 接口 | 实现 | 说明 |
|------|------|------|
| `UserService` | `UserServiceImpl` | 用户业务 (BCrypt+JWT) |
| `ProductService` | `ProductServiceImpl` | 商品业务 (@Cacheable) |
| `CartService` | `CartServiceImpl` | 购物车业务 |
| `OrderService` | `OrderServiceImpl` | 订单业务 (@Transactional+RabbitMQ) |
| `AddressService` | `AddressServiceImpl` | 地址业务 |
| `AnalyticsService` | `AnalyticsServiceImpl` | 统计 (JdbcTemplate) |
| `RecommendationService` | `RecommendationServiceImpl` | 推荐 (协同+内容) |

### mapper/ (8个)
| 类 | SQL 方式 | 说明 |
|----|---------|------|
| `UserMapper` | 注解 | 用户 CRUD + 分页搜索 |
| `ProductMapper` | XML | 商品 CRUD + 智能搜索 |
| `CartMapper` | XML | 购物车 (JOIN product) |
| `OrderMapper` | XML | 订单 + 订单项 |
| `AddressMapper` | XML | 地址 CRUD |
| `BehaviorMapper` | 注解 | 用户行为 + 推荐查询 |
| `ExchangeOrderMapper` | 注解 | 交换订单 |
| `OperationLogMapper` | 注解 | 操作日志 + 搜索/清理 |

### pojo/ (17个)
`Result` `User` `UserRole` `Product` `Category` `Cart` `Order` `OrderItem` `ExchangeOrder` `Address` `UserBehavior` `OperationLog` `Review` `Favorite` `Coupon` `UserCoupon` `Notification`

### util/ (9个)
| 类 | 说明 |
|----|------|
| `JwtUtil` | JWT 生成/验证/解析 |
| `PasswordUtil` | BCrypt 加密/匹配 |
| `CaptchaUtil` | 验证码图片生成 |
| `JwtFilter` | JWT 过滤器 |
| `EncodingFilter` | UTF-8 编码过滤器 |
| `RoleInterceptor` | 角色权限拦截 |
| `AdminOperation` | 操作日志注解 |
| `OperationLogAspect` | 操作日志 AOP 切面 |

### module/ (20个 Controller)
- `admin/controller/` (2): `AdminDashboardController`, `AdminUserController`
- `customer/controller/` (16): 用户/商品/订单/购物车/地址/收藏/评价/通知/优惠券/搜索历史/退款/物流/反馈/Banner/支付/上传
- `exchange/controller/` (1): `ExchangeApiController`
- `merchant/controller/` (1): `MerchantProductController`

### ai/ (完整子模块)
- **config**: `AiProviderConfig`
- **controller** (4): 估值/价格预测/欺诈检测/图片识别
- **service** (7接口): AiProviderService, SmartValuationService, FraudDetectionService, PricePredictionService, ImageRecognitionService, ProductSearchService, PaymentService
- **service/impl** (10): 通义/文心实现, 估值/检测/预测/识别/搜索/支付/Demo/降级
- **mapper** (3): FraudAlertMapper, PriceHistoryMapper, ProductSearchRepository
- **pojo** (9): 估值请求/报告, 价格历史/预测, 欺诈告警, 图片识别, 支付结果, 商品文档(ES)

---

## 十、模板文件清单 (28个HTML)

**公共**: `fragments/admin-layout.html`

**错误**: `error.html` `error/404.html` `error/500.html`

**前台** (15): `index` `login` `register` `profile` `cart` `orderconfirm` `orderdetail` `orderlist` `productdetail` `productlist` `payment` `userlist` `adduser` `edituser` `test`

**管理后台** (4): `admin/dashboard` `admin/users` `admin/products` `admin/logs`

**商家** (3): `merchant/dashboard` `merchant/add-product` `merchant/edit-product`

**交换** (3): `exchange/market` `exchange/my-orders` `exchange/request`

**地址** (2): `address/list` `address/edit`

---

*文档生成: 2026-05-29*
