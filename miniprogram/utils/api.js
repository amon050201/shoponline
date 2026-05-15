var SERVER_HOST = '172.21.21.181';  // 局域网手机访问
var SERVER_PORT = '8080';
var BASE_URL = 'http://' + SERVER_HOST + ':' + SERVER_PORT;
function request(url, opts) {
  opts = opts || {};
  return new Promise(function(resolve, reject) {
    wx.request({
      url: BASE_URL + url,
      method: opts.method || 'GET',
      data: opts.data || {},
      timeout: 15000,
      header: Object.assign(
        { 'Content-Type': 'application/json' },
        getApp().globalData.token ? { 'Authorization': 'Bearer ' + getApp().globalData.token } : {}
      ),
      success: function(res) {
        if (res.statusCode === 200) {
          if (res.data && res.data.success !== false) resolve(res.data);
          else reject(res.data || { message: 'fail' });
        } else reject({ message: 'HTTP ' + res.statusCode });
      },
      fail: function(err) {
        var msg = (err && err.errMsg) || 'network error';
        if (msg.indexOf('timeout') >= 0) reject({ message: 'timeout' });
        else reject({ message: msg });
      }
    });
  });
}
function POST(url, data) { return request(url, { method: 'POST', data: data }); }
var api = {
  login: function(u, p) { return POST('/api/auth/login', { username: u, password: p }); },
  register: function(d) { return POST('/api/auth/register', d); },
  getProfile: function() { return request('/api/user/profile'); },
  updateProfile: function(d) { return POST('/api/user/profile/update', d); },
  changePassword: function(d) { return POST('/api/user/password/change', d); },
  logoutApi: function() { return POST('/api/user/logout'); },
  getProducts: function(p, s, c) {
    var u = '/api/customer/products?page=' + (p||1) + '&size=' + (s||20);
    if (c) u += '&categoryId=' + c; return request(u);
  },
  getProductDetail: function(id) { return request('/api/customer/products/' + id); },
  searchProducts: function(kw) { return request('/api/customer/products/search?keyword=' + encodeURIComponent(kw)); },
  getCategories: function(pid) {
    var u = '/api/customer/categories'; if (pid !== undefined) u += '?parentId=' + pid; return request(u);
  },
  getSubcategories: function(id) { return request('/api/customer/categories/' + id + '/subcategories'); },
  getCart: function() { return request('/api/customer/cart'); },
  addToCart: function(pid, qty) { return POST('/api/customer/cart/add', { productId: pid, quantity: qty||1 }); },
  updateCartQty: function(id, qty) { return POST('/api/customer/cart/update-quantity', { id: id, quantity: qty }); },
  updateCartSel: function(id, sel) { return POST('/api/customer/cart/update-selected', { id: id, selected: sel }); },
  removeFromCart: function(id) { return request('/api/customer/cart/' + id, { method: 'DELETE' }); },
  clearCart: function() { return request('/api/customer/cart/clear', { method: 'DELETE' }); },
  getOrders: function() { return request('/api/customer/order'); },
  getOrderDetail: function(id) { return request('/api/customer/order/' + id); },
  createOrder: function(d) { return POST('/order/create', d); },
  payOrder: function(oid, m) { return POST('/api/customer/order/pay/' + oid + '?paymentMethod=' + (m||'alipay')); },
  cancelOrder: function(oid) { return POST('/api/customer/order/cancel/' + oid); },
  confirmReceive: function(oid) { return POST('/api/customer/order/receive/' + oid); },
  getLogistics: function(oid) { return request('/api/customer/order/' + oid + '/logistics'); },
  applyRefund: function(oid) { return POST('/api/customer/order/refund/' + oid); },
  cancelRefund: function(oid) { return POST('/api/customer/order/refund/' + oid + '/cancel'); },
  getAddresses: function() { return request('/api/customer/address'); },
  getDefaultAddr: function() { return request('/api/customer/address/default'); },
  saveAddress: function(d) { return POST('/api/customer/address/save', d); },
  deleteAddress: function(id) { return POST('/api/customer/address/delete/' + id); },
  createPayment: function(oid, m) { return POST('/api/payment/create?orderId=' + oid + '&paymentMethod=' + (m||'alipay')); },
  queryPayStatus: function(ono) { return request('/api/payment/status/' + ono); },
  getFavorites: function() { return request('/api/customer/favorites'); },
  addFav: function(pid) { return POST('/api/customer/favorites/add', { productId: pid }); },
  removeFav: function(id) { return request('/api/customer/favorites/' + id, { method: 'DELETE' }); },
  checkFav: function(pid) { return request('/api/customer/favorites/check/' + pid); },
  getReviews: function(pid) { return request('/api/customer/products/' + pid + '/reviews'); },
  submitReview: function(pid, d) { return POST('/api/customer/products/' + pid + '/review', d); },
  getAvailableCoupons: function() { return request('/api/customer/coupons'); },
  receiveCoupon: function(cid) { return POST('/api/customer/coupons/receive/' + cid); },
  myCoupons: function() { return request('/api/customer/coupons/my'); },
  getNotifications: function(p, s) { return request('/api/customer/notifications?page=' + (p||1) + '&size=' + (s||20)); },
  markRead: function(id) { return POST('/api/customer/notifications/read/' + id); },
  markAllRead: function() { return POST('/api/customer/notifications/read-all'); },
  getBanners: function() { return request('/api/banners'); },
  getTrending: function(n) { return request('/api/recommend/trending?limit=' + (n||10)); },
  getPersonalized: function(n) { return request('/api/recommend/personalized?limit=' + (n||10)); },
  getHotKeywords: function(n) { return request('/api/recommend/hot-keywords?limit=' + (n||10)); },
  getAlsoBought: function(pid) { return request('/api/recommend/also-bought/' + pid); },
  getSimilar: function(pid) { return request('/api/recommend/similar/' + pid); },
  trackBehavior: function(d) { return POST('/api/recommend/track', d); },
  getExchangeList: function() { return request('/exchange/my-orders-json'); },
  createExchange: function(d) { return POST('/exchange/request', d); },
  confirmExchange: function(id) { return POST('/exchange/confirm/' + id); },
  rejectExchange: function(id) { return POST('/exchange/reject/' + id); },
  cancelExchange: function(id) { return POST('/exchange/cancel/' + id); },
  getExchangeValuation: function(pid) { return request('/exchange/valuation/' + pid); },
  aiValuate: function(d) { return POST('/api/ai/valuation/evaluate', d); },
  aiPricePredict: function(pid) { return request('/api/ai/price/predict/' + pid); },
  getSearchHistory: function() { return request('/api/customer/search/history'); },
  clearSearchHistory: function() { return request('/api/customer/search/history', { method: 'DELETE' }); },
  submitFeedback: function(d) { return POST('/api/customer/feedback', d); },
  uploadImage: function(fp) {
    return new Promise(function(resolve, reject) {
      wx.uploadFile({ url: BASE_URL + '/api/upload/image', filePath: fp, name: 'file',
        success: function(r) { try { resolve(JSON.parse(r.data)); } catch(e) { reject(e); } },
        fail: function(e) { reject(e); }
      });
    });
  }
};
module.exports = api;
