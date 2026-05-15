# 线上购物与交易系统 - 项目说明文档

## 📋 项目概述

这是一个基于 Spring Boot + MyBatis + Thymeleaf 开发的完整线上购物与交易系统，实现了商品管理、购物车、订单管理等核心电商功能。

## 🛠️ 技术栈

### 后端技术
- **Spring Boot 3.2.4** - 核心框架
- **MyBatis 3.0.3** - ORM 框架
- **MySQL** - 数据库
- **JWT** - 身份认证
- **Java 17** - 开发语言

### 前端技术
- **Thymeleaf** - 模板引擎
- **HTML5 + CSS3** - 页面结构与样式
- **JavaScript (原生)** - 交互逻辑

## 📁 项目结构

```
src/main/java/com/qzy/springbootlogin/
├── config/                     # 配置类
│   └── DatabaseInitializer.java
├── controller/                 # 控制器层
│   ├── LoginController.java    # 登录注册控制器
│   ├── ProductController.java  # 商品控制器
│   ├── CartController.java     # 购物车控制器
│   └── OrderController.java    # 订单控制器
├── mapper/                     # 数据访问层
│   ├── UserMapper.java         # 用户Mapper
│   ├── ProductMapper.java      # 商品Mapper
│   ├── CartMapper.java         # 购物车Mapper
│   └── OrderMapper.java        # 订单Mapper
├── pojo/                       # 实体类
│   ├── User.java               # 用户实体
│   ├── Category.java           # 分类实体
│   ├── Product.java            # 商品实体
│   ├── Cart.java               # 购物车实体
│   ├── Order.java              # 订单实体
│   ├── OrderItem.java          # 订单项实体
│   └── Result.java             # 统一返回结果
├── service/                    # 业务逻辑层
│   ├── UserService.java
│   ├── ProductService.java
│   ├── CartService.java
│   ├── OrderService.java
│   └── impl/                   # 实现类
│       ├── UserServiceImpl.java
│       ├── ProductServiceImpl.java
│       ├── CartServiceImpl.java
│       └── OrderServiceImpl.java
└── util/                       # 工具类
    ├── EncodingFilter.java     # 编码过滤器
    ├── JwtFilter.java          # JWT过滤器
    └── JwtUtil.java            # JWT工具类

src/main/resources/
├── mapper/                     # MyBatis XML映射文件
│   ├── ProductMapper.xml
│   ├── CartMapper.xml
│   └── OrderMapper.xml
├── templates/pages/            # Thymeleaf页面
│   ├── login.html              # 登录页
│   ├── register.html           # 注册页
│   ├── index.html              # 首页
│   ├── productlist.html        # 商品列表
│   ├── productdetail.html      # 商品详情
│   ├── cart.html               # 购物车
│   ├── orderlist.html          # 订单列表
│   └── orderdetail.html        # 订单详情
└── application.properties      # 配置文件
```

## 🗄️ 数据库设计

### 核心表结构

1. **users（用户表）**
   - id, username, password, email, phone, create_time, update_time

2. **category（商品分类表）**
   - id, name, description, parent_id, sort_order, status, created_time, updated_time

3. **product（商品表）**
   - id, name, description, price, original_price, stock, category_id, image_url, brand, model, status, sales_count, view_count

4. **cart（购物车表）**
   - id, user_id, product_id, quantity, selected, created_time, updated_time

5. **orders（订单表）**
   - id, order_no, user_id, total_amount, actual_amount, shipping_fee, discount_amount, status, payment_method, payment_time, shipping_address, receiver_name, receiver_phone, remark

6. **order_item（订单项表）**
   - id, order_id, product_id, product_name, product_image, price, quantity, total_price

## 🚀 快速开始

### 1. 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 5.7+ 或 8.0+

### 2. 数据库初始化

执行 `init_database.sql` 脚本创建数据库和表：

```bash
mysql -u root -p < init_database.sql
```

或者在 MySQL 客户端中直接执行该脚本。

### 3. 配置数据库连接

修改 `src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/demo1_db?useSSL=false&serverTimezone=UTC&characterEncoding=utf8&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=你的密码
```

### 4. 启动项目

```bash
mvn spring-boot:run
```

或在 IDE 中运行 `SpringbootloginApplication.java`

### 5. 访问系统

浏览器访问：http://localhost:8080

## 📝 功能模块

### 1. 用户模块
- ✅ 用户注册
- ✅ 用户登录
- ✅ JWT 身份认证
- ✅ 会话管理

### 2. 商品模块
- ✅ 商品列表展示
- ✅ 商品详情查看
- ✅ 商品分类筛选
- ✅ 商品搜索
- ✅ 浏览量统计
- ✅ 销量统计

