var api = require('../../utils/api');

Page({
  data: {
    banners: [],
    categories: [],
    trending: [],
    isLogin: false
  },

  onLoad: function () {
    var app = getApp();
    this.setData({ isLogin: app.globalData.isLoggedIn });
    this.loadData();
  },

  onShow: function () {
    var app = getApp();
    this.setData({ isLogin: app.globalData.isLoggedIn });
  },

  loadData: function () {
    var that = this;

    api.getBanners().then(function (r) {
      that.setData({ banners: r.data || [] });
    }).catch(function (e) {
      console.log('banners error:', e);
    });

    api.getCategories().then(function (r) {
      var all = r.data || [];
      var top = all.filter(function (c) { return c.parentId === 0; });
      that.setData({ categories: top });
    }).catch(function (e) {
      console.log('categories error:', e);
    });

    api.getTrending(6).then(function (r) {
      that.setData({ trending: r.data || [] });
    }).catch(function (e) {
      console.log('trending error:', e);
    });
  },

  goSearch: function () {
    wx.navigateTo({ url: '/pages/search/search' });
  },

  goProduct: function (e) {
    var id = e.currentTarget.dataset.id;
    if (id) wx.navigateTo({ url: '/pages/product-detail/product-detail?id=' + id });
  },

  goCategory: function (e) {
    var id = e.currentTarget.dataset.id;
    getApp().globalData.selectedCategory = id;
    wx.switchTab({ url: '/pages/category/category' });
  }
});
