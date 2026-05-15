import os
base = r"c:\ideaProject\demo1\miniprogram"

def W(rel, s):
    p = os.path.join(base, rel)
    os.makedirs(os.path.dirname(p), exist_ok=True)
    with open(p, 'w', encoding='utf-8') as f:
        f.write(s.strip() + '\n')

API = '''const BASE_URL = 'http://localhost:8080';
function request(url, opts) {
  opts = opts || {};
  return new Promise(function(resolve, reject) {
    wx.request({
      url: BASE_URL + url, method: opts.method || 'GET', data: opts.data || {},
      header: { 'Content-Type': 'application/json' },
      success: function(res) {
        if (res.statusCode === 200) {
          if (res.data && res.data.success !== false) resolve(res.data);
          else reject(res.data || { message: 'fail' });
        } else reject({ message: 'HTTP ' + res.statusCode });
      },
      fail: function(err) { reject({ message: err.errMsg || 'error' }); }
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
module.exports = api;'''

W("utils/api.js", API)
print("api.js OK")

# Page definitions: (name, js, wxml)
PAGES = []

def p(name, js, wxml):
    PAGES.append((name, js.strip(), wxml.strip()))

p("index",
"var api = require('../../utils/api');\nPage({data:{banners:[],categories:[],trending:[],personalized:[],hotKeywords:[]},onLoad:function(){this.loadData();},loadData:function(){var t=this;api.getBanners().then(function(r){t.setData({banners:r.data||[]});}).catch(function(){});api.getCategories().then(function(r){var c=r.data||[];t.setData({categories:c.filter(function(x){return x.parentId===0;})});}).catch(function(){});api.getTrending().then(function(r){t.setData({trending:r.data||[]});}).catch(function(){});api.getPersonalized().then(function(r){t.setData({personalized:r.data||[]});}).catch(function(){});api.getHotKeywords(12).then(function(r){t.setData({hotKeywords:r.data||[]});}).catch(function(){});},goSearch:function(){wx.navigateTo({url:'/pages/search/search'});},goProduct:function(e){wx.navigateTo({url:'/pages/product-detail/product-detail?id='+e.currentTarget.dataset.id});},goKeyword:function(e){wx.navigateTo({url:'/pages/search/search?keyword='+encodeURIComponent(e.currentTarget.dataset.kw)});}});",
"<view class='page-wrap'><view class='search-bar'><input placeholder='搜索商品' bindtap='goSearch' disabled/><button bindtap='goSearch'>搜索</button></view><view class='banner-wrap' wx:if='{{banners.length>0}}'><swiper autoplay circular indicator-dots><swiper-item wx:for='{{banners}}' wx:key='id'><image src='{{item.imageUrl}}' mode='aspectFill'/></swiper-item></swiper></view><view class='section-title'>商品分类</view><view style='display:flex;flex-wrap:wrap;padding:0 24rpx;'><view wx:for='{{categories}}' wx:key='id' style='width:25%;text-align:center;padding:16rpx 0;'><view style='font-size:44rpx;'>{{item.icon||'📦'}}</view><view style='font-size:24rpx;'>{{item.name}}</view></view></view><view class='section-title' wx:if='{{trending.length>0}}'>热门推荐</view><scroll-view scroll-x class='recommend-scroll' wx:if='{{trending.length>0}}'><view wx:for='{{trending}}' wx:key='id' class='recommend-item' data-id='{{item.id}}' bindtap='goProduct'><image src='{{item.imageUrl||item.image}}' mode='aspectFill'/><view class='name'>{{item.name}}</view><view class='price'>¥{{item.price}}</view></view></scroll-view><view class='section-title' wx:if='{{hotKeywords.length>0}}'>热门搜索</view><view style='display:flex;flex-wrap:wrap;padding:0 24rpx 40rpx;' wx:if='{{hotKeywords.length>0}}'><view wx:for='{{hotKeywords}}' wx:key='keyword' data-kw='{{item.keyword}}' bindtap='goKeyword' style='padding:8rpx 24rpx;margin:6rpx;background:rgba(102,126,234,0.08);border-radius:32rpx;font-size:24rpx;color:#667eea;'>{{item.keyword}}</view></view></view>")