### 3. 购物车模块
- ✅ 添加商品到购物车
- ✅ 修改商品数量
- ✅ 删除购物车项
- ✅ 选中/取消选中商品
- ✅ 全选功能
- ✅ 实时计算总价
- ✅ 清空购物车

### 4. 订单模块
- ✅ 创建订单
- ✅ 订单列表查询
- ✅ 订单详情查看
- ✅ 订单支付（模拟）
- ✅ 取消订单
- ✅ 订单状态管理（待支付、已支付、已发货、已完成、已取消等）
- ✅ 自动生成订单号

## 🔌 API 接口说明

### 商品接口
- `GET /product/list` - 商品列表页面
- `GET /product/detail/{id}` - 商品详情页面
- `GET /product/category/{categoryId}` - 按分类查询商品
- `GET /product/search?keyword=xxx` - 搜索商品
- `POST /product/add` - 添加商品（管理员）
- `POST /product/update` - 更新商品（管理员）
- `DELETE /product/delete/{id}` - 删除商品（管理员）

### 购物车接口
- `GET /cart/view` - 购物车页面
- `GET /cart/data` - 获取购物车数据（AJAX）
- `POST /cart/add` - 添加到购物车
- `POST /cart/updateQuantity` - 更新数量
- `POST /cart/updateSelected` - 更新选中状态
- `DELETE /cart/delete/{id}` - 删除购物车项
- `DELETE /cart/clear` - 清空购物车

### 订单接口
- `GET /order/list` - 订单列表页面
- `GET /order/detail/{id}` - 订单详情页面
- `POST /order/confirm` - 确认订单
- `POST /order/create` - 创建订单
- `POST /order/pay/{orderId}` - 支付订单
- `POST /order/cancel/{orderId}` - 取消订单

## 💡 核心特性

### 1. 事务管理
订单创建使用 `@Transactional` 注解保证数据一致性：
- 创建订单记录
- 创建订单项
- 扣减库存
- 任一环节失败自动回滚

### 2. 库存管理
- 添加订单时自动扣减库存
- 库存不足时抛出异常并回滚
- 支付成功后增加销量统计

### 3. 订单号生成
采用时间戳 + 随机数的方式生成唯一订单号：
```
格式：yyyyMMddHHmmss + 4位随机数
示例：202401011205301234
```

### 4. 购物车智能合并
- 重复添加同一商品时自动合并数量
- 支持单独选中/取消商品
- 实时计算选中商品总价

## 🎨 页面说明

### 主要页面
1. **登录页** (`/pages/login.html`) - 用户登录入口
2. **注册页** (`/pages/register.html`) - 新用户注册
3. **商品列表** (`/product/list`) - 浏览所有商品，支持分类筛选和搜索
4. **商品详情** (`/product/detail/{id}`) - 查看商品详细信息
5. **购物车** (`/cart/view`) - 管理购物车商品
6. **订单列表** (`/order/list`) - 查看历史订单
7. **订单详情** (`/order/detail/{id}`) - 查看订单详细信息

## 🔐 安全机制

1. **JWT 认证** - 基于 Token 的身份验证
2. **会话管理** - HttpSession 存储用户信息
3. **权限控制** - 未登录用户自动跳转登录页
4. **SQL 防注入** - MyBatis 参数化查询

## 📊 示例数据

系统初始化时会自动插入：
- 1个管理员账户：admin / 123456
- 10个商品分类（5个一级分类 + 5个二级分类）
- 5个示例商品

## 🐛 常见问题

### 1. 数据库连接失败
检查 `application.properties` 中的数据库配置是否正确，确保 MySQL 服务已启动。

### 2. 端口冲突
如果 8080 端口被占用，修改 `application.properties`：
```properties
server.port=8081
```

### 3. 静态资源404
确保图片等资源放在 `src/main/resources/static/` 目录下。

### 4. 中文乱码
已在配置文件中设置 UTF-8 编码，如仍有问题检查数据库字符集是否为 utf8mb4。

## 🔄 后续扩展建议

1. **支付集成** - 接入支付宝、微信支付真实接口
2. **物流跟踪** - 集成快递查询 API
3. **评价系统** - 商品评价和评分功能
4. **优惠券** - 促销优惠活动管理
5. **库存预警** - 低库存自动提醒
6. **数据统计** - 销售数据分析和报表
7. **消息通知** - 订单状态变更短信/邮件通知
8. **推荐系统** - 基于用户行为的商品推荐
9. **秒杀功能** - 限时抢购活动
10. **多端适配** - 开发移动端 APP 或小程序

## 📄 许可证

本项目仅供学习参考使用。

## 👥 联系方式

如有问题或建议，欢迎反馈！

---

**祝您使用愉快！** 🎉
