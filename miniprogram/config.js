/**
 * 小程序配置文件
 *
 * 使用方式:
 * - 微信开发者工具模拟器: 保持默认 http://localhost:8080
 * - 真机调试/预览: 修改 SERVER_HOST 为电脑的局域网 IP（如 192.168.x.x 或 172.x.x.x）
 *
 * 查看本机IP: 打开命令行输入 ipconfig (Windows) 或 ifconfig (Mac)
 */

// ===== 修改这里的 IP 地址即可切换环境 =====
const SERVER_HOST = '172.21.37.167';
const SERVER_PORT = '8080';

const CONFIG = {
  // 后端服务基础地址
  BASE_URL: `http://${SERVER_HOST}:${SERVER_PORT}`,
  // 请求超时 (ms)
  TIMEOUT: 15000,
  // 是否自动处理 Token 过期
  AUTO_LOGOUT_ON_401: true
};

module.exports = CONFIG;