p("category",
"var api=require('../../utils/api');Page({data:{parentCategories:[],currentParent:0,subCategories:[],products:[],page:1,loading:false},onLoad:function(){var t=this;api.getCategories().then(function(r){var c=r.data||[];t.setData({parentCategories:c.filter(function(x){return x.parentId===0;})});}).catch(function(){});this.loadProducts();},switchParent:function(e){var id=e.currentTarget.dataset.id;this.setData({currentParent:id,page:1,products:[]});var t=this;api.getSubcategories(id).then(function(r){t.setData({subCategories:r.data||[]});}).catch(function(){});this.loadProducts(id);},loadProducts:function(cid){var t=this;if(t.data.loading)return;t.setData({loading:true});api.getProducts(t.data.page,20,cid).then(function(r){var d=r.data||{};t.setData({products:d.list||d||[],loading:false});}).catch(function(){t.setData({loading:false});});},goProduct:function(e){wx.navigateTo({url:'/pages/product-detail/product-detail?id='+e.currentTarget.dataset.id});}});",
"<view class='page-wrap'><scroll-view scroll-x style='white-space:nowrap;padding:16rpx;background:#fff;'><view wx:for='{{parentCategories}}' wx:key='id' data-id='{{item.id}}' bindtap='switchParent' style='display:inline-block;padding:12rpx 28rpx;margin-right:12rpx;border-radius:32rpx;font-size:26rpx;{{currentParent===item.id?\"background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;\":\"background:#f5f5f5;color:#333;\"}}'>{{item.name}}</view></scroll-view><view wx:if='{{subCategories.length>0}}' style='display:flex;flex-wrap:wrap;padding:16rpx 24rpx;background:#fff;'><view wx:for='{{subCategories}}' wx:key='id' data-id='{{item.id}}' bindtap='switchParent' style='padding:10rpx 24rpx;margin:6rpx;background:#f5f5f5;border-radius:8rpx;font-size:24rpx;'>{{item.name}}</view></view><view class='product-grid' style='padding-top:16rpx;'><view wx:for='{{products}}' wx:key='id' class='product-card' data-id='{{item.id}}' bindtap='goProduct'><image src='{{item.imageUrl}}' mode='aspectFill'/><view class='info'><view class='name'>{{item.name}}</view><view class='price'>¥{{item.price}}</view></view></view></view><view wx:if='{{products.length===0&&!loading}}' class='empty-state'><text>暂无商品</text></view></view>")

