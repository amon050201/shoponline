App({
  globalData: {
    baseUrl: 'http://localhost:8080',
    userInfo: null,
    token: null,
    isLoggedIn: false,
    cartCount: 0
  },

  onLaunch() {
    this.checkLogin();
  },

  checkLogin() {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    if (token && userInfo) {
      this.globalData.token = token;
      this.globalData.userInfo = userInfo;
      this.globalData.isLoggedIn = true;
    }
  },

  setLogin(res) {
    this.globalData.token = res.token;
    this.globalData.userInfo = {
      id: res.userId,
      username: res.username,
      roleType: res.roleType
    };
    this.globalData.isLoggedIn = true;
    wx.setStorageSync('token', res.token);
    wx.setStorageSync('userInfo', this.globalData.userInfo);
  },

  logout() {
    this.globalData.token = null;
    this.globalData.userInfo = null;
    this.globalData.isLoggedIn = false;
    this.globalData.cartCount = 0;
    wx.removeStorageSync('token');
    wx.removeStorageSync('userInfo');
  }
});
