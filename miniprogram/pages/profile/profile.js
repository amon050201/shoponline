// pages/profile/profile.js
const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    isLoggedIn: false,
    userInfo: null,
    orderCounts: {
      pending: 0,
      paid: 0,
      shipped: 0,
      completed: 0
    },
    // 功能菜单
    menuList: [
      { id: 'order', icon: '📋', name: '我的订单', url: '/pages/order/order', badge: '' },
      { id: 'address', icon: '📍', name: '收货地址', url: '/pages/address/address', badge: '' },
      { id: 'favorite', icon: '❤️', name: '我的收藏', url: '', badge: '' },
      { id: 'coupon', icon: '🎫', name: '优惠券', url: '', badge: '' },
      { id: 'footprint', icon: '👣', name: '浏览记录', url: '', badge: '' },
      { id: 'service', icon: '💬', name: '联系客服', url: '', badge: '' },
      { id: 'about', icon: 'ℹ️', name: '关于我们', url: '', badge: '' }
    ]
  },

  onShow() {
    this.checkLoginState();
  },

  checkLoginState() {
    const isLoggedIn = app.isLoggedIn();
    const rawInfo = app.globalData.userInfo || wx.getStorageSync('userInfo');
    const userInfo = rawInfo ? { ...rawInfo } : null;
    if (userInfo && userInfo.username) {
      userInfo._avatarLetter = userInfo.username.charAt(0).toUpperCase();
    }
    this.setData({
      isLoggedIn,
      userInfo
    });
  },

  // 点击菜单
  onMenuTap(e) {
    const { id, url } = e.currentTarget.dataset;
    if (id === 'order' || url) {
      if (!this.data.isLoggedIn) {
        this.goToLogin();
        return;
      }
      if (url) {
        wx.navigateTo({ url });
      }
    } else if (id === 'service') {
      wx.showToast({ title: '客服热线: 400-888-8888', icon: 'none' });
    } else if (id === 'about') {
      wx.showModal({
        title: '关于我们',
        content: '优选商城 v1.0.0\n专业的电商购物平台，为您提供优质的商品和服务。',
        showCancel: false
      });
    } else {
      wx.showToast({ title: `${e.currentTarget.dataset.name}`, icon: 'none' });
    }
  },

  // 快捷订单状态
  onOrderStatusTap(e) {
    if (!this.data.isLoggedIn) {
      this.goToLogin();
      return;
    }
    const { status } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/order/order?status=${status}`
    });
  },

  // 去登录
  goToLogin() {
    wx.navigateTo({ url: '/pages/auth/auth' });
  },

  // 退出登录
  onLogout() {
    wx.showModal({
      title: '提示',
      content: '确定退出登录？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('token');
          wx.removeStorageSync('userInfo');
          app.globalData.token = '';
          app.globalData.userInfo = null;
          app.globalData.cartCount = 0;

          this.setData({
            isLoggedIn: false,
            userInfo: null
          });

          app.showSuccess('已退出登录');
        }
      }
    });
  }
});