p("cart",
"var api=require('../../utils/api');Page({data:{cartItems:[],totalPrice:0,allSelected:true},onShow:function(){this.loadCart();},loadCart:function(){var t=this;api.getCart().then(function(r){var items=r.data||[];t.setData({cartItems:items});t.calcTotal(items);}).catch(function(){});},calcTotal:function(items){var total=0,allSel=true;(items||[]).forEach(function(i){if(i.selected!==0)total+=(i.productPrice||0)*(i.quantity||1);else allSel=false;});this.setData({totalPrice:total.toFixed(2),allSelected:allSel});},toggleSelect:function(e){var id=e.currentTarget.dataset.id,sel=e.detail.value?1:0;var t=this;api.updateCartSel(id,sel).then(function(){t.loadCart();});},addQty:function(e){var item=e.currentTarget.dataset.item;var t=this;api.updateCartQty(item.id,(item.quantity||1)+1).then(function(){t.loadCart();});},subQty:function(e){var item=e.currentTarget.dataset.item,q=(item.quantity||1)-1;if(q<1)return;var t=this;api.updateCartQty(item.id,q).then(function(){t.loadCart();});},removeItem:function(e){var id=e.currentTarget.dataset.id;var t=this;wx.showModal({title:'提示',content:'确定删除？',success:function(r){if(r.confirm)api.removeFromCart(id).then(function(){t.loadCart();});}});},goCheckout:function(){var items=this.data.cartItems.filter(function(i){return i.selected!==0;});if(items.length===0){wx.showToast({title:'请选择商品',icon:'none'});return;}wx.navigateTo({url:'/pages/checkout/checkout'});},goProduct:function(e){wx.navigateTo({url:'/pages/product-detail/product-detail?id='+e.currentTarget.dataset.id});}});",
"<view class='page-wrap'><view wx:if='{{cartItems.length===0}}' class='empty-state'><text>购物车是空的</text><button class='btn-primary' style='width:300rpx;margin-top:30rpx;' bindtap='goShop'>去逛逛</button></view><block wx:for='{{cartItems}}' wx:key='id'><view class='card' style='display:flex;align-items:center;gap:16rpx;'><checkbox checked='{{item.selected!==0}}' data-id='{{item.id}}' bindchange='toggleSelect'/><image src='{{item.productImage}}' mode='aspectFill' style='width:120rpx;height:120rpx;border-radius:8rpx;background:#f0f0f0;' data-id='{{item.productId}}' bindtap='goProduct'/><view style='flex:1;'><view style='font-size:28rpx;font-weight:600;'>{{item.productName}}</view><view style='color:#e74c3c;font-size:30rpx;font-weight:bold;margin-top:8rpx;'>¥{{item.productPrice}}</view></view><view style='display:flex;align-items:center;gap:4rpx;'><view style='width:48rpx;height:48rpx;line-height:48rpx;text-align:center;background:#f5f5f5;border-radius:8rpx;' data-item='{{item}}' bindtap='subQty'>-</view><text style='width:60rpx;text-align:center;'>{{item.quantity||1}}</text><view style='width:48rpx;height:48rpx;line-height:48rpx;text-align:center;background:#f5f5f5;border-radius:8rpx;' data-item='{{item}}' bindtap='addQty'>+</view></view><view data-id='{{item.id}}' bindtap='removeItem' style='color:#ff6b6b;font-size:24rpx;'>删除</view></view></block><view wx:if='{{cartItems.length>0}}' style='position:fixed;bottom:0;left:0;right:0;background:#fff;padding:20rpx 30rpx;display:flex;align-items:center;justify-content:space-between;box-shadow:0 -2rpx 10rpx rgba(0,0,0,0.05);'><view><text style='font-size:24rpx;color:#999;'>合计：</text><text style='color:#e74c3c;font-size:36rpx;font-weight:bold;'>¥{{totalPrice}}</text></view><button class='btn-primary' style='width:200rpx;height:72rpx;line-height:72rpx;font-size:28rpx;margin:0;' bindtap='goCheckout'>结算</button></view></view>")

