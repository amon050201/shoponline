var api = require('../../utils/api');
Page({
  data: {
    orderId: 0,
    orderNo: '',
    amount: 0,
    qrCodeUrl: '',
    paymentMethod: 'alipay',
    countdown: '05:00',
    paying: false,
    paid: false
  },
  onLoad: function(opts) {
    var t = this;
    var oid = parseInt(opts.orderId) || 0;
    this.setData({ orderId: oid });
    this.createPayment('alipay');
  },
  createPayment: function(method) {
    var t = this;
    t.setData({ paying: true, paymentMethod: method });
    api.createPayment(t.data.orderId, method).then(function(r) {
      t.setData({
        paying: false,
        orderNo: r.data.orderNo,
        amount: r.data.amount,
        qrCodeUrl: r.data.qrCodeUrl
      });
      t.startCountdown(300);
    }).catch(function(e) {
      t.setData({ paying: false });
      wx.showToast({ title: e.message || '创建支付失败', icon: 'none' });
    });
  },
  switchMethod: function(e) {
    var m = e.currentTarget.dataset.method;
    this.createPayment(m);
  },
  startCountdown: function(seconds) {
    var that = this;
    clearInterval(this._timer);
    var remaining = seconds;
    function tick() {
      var m = Math.floor(remaining / 60);
      var s = remaining % 60;
      that.setData({ countdown: String(m).padStart(2,'0') + ':' + String(s).padStart(2,'0') });
      if (remaining <= 0) {
        clearInterval(that._timer);
        that.setData({ countdown: '00:00', qrCodeUrl: '' });
      }
      remaining--;
    }
    tick();
    this._timer = setInterval(tick, 1000);
  },
  simulatePay: function() {
    var t = this;
    wx.showLoading({ title: '支付中...' });
    api.createPayment(t.data.orderId, t.data.paymentMethod).then(function(r) {
      // Simulate callback
      wx.request({
        url: 'http://127.0.0.1:8080/api/payment/callback?orderNo=' + (r.data.orderNo || t.data.orderNo) + '&transactionId=DEMO' + Date.now(),
        method: 'POST',
        success: function() {
          wx.hideLoading();
          t.setData({ paid: true });
          clearInterval(t._timer);
          setTimeout(function() {
            wx.switchTab({ url: '/pages/order/order' });
          }, 2000);
        },
        fail: function() {
          wx.hideLoading();
          wx.showToast({ title: '支付失败', icon: 'none' });
        }
      });
    });
  },
  onUnload: function() { clearInterval(this._timer); }
});
