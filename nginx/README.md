# Nginx 配置 - 线上购物与交易系统

## 安装 Nginx

### Windows
1. 下载 Nginx: http://nginx.org/en/download.html (Windows版本)
2. 解压到 `C:\nginx`
3. 复制 `nginx.conf` 到 `C:\nginx\conf\nginx.conf`

### macOS
```bash
brew install nginx
cp nginx.conf /usr/local/etc/nginx/nginx.conf
```

### Linux (Ubuntu/Debian)
```bash
sudo apt install nginx
sudo cp nginx.conf /etc/nginx/nginx.conf
```

## 启动

### Windows
```bash
cd C:\nginx
start nginx
```

### Windows 重载配置
```bash
nginx -s reload
```

### macOS/Linux
```bash
sudo nginx
sudo nginx -s reload
```

## 配置说明

| 功能 | 说明 |
|------|------|
| 反向代理 | `http://localhost` → `http://localhost:8080` |
| 静态资源 | `/css/`, `/js/`, `/images/` 由nginx直接服务，缓存30天 |
| Gzip压缩 | 对HTML/CSS/JS/JSON启用压缩 |
| 限流 | API `/api/` 路径限流 10r/s |
| 安全头 | X-Frame-Options, XSS-Protection 等 |

## 验证

1. 确保后端Spring Boot运行在 `localhost:8080`
2. 启动 Nginx
3. 访问 `http://localhost` 应能看到应用首页
4. 检查响应头应包含 `Content-Encoding: gzip`
