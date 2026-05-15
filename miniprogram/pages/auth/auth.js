// pages/auth/auth.js
const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    // 登录/注册切换
    isLoginTab: true,
    // 登录表单
    loginForm: {
      username: '',
      password: ''
    },
    // 注册表单
    registerForm: {
      username: '',
      password: '',
      confirmPassword: '',
      captcha: ''
    },
    // 验证码
    captchaSrc: '',
    captchaId: '',
    // 加载状态
    loginLoading: false,
    registerLoading: false,
    // 错误信息
    loginError: '',
    registerError: '',
    // 密码可见
    showLoginPassword: false,
    showRegisterPassword: false,
    showConfirmPassword: false,
    // 协议
    agreed: false
  },

  onLoad() {
    // 已登录则跳转首页
    if (app.isLoggedIn()) {
      wx.switchTab({ url: '/pages/index/index' });
    }
  },

  // 切换登录/注册
  switchToLogin() {
    this.setData({
      isLoginTab: true,
      loginError: '',
      registerError: ''
    });
    wx.setNavigationBarTitle({ title: '登录' });
  },

  switchToRegister() {
    this.setData({
      isLoginTab: false,
      loginError: '',
      registerError: ''
    });
    wx.setNavigationBarTitle({ title: '注册' });
  },

  // 登录表单输入
  onLoginInput(e) {
    const { field } = e.currentTarget.dataset;
    this.setData({
      [`loginForm.${field}`]: e.detail.value,
      loginError: ''
    });
  },

  // 注册表单输入
  onRegisterInput(e) {
    const { field } = e.currentTarget.dataset;
    this.setData({
      [`registerForm.${field}`]: e.detail.value,
      registerError: ''
    });
  },

  // 切换密码可见
  toggleLoginPassword() {
    this.setData({ showLoginPassword: !this.data.showLoginPassword });
  },

  toggleRegisterPassword() {
    this.setData({ showRegisterPassword: !this.data.showRegisterPassword });
  },

  toggleConfirmPassword() {
    this.setData({ showConfirmPassword: !this.data.showConfirmPassword });
  },

  // 同意协议
  toggleAgreed() {
    this.setData({ agreed: !this.data.agreed });
  },

  // 登录
  async onLogin() {
    const { username, password } = this.data.loginForm;

    if (!username.trim()) {
      this.setData({ loginError: '请输入用户名' });
      return;
    }
    if (!password) {
      this.setData({ loginError: '请输入密码' });
      return;
    }

    this.setData({ loginLoading: true, loginError: '' });

    try {
      const res = await api.login(username.trim(), password);

      if (res.success !== false) {
        const data = res.data || res;
        const token = data.token || data.accessToken || '';
        const userInfo = {
          username: data.username || username,
          userId: data.userId || data.id,
          roleType: data.roleType || 'USER'
        };

        // 保存登录信息
        app.globalData.token = token;
        app.globalData.userInfo = userInfo;
        wx.setStorageSync('token', token);
        wx.setStorageSync('userInfo', userInfo);

        app.showSuccess('登录成功');

        // 更新购物车数量
        app.updateCartCount();

        // 返回上一页或首页
        setTimeout(() => {
          const pages = getCurrentPages();
          if (pages.length > 1) {
            wx.navigateBack();
          } else {
            wx.switchTab({ url: '/pages/index/index' });
          }
        }, 500);
      } else {
        this.setData({
          loginError: res.message || '用户名或密码错误',
          loginLoading: false
        });
      }
    } catch (err) {
      console.error('登录失败:', err);
      this.setData({
        loginError: err.message || '登录失败，请检查网络连接',
        loginLoading: false
      });
    }
  },

  // 注册
  async onRegister() {
    const { username, password, confirmPassword, captcha } = this.data.registerForm;

    if (!username.trim()) {
      this.setData({ registerError: '请输入用户名' });
      return;
    }
    if (username.trim().length < 3) {
      this.setData({ registerError: '用户名至少3个字符' });
      return;
    }
    if (!password) {
      this.setData({ registerError: '请输入密码' });
      return;
    }
    if (password.length < 6) {
      this.setData({ registerError: '密码至少6个字符' });
      return;
    }
    if (password !== confirmPassword) {
      this.setData({ registerError: '两次输入密码不一致' });
      return;
    }
    if (!this.data.agreed) {
      this.setData({ registerError: '请同意用户协议和隐私政策' });
      return;
    }

    this.setData({ registerLoading: true, registerError: '' });

    try {
      const res = await api.register(username.trim(), password);

      if (res.success !== false) {
        app.showSuccess('注册成功');
        // 切回登录
        this.setData({
          isLoginTab: true,
          loginForm: { username: username.trim(), password: '' },
          registerForm: { username: '', password: '', confirmPassword: '', captcha: '' },
          registerLoading: false
        });
        wx.setNavigationBarTitle({ title: '登录' });
      } else {
        this.setData({
          registerError: res.message || '注册失败',
          registerLoading: false
        });
      }
    } catch (err) {
      console.error('注册失败:', err);
      this.setData({
        registerError: err.message || '注册失败，请检查网络连接',
        registerLoading: false
      });
    }
  }
});