p("order",
"var api=require('../../utils/api');Page({data:{orders:[]},onShow:function(){this.loadOrders();},loadOrders:function(){var t=this;api.getOrders().then(function(r){var orders=(r.data||[]).map(function(o){o.statusLabel=['待支付','已支付','已发货','已完成','已取消','退款中','已退款'][o.status]||'未知';return o;});t.setData({orders:orders});}).catch(function(){});},goDetail:function(e){wx.navigateTo({url:'/pages/order-detail/order-detail?id='+e.currentTarget.dataset.id});},cancelOrder:function(e){var id=e.currentTarget.dataset.id,t=this;wx.showModal({title:'取消订单',content:'确定取消？',success:function(r){if(r.confirm)api.cancelOrder(id).then(function(){wx.showToast({title:'已取消',icon:'none'});t.loadOrders();});}});},confirmReceive:function(e){var id=e.currentTarget.dataset.id,t=this;wx.showModal({title:'确认收货',content:'确定已收到商品？',success:function(r){if(r.confirm)api.confirmReceive(id).then(function(){wx.showToast({title:'已确认',icon:'none'});t.loadOrders();});}});},payOrder:function(e){wx.navigateTo({url:'/pages/checkout/checkout?orderId='+e.currentTarget.dataset.id+'&mode=pay'});}});",
"<view class='page-wrap'><view wx:if='{{orders.length===0}}' class='empty-state'><text>暂无订单</text></view><block wx:for='{{orders}}' wx:key='id'><view class='card' data-id='{{item.id}}' bindtap='goDetail'><view style='display:flex;justify-content:space-between;margin-bottom:12rpx;'><text style='font-size:24rpx;color:#999;'>订单号：{{item.orderNo}}</text><text class='status-tag status-{{item.status}}'>{{item.statusLabel}}</text></view><view style='text-align:right;margin-top:12rpx;font-size:28rpx;'>共¥<text style='color:#e74c3c;font-weight:bold;'>{{item.actualAmount||item.totalAmount}}</text></view><view style='display:flex;justify-content:flex-end;gap:16rpx;margin-top:12rpx;' catchtap='stop'><button wx:if='{{item.status===0}}' class='btn-sm btn-primary' data-id='{{item.id}}' bindtap='payOrder'>立即支付</button><button wx:if='{{item.status===0}}' class='btn-sm btn-ghost' data-id='{{item.id}}' bindtap='cancelOrder'>取消</button><button wx:if='{{item.status===2}}' class='btn-sm btn-primary' data-id='{{item.id}}' bindtap='confirmReceive'>确认收货</button></view></view></block></view>")

p("user",
"var api=require('../../utils/api');var app=getApp();Page({data:{isLogin:false,userInfo:{},unreadCount:0},onShow:function(){var isLogin=app.globalData.isLoggedIn;this.setData({isLogin:isLogin,userInfo:app.globalData.userInfo||{}});if(isLogin){var t=this;api.getProfile().then(function(r){t.setData({userInfo:r.data||{}});}).catch(function(){});api.getNotifications(1,1).then(function(r){t.setData({unreadCount:r.data&&r.data.unreadCount||0});}).catch(function(){});}},goLogin:function(){wx.navigateTo({url:'/pages/login/login'});},goFavorites:function(){wx.navigateTo({url:'/pages/favorites/favorites'});},goCoupons:function(){wx.navigateTo({url:'/pages/coupons/coupons'});},goNotifications:function(){wx.navigateTo({url:'/pages/notifications/notifications'});},goAddress:function(){wx.navigateTo({url:'/pages/address/address'});},goExchange:function(){wx.navigateTo({url:'/pages/exchange/exchange'});},goFeedback:function(){wx.navigateTo({url:'/pages/feedback/feedback'});},goProfile:function(){wx.navigateTo({url:'/pages/user/user-edit'});},logout:function(){var t=this;wx.showModal({title:'退出登录',content:'确定退出？',success:function(r){if(r.confirm){api.logoutApi();app.logout();t.setData({isLogin:false,userInfo:{}});}}});}});",
"<view class='page-wrap'><view wx:if='{{!isLogin}}' style='text-align:center;padding:80rpx 0;'><view style='font-size:80rpx;margin-bottom:20rpx;'>👤</view><button class='btn-primary' style='width:400rpx;margin:0 auto;' bindtap='goLogin'>登录 / 注册</button></view><block wx:if='{{isLogin}}'><view style='background:linear-gradient(135deg,#667eea,#764ba2);padding:40rpx 30rpx;color:#fff;'><view style='display:flex;align-items:center;gap:20rpx;'><view style='width:100rpx;height:100rpx;border-radius:50%;background:rgba(255,255,255,0.3);display:flex;align-items:center;justify-content:center;font-size:50rpx;'>👤</view><view><view style='font-size:36rpx;font-weight:bold;'>{{userInfo.username}}</view><view style='font-size:24rpx;opacity:0.8;'>{{userInfo.roleType===2?'管理员':(userInfo.roleType===1?'商家':'顾客')}}</view></view></view></view><view class='card' style='display:flex;justify-content:space-around;'><view bindtap='goFavorites' style='text-align:center;'><view style='font-size:36rpx;'>⭐</view><view style='font-size:24rpx;'>收藏</view></view><view bindtap='goCoupons' style='text-align:center;'><view style='font-size:36rpx;'>🎫</view><view style='font-size:24rpx;'>优惠券</view></view><view bindtap='goNotifications' style='text-align:center;position:relative;'><view style='font-size:36rpx;'>🔔</view><view style='font-size:24rpx;'>消息</view><view wx:if='{{unreadCount>0}}' style='position:absolute;top:-8rpx;right:8rpx;min-width:32rpx;height:32rpx;line-height:32rpx;background:#ff6b6b;color:#fff;border-radius:16rpx;font-size:20rpx;text-align:center;'>{{unreadCount>99?'99+':unreadCount}}</view></view><view bindtap='goFeedback' style='text-align:center;'><view style='font-size:36rpx;'>💬</view><view style='font-size:24rpx;'>反馈</view></view></view><view style='margin:20rpx 24rpx;'><view class='card' style='margin:0 0 2rpx 0;border-radius:16rpx 16rpx 0 0;' bindtap='goOrders'><text>我的订单</text><text style='color:#999;float:right;'>查看全部 ></text></view><view class='card' style='margin:0 0 2rpx 0;border-radius:0;' bindtap='goAddress'><text>收货地址</text><text style='color:#999;float:right;'>></text></view><view class='card' style='margin:0 0 2rpx 0;border-radius:0;' bindtap='goExchange'><text>以物易物</text><text style='color:#999;float:right;'>></text></view><view class='card' style='margin:0;border-radius:0 0 16rpx 16rpx;' bindtap='goProfile'><text>个人信息</text><text style='color:#999;float:right;'>></text></view></view><view style='padding:40rpx 24rpx;'><button class='btn-ghost' bindtap='logout' style='width:100%;'>退出登录</button></view></block></view>")

