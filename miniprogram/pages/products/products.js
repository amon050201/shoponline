// pages/products/products.js
const api = require('../../utils/api');

Page({
  data: {
    // 搜索
    keyword: '',
    showClear: false,
    searchHistory: [],

    // 分类
    categories: [
      { id: 0, name: '全部' },
      { id: 1, name: '手机数码' },
      { id: 2, name: '电脑办公' },
      { id: 3, name: '家用电器' },
      { id: 4, name: '服装鞋帽' },
      { id: 5, name: '美妆个护' },
      { id: 6, name: '食品生鲜' },
      { id: 7, name: '家居生活' },
      { id: 8, name: '运动户外' },
      { id: 9, name: '图书文娱' },
      { id: 10, name: '母婴玩具' }
    ],
    activeCategoryId: 0,

    // 排序
    sortOptions: [
      { id: 'default', name: '综合' },
      { id: 'sales', name: '销量' },
      { id: 'price_asc', name: '价格↑' },
      { id: 'price_desc', name: '价格↓' },
      { id: 'newest', name: '最新' }
    ],
    activeSort: 'default',

    // 商品列表
    products: [],
    total: 0,
    page: 1,
    pageSize: 20,
    isLoading: false,
    isLoadingMore: false,
    hasMore: true,

    // 状态
    isSearching: false,
    showSuggestions: false,
    suggestions: [],
    pageLoaded: false,
    error: ''
  },

  onLoad(options) {
    // 如果传入了搜索关键词
    if (options.keyword) {
      this.setData({
        keyword: options.keyword,
        showClear: true,
        isSearching: true
      });
      this.doSearch(options.keyword);
    } else {
      this.loadProducts();
    }

    // 加载搜索历史
    const history = wx.getStorageSync('searchHistory') || [];
    this.setData({ searchHistory: history });
  },

  onPullDownRefresh() {
    this.setData({
      page: 1,
      hasMore: true,
      products: []
    });
    if (this.data.isSearching) {
      this.doSearch(this.data.keyword);
    } else {
      this.loadProducts();
    }
    wx.stopPullDownRefresh();
  },

  onReachBottom() {
    if (!this.data.hasMore || this.data.isLoadingMore) return;
    this.loadMore();
  },

  // 加载商品列表
  async loadProducts() {
    if (this.data.isLoading) return;
    this.setData({ isLoading: true, error: '' });
    try {
      const res = await api.getProducts({
        page: 1,
        size: this.data.pageSize,
        categoryId: this.data.activeCategoryId > 0 ? this.data.activeCategoryId : undefined,
        sort: this.data.activeSort !== 'default' ? this.data.activeSort : undefined
      });
      const data = res.data || res || [];
      const list = Array.isArray(data) ? data : (data.content || data.records || []);
      this.setData({
        products: list,
        total: data.total || data.totalElements || list.length,
        page: 1,
        isLoading: false,
        pageLoaded: true,
        hasMore: list.length >= this.data.pageSize
      });
    } catch (err) {
      console.error('加载商品失败:', err);
      this.setData({
        isLoading: false,
        pageLoaded: true,
        products: this.getFallbackProducts(),
        error: err.message || ''
      });
    }
  },

  // 加载更多
  async loadMore() {
    if (this.data.isLoadingMore || !this.data.hasMore) return;
    this.setData({ isLoadingMore: true });
    const nextPage = this.data.page + 1;
    try {
      const res = await api.getProducts({
        page: nextPage,
        size: this.data.pageSize,
        categoryId: this.data.activeCategoryId > 0 ? this.data.activeCategoryId : undefined,
        sort: this.data.activeSort !== 'default' ? this.data.activeSort : undefined
      });
      const data = res.data || res || [];
      const list = Array.isArray(data) ? data : (data.content || data.records || []);
      this.setData({
        products: [...this.data.products, ...list],
        page: nextPage,
        isLoadingMore: false,
        hasMore: list.length >= this.data.pageSize
      });
    } catch (err) {
      console.error('加载更多失败:', err);
      this.setData({ isLoadingMore: false });
    }
  },

  // 搜索
  async doSearch(keyword) {
    if (!keyword.trim()) return;
    this.setData({ isLoading: true, error: '', showSuggestions: false });
    try {
      const res = await api.searchProducts(keyword, {
        page: 1,
        size: this.data.pageSize
      });
      const data = res.data || res || [];
      const list = Array.isArray(data) ? data : (data.content || data.records || []);
      this.setData({
        products: list,
        total: data.total || data.totalElements || list.length,
        page: 1,
        isLoading: false,
        pageLoaded: true,
        hasMore: list.length >= this.data.pageSize,
        isSearching: true
      });

      // 保存搜索历史
      this.saveSearchHistory(keyword);
    } catch (err) {
      console.error('搜索失败:', err);
      this.setData({
        isLoading: false,
        pageLoaded: true,
        products: [],
        error: err.message || '搜索失败，请重试'
      });
    }
  },

  // 保存搜索历史
  saveSearchHistory(keyword) {
    let history = wx.getStorageSync('searchHistory') || [];
    history = [keyword, ...history.filter(k => k !== keyword)];
    if (history.length > 10) history = history.slice(0, 10);
    wx.setStorageSync('searchHistory', history);
    this.setData({ searchHistory: history });
  },

  // 清除搜索历史
  clearSearchHistory() {
    wx.showModal({
      title: '提示',
      content: '确定清除搜索历史？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('searchHistory');
          this.setData({ searchHistory: [] });
        }
      }
    });
  },

  // 降级数据
  getFallbackProducts() {
    const fallback = [];
    for (let i = 1; i <= 10; i++) {
      fallback.push({
        id: i,
        name: `商品名称示例 ${i}`,
        price: (Math.random() * 500 + 10).toFixed(2),
        originalPrice: (Math.random() * 800 + 100).toFixed(2),
        image: `https://picsum.photos/seed/prod${i}/400/400`,
        sales: Math.floor(Math.random() * 2000),
        rating: (3.5 + Math.random() * 1.5).toFixed(1)
      });
    }
    return fallback;
  },

  // 输入框内容变化
  onInputChange(e) {
    const value = e.detail.value;
    this.setData({
      keyword: value,
      showClear: value.length > 0
    });
  },

  // 清除搜索框
  onClear() {
    this.setData({ keyword: '', showClear: false, isSearching: false });
    this.loadProducts();
  },

  // 执行搜索
  onSearch() {
    if (this.data.keyword.trim()) {
      this.doSearch(this.data.keyword.trim());
    }
  },

  // 点击分类
  onCategoryTap(e) {
    const id = e.currentTarget.dataset.id;
    if (id === this.data.activeCategoryId) return;
    this.setData({
      activeCategoryId: id,
      page: 1,
      products: [],
      hasMore: true
    });
    if (this.data.isSearching) {
      this.setData({ isSearching: false });
    }
    this.loadProducts();
  },

  // 点击排序
  onSortTap(e) {
    const sort = e.currentTarget.dataset.sort;
    if (sort === this.data.activeSort) return;
    this.setData({
      activeSort: sort,
      page: 1,
      products: [],
      hasMore: true
    });
    if (this.data.isSearching) {
      this.doSearch(this.data.keyword);
    } else {
      this.loadProducts();
    }
  },

  // 点击商品
  onProductTap(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/product-detail/product-detail?id=${id}`
    });
  },

  // 点击历史搜索词
  onHistoryTap(e) {
    const { keyword } = e.currentTarget.dataset;
    this.setData({ keyword });
    this.doSearch(keyword);
  },

  // 重试
  onRetry() {
    this.setData({ pageLoaded: false, error: '' });
    if (this.data.isSearching) {
      this.doSearch(this.data.keyword);
    } else {
      this.loadProducts();
    }
  }
});
