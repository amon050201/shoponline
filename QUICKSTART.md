# 快速启动指南

## 🚀 5分钟快速启动购物系统

### 第一步：检查环境

确保您的计算机已安装：
- ✅ JDK 17 或更高版本
- ✅ Maven 3.6+
- ✅ MySQL 5.7+ 或 8.0+

### 第二步：初始化数据库

1. **启动 MySQL 服务**

   Windows:
   ```powershell
   # 以管理员身份运行 PowerShell
   net start mysql
   ```

2. **执行数据库初始化脚本**

   方法一：使用命令行
   ```bash
   mysql -u root -p < init_database.sql
   ```

   方法二：使用 MySQL 客户端
   - 打开 MySQL Workbench 或其他客户端工具
   - 连接到 MySQL 服务器
   - 打开 `init_database.sql` 文件
   - 执行整个脚本

3. **验证数据库创建成功**
   ```sql
   SHOW DATABASES;  -- 应该看到 demo1_db
   USE demo1_db;
   SHOW TABLES;     -- 应该看到 6 个表
   ```

### 第三步：配置项目

1. **修改数据库配置**

   打开 `src/main/resources/application.properties`，修改以下配置：

   ```properties
   # 将 password 改为您的 MySQL 密码
   spring.datasource.password=你的MySQL密码
   ```

   如果您的 MySQL 用户名不是 root，也需要修改：
   ```properties
   spring.datasource.username=你的用户名
   ```

2. **（可选）修改端口**

   如果 8080 端口被占用：
   ```properties
   server.port=8081
   ```

### 第四步：启动项目

#### 方法一：使用 Maven 命令

在项目根目录打开终端：

```bash
mvn spring-boot:run
```

#### 方法二：使用 IDE

1. 用 IntelliJ IDEA 或 Eclipse 打开项目
2. 找到 `SpringbootloginApplication.java`
3. 右键点击 → Run 'SpringbootloginApplication'

#### 方法三：打包后运行

```bash
# 编译打包
mvn clean package

# 运行 jar 包
java -jar target/springboot-mybatis-login-1.0-SNAPSHOT.jar
```

### 第五步：访问系统

浏览器打开：**http://localhost:8080**

#### 默认账户

- **管理员账户**：admin / 123456
- 或者先注册一个新账户

### 第六步：体验功能

#### 📦 浏览商品
1. 登录后自动跳转到首页
2. 点击"商品列表"查看所有商品
3. 可以按分类筛选或搜索商品

#### 🛒 添加购物车
1. 在商品列表点击"加入购物车"
2. 或在商品详情页选择数量后添加
3. 点击右上角"购物车"查看已选商品

#### 💳 下单购买
1. 在购物车中勾选要购买的商品
2. 点击"去结算"
3. 填写收货信息
4. 提交订单
5. 进行支付（模拟支付）

#### 📋 查看订单
1. 点击"我的订单"
2. 查看所有历史订单
3. 查看订单详情
4. 对待支付订单进行支付或取消

## 🔧 常见问题解决

### 问题1：数据库连接失败

**错误信息**：`Communications link failure`

**解决方案**：
1. 确认 MySQL 服务已启动
2. 检查 `application.properties` 中的用户名和密码
3. 确认数据库 `demo1_db` 已创建

### 问题2：端口被占用

**错误信息**：`Web server failed to start. Port 8080 was already in use.`

**解决方案**：
修改 `application.properties`：
```properties
server.port=8081
```

### 问题3：中文乱码

**解决方案**：
已在配置文件中设置 UTF-8，如仍有问题：
1. 检查数据库字符集：`SHOW CREATE DATABASE demo1_db;`
2. 应该是 `utf8mb4`

### 问题4：找不到页面 404

**解决方案**：
1. 确认项目已成功启动（控制台显示 Started SpringbootloginApplication）
2. 访问正确的路径：http://localhost:8080
3. 清除浏览器缓存后重试

### 问题5：Maven 依赖下载慢

**解决方案**：
配置阿里云镜像，编辑 `~/.m2/settings.xml`：
```xml
<mirrors>
    <mirror>
        <id>aliyun</id>
        <mirrorOf>central</mirrorOf>
        <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
</mirrors>
```

## 📝 测试流程

### 完整购物流程测试

1. **注册/登录**
   - 访问 http://localhost:8080
   - 使用 admin/123456 登录，或注册新账户

2. **浏览商品**
   - 进入商品列表页面
   - 查看 5 个示例商品
   - 尝试分类筛选和搜索功能

3. **添加购物车**
   - 选择任意商品点击"加入购物车"
   - 进入购物车页面查看
   - 修改商品数量
   - 勾选/取消勾选商品

4. **创建订单**
   - 在购物车勾选商品
   - 点击"去结算"
   - 填写收货信息：
     - 收货人：张三
     - 电话：13800138000
     - 地址：北京市朝阳区xxx路xxx号
   - 提交订单

5. **支付订单**
   - 在订单列表找到刚创建的订单
   - 点击"立即支付"
   - 输入支付方式（alipay/wechat/card）
   - 确认支付

6. **查看订单状态**
   - 返回订单列表
   - 确认订单状态已变为"已支付"
   - 查看订单详情

## 🎯 下一步

恭喜！您已经成功启动并测试了购物系统。接下来可以：

1. 📖 阅读 `README_购物系统.md` 了解详细功能
2. 🔍 查看源代码学习架构设计
3. 🎨 自定义页面样式和功能
4. 📊 添加更多商品数据
5. 🚀 部署到服务器

## 📞 需要帮助？

如果遇到其他问题：
1. 检查控制台错误日志
2. 查看 `README_购物系统.md` 的常见问题部分
3. 确认所有配置项都正确设置

---

**祝您使用愉快！** 🎉