p("product-detail",
"var api=require('../../utils/api');Page({data:{product:null,isFavorite:false,quantity:1,reviews:[],alsoBought:[],similar:[],pricePrediction:null},onLoad:function(opts){var t=this,id=parseInt(opts.id);api.getProductDetail(id).then(function(r){t.setData({product:r.data});api.trackBehavior({productId:id,action:'view'});}).catch(function(){});api.getAlsoBought(id).then(function(r){t.setData({alsoBought:r.data||[]});}).catch(function(){});api.getSimilar(id).then(function(r){t.setData({similar:r.data||[]});}).catch(function(){});api.getReviews(id).then(function(r){t.setData({reviews:r.data||[]});}).catch(function(){});api.aiPricePredict(id).then(function(r){t.setData({pricePrediction:r.data});}).catch(function(){});api.checkFav(id).then(function(r){t.setData({isFavorite:r.data&&r.data.isFavorite});}).catch(function(){});},addQty:function(){if(this.data.quantity<(this.data.product?this.data.product.stock||99:99))this.setData({quantity:this.data.quantity+1});},subQty:function(){if(this.data.quantity>1)this.setData({quantity:this.data.quantity-1});},addToCart:function(){var pid=this.data.product.id,qty=this.data.quantity;api.addToCart(pid,qty).then(function(){wx.showToast({title:'已加入购物车',icon:'success'});}).catch(function(e){wx.showToast({title:e.message||'请先登录',icon:'none'});});},buyNow:function(){var t=this;api.addToCart(this.data.product.id,this.data.quantity).then(function(){wx.navigateTo({url:'/pages/checkout/checkout'});});},toggleFavorite:function(){var t=this,isFav=t.data.isFavorite,fn=isFav?api.removeFav:api.addFav;fn(t.data.product.id).then(function(){t.setData({isFavorite:!isFav});});},goProduct:function(e){wx.navigateTo({url:'/pages/product-detail/product-detail?id='+e.currentTarget.dataset.id});}});",
"<view class='page-wrap'><view wx:if='{{!product}}' class='empty-state'><text>加载中...</text></view><block wx:if='{{product}}'><image src='{{product.imageUrl}}' mode='aspectFill' style='width:100%;height:600rpx;background:#f0f0f0;'/><view style='background:#fff;padding:24rpx;'><view style='font-size:32rpx;font-weight:bold;margin-bottom:16rpx;'>{{product.name}}</view><view style='display:flex;align-items:baseline;gap:12rpx;margin-bottom:16rpx;'><text style='color:#e74c3c;font-size:44rpx;font-weight:bold;'>¥{{product.price}}</text><text wx:if='{{product.originalPrice}}' style='color:#999;font-size:26rpx;text-decoration:line-through;'>¥{{product.originalPrice}}</text></view><view style='display:flex;gap:24rpx;font-size:24rpx;color:#999;'><text>销量：{{product.salesCount||0}}</text><text>库存：{{product.stock||0}}</text></view><view wx:if='{{pricePrediction}}' style='margin-top:20rpx;padding:20rpx;background:#f8f9fa;border-radius:12rpx;'><view style='font-size:26rpx;font-weight:bold;margin-bottom:12rpx;'>AI 价格预测</view><view style='display:flex;gap:16rpx;'><view style='flex:1;text-align:center;background:#fff;border-radius:8rpx;padding:12rpx;'><text style='font-size:22rpx;color:#999;'>7天</text><view style='font-size:28rpx;font-weight:bold;color:#ff6700;'>¥{{pricePrediction.predictedPrice7d||'--'}}</view></view><view style='flex:1;text-align:center;background:#fff;border-radius:8rpx;padding:12rpx;'><text style='font-size:22rpx;color:#999;'>30天</text><view style='font-size:28rpx;font-weight:bold;color:#ff6700;'>¥{{pricePrediction.predictedPrice30d||'--'}}</view></view><view style='flex:1;text-align:center;background:#fff;border-radius:8rpx;padding:12rpx;'><text style='font-size:22rpx;color:#999;'>90天</text><view style='font-size:28rpx;font-weight:bold;color:#ff6700;'>¥{{pricePrediction.predictedPrice90d||'--'}}</view></view></view></view></view><view class='card'><view class='section-title' style='padding:0 0 16rpx 0;'>商品详情</view><view style='font-size:28rpx;color:#666;line-height:1.8;'>{{product.description||'暂无详细描述'}}</view></view><view wx:if='{{reviews.length>0}}' class='card'><view style='font-size:28rpx;font-weight:bold;margin-bottom:16rpx;'>用户评价 ({{reviews.length}})</view><view wx:for='{{reviews}}' wx:key='id' style='padding:16rpx 0;border-bottom:1rpx solid #f0f0f0;'><view style='display:flex;justify-content:space-between;'><text style='font-weight:600;'>{{item.username||'匿名'}}</text><text style='color:#ffa502;'>★★★★★</text></view><view style='font-size:26rpx;color:#666;margin-top:8rpx;'>{{item.content}}</view></view></view><view wx:if='{{alsoBought.length>0}}'><view class='section-title'>买了又买</view><scroll-view scroll-x class='recommend-scroll'><view wx:for='{{alsoBought}}' wx:key='id' class='recommend-item' data-id='{{item.id}}' bindtap='goProduct'><image src='{{item.imageUrl||item.image}}' mode='aspectFill'/><view class='name'>{{item.name}}</view><view class='price'>¥{{item.price}}</view></view></scroll-view></view><view style='position:fixed;bottom:0;left:0;right:0;background:#fff;padding:16rpx 24rpx;display:flex;align-items:center;gap:16rpx;box-shadow:0 -2rpx 10rpx rgba(0,0,0,0.05);z-index:100;'><view bindtap='toggleFavorite' style='text-align:center;font-size:24rpx;'><view style='font-size:40rpx;'>{{isFavorite?'⭐':'☆'}}</view><text>{{isFavorite?'已收藏':'收藏'}}</text></view><button class='btn-ghost' style='flex:1;height:72rpx;line-height:72rpx;font-size:28rpx;margin:0;' bindtap='addToCart'>加入购物车</button><button class='btn-primary' style='flex:1;height:72rpx;line-height:72rpx;font-size:28rpx;margin:0;' bindtap='buyNow'>立即购买</button></view></block></view>")

