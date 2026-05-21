# 线上购物与交易系统

## 快速启动

### 方式一：直接运行
1. 安装 Java 17+ 和 MySQL 8.0+
2. 创建数据库: 
3. 编辑 application.properties 修改数据库密码
4. 双击 start.bat (Windows) 或 ./start.sh (Mac/Linux)
5. 浏览器打开 http://localhost:8080

### 方式二：Docker


## 默认账号
- 管理员: admin / 123456
- 商家: merchant / 123456 (需自行注册)
- 顾客: 注册即可

## API 接口
- Web 页面: http://localhost:8080/
- 小程序 API: http://localhost:8080/api/
- 小程序项目: ../miniprogram/

## 目录结构
- app.jar - Spring Boot 可执行文件
- application.properties - 配置文件
- schema.sql - 数据库建表参考
- Dockerfile - Docker 镜像构建
- start.bat / start.sh - 启动脚本
