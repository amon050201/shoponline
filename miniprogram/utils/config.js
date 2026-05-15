// === 切换环境：只改下面这一行 ===
// '127.0.0.1' = 本机开发； '172.25.16.1' = 局域网
var SERVER_HOST = '127.0.0.1';
var SERVER_PORT = '8080';

module.exports = {
  BASE_URL: 'http://' + SERVER_HOST + ':' + SERVER_PORT,
  TIMEOUT: 15000
};