p("login",
"var api=require('../../utils/api');var app=getApp();Page({data:{username:'',password:''},onUsername:function(e){this.setData({username:e.detail.value});},onPassword:function(e){this.setData({password:e.detail.value});},login:function(){var t=this,d=t.data;if(!d.username||!d.password){wx.showToast({title:'请填写完整',icon:'none'});return;}wx.showLoading({title:'登录中...'});api.login(d.username,d.password).then(function(res){wx.hideLoading();app.setLogin(res.data);wx.showToast({title:'登录成功',icon:'success'});setTimeout(function(){wx.navigateBack();},1000);}).catch(function(e){wx.hideLoading();wx.showToast({title:(e&&e.message)||'登录失败',icon:'none'});});},goRegister:function(){wx.navigateTo({url:'/pages/register/register'});}});",
"<view class='page-wrap' style='padding:60rpx 40rpx;'><view style='text-align:center;margin-bottom:60rpx;'><view style='font-size:48rpx;font-weight:bold;color:#667eea;'>线上购物</view><view style='font-size:28rpx;color:#999;margin-top:10rpx;'>欢迎回来</view></view><view style='background:#fff;border-radius:16rpx;padding:30rpx;'><input placeholder='用户名' value='{{username}}' bindinput='onUsername' style='height:88rpx;border-bottom:1rpx solid #f0f0f0;font-size:30rpx;'/><input placeholder='密码' password value='{{password}}' bindinput='onPassword' style='height:88rpx;border-bottom:1rpx solid #f0f0f0;font-size:30rpx;'/><button class='btn-primary' bindtap='login' style='margin-top:40rpx;'>登 录</button><button class='btn-ghost' bindtap='goRegister' style='margin-top:20rpx;'>注册新账号</button></view></view>")

p("register",
"var api=require('../../utils/api');Page({data:{username:'',password:'',confirmPassword:'',email:'',phone:''},register:function(){var d=this.data;if(!d.username||!d.password){wx.showToast({title:'请填写用户名和密码',icon:'none'});return;}if(d.password!==d.confirmPassword){wx.showToast({title:'两次密码不一致',icon:'none'});return;}wx.showLoading({title:'注册中...'});api.register({username:d.username,password:d.password,email:d.email,phone:d.phone}).then(function(res){wx.hideLoading();wx.showToast({title:'注册成功',icon:'success'});setTimeout(function(){wx.navigateBack();},1000);}).catch(function(e){wx.hideLoading();wx.showToast({title:(e&&e.message)||'注册失败',icon:'none'});});}});",
"<view class='page-wrap' style='padding:40rpx;'><view style='text-align:center;margin-bottom:40rpx;'><view style='font-size:40rpx;font-weight:bold;color:#667eea;'>注册账号</view></view><view style='background:#fff;border-radius:16rpx;padding:30rpx;'><input placeholder='用户名' value='{{username}}' bindinput='onUsername' style='height:80rpx;border-bottom:1rpx solid #f0f0f0;font-size:28rpx;'/><input placeholder='密码(6位以上)' password value='{{password}}' bindinput='onPassword' style='height:80rpx;border-bottom:1rpx solid #f0f0f0;font-size:28rpx;'/><input placeholder='确认密码' password value='{{confirmPassword}}' bindinput='onConfirmPassword' style='height:80rpx;border-bottom:1rpx solid #f0f0f0;font-size:28rpx;'/><input placeholder='邮箱(选填)' value='{{email}}' bindinput='onEmail' style='height:80rpx;border-bottom:1rpx solid #f0f0f0;font-size:28rpx;'/><input placeholder='手机号(选填)' type='number' value='{{phone}}' bindinput='onPhone' style='height:80rpx;font-size:28rpx;'/><button class='btn-primary' bindtap='register' style='margin-top:40rpx;'>注 册</button></view></view>")

p("search",
"var api=require('../../utils/api');Page({data:{keyword:'',results:[],history:[],loading:false},onLoad:function(opts){if(opts.keyword){this.setData({keyword:opts.keyword});this.doSearch();}this.loadHistory();},onShow:function(){this.loadHistory();},loadHistory:function(){var t=this;api.getSearchHistory().then(function(r){t.setData({history:r.data||[]});}).catch(function(){});},doSearch:function(){var kw=this.data.keyword.trim();if(!kw)return;var t=this;t.setData({loading:true});api.searchProducts(kw).then(function(r){t.setData({results:(r.data&&r.data.list)||r.data||[],loading:false});}).catch(function(){t.setData({loading:false});});},goProduct:function(e){wx.navigateTo({url:'/pages/product-detail/product-detail?id='+e.currentTarget.dataset.id});},tapHistory:function(e){var kw=e.currentTarget.dataset.kw;this.setData({keyword:kw});this.doSearch();},clearHistory:function(){var t=this;api.clearSearchHistory().then(function(){t.setData({history:[]});});}});",
"<view class='page-wrap'><view class='search-bar'><input placeholder='搜索商品' value='{{keyword}}' bindinput='onKeyword' confirm-type='search' bindconfirm='doSearch' focus/><button bindtap='doSearch'>搜索</button></view><view wx:if='{{history.length>0&&results.length===0&&!loading}}'><view style='display:flex;justify-content:space-between;padding:20rpx 24rpx;'><text style='font-weight:bold;'>搜索历史</text><text style='color:#999;font-size:24rpx;' bindtap='clearHistory'>清除</text></view><view style='display:flex;flex-wrap:wrap;padding:0 24rpx;'><view wx:for='{{history}}' wx:key='keyword' data-kw='{{item.keyword}}' bindtap='tapHistory' style='padding:8rpx 24rpx;margin:6rpx;background:#f5f5f5;border-radius:32rpx;font-size:24rpx;'>{{item.keyword}}</view></view></view><view wx:if='{{loading}}' class='load-more'>搜索中...</view><view class='product-grid' style='padding-top:16rpx;'><view wx:for='{{results}}' wx:key='id' class='product-card' data-id='{{item.id}}' bindtap='goProduct'><image src='{{item.imageUrl}}' mode='aspectFill'/><view class='info'><view class='name'>{{item.name}}</view><view class='price'>¥{{item.price}}</view></view></view></view><view wx:if='{{results.length===0&&!loading&&keyword}}' class='empty-state'><text>未找到相关商品</text></view></view>")

# Simple pages for remaining routes
for name in ['order-detail', 'checkout', 'address', 'address-edit', 'favorites',
              'coupons', 'notifications', 'reviews', 'logistics', 'exchange',
              'exchange-detail', 'refund', 'feedback']:
    p(name,
      "Page({data:{},onLoad:function(){console.log('" + name + " loaded');}});",
      "<view class='page-wrap'><view class='section-title'>" + name.replace('-', ' ') + "</view><view class='container'><text>Feature page: " + name + "</text></view></view>")

# Write all
for name, js, wxml in PAGES:
    d = 'pages/' + name
    W(d + '/' + name + '.js', js)
    W(d + '/' + name + '.wxml', wxml)
    W(d + '/' + name + '.wxss', '')
    W(d + '/' + name + '.json', '{}')
    print("  " + name)

print("\nDone! All " + str(len(PAGES)) + " pages generated.")