/**
 * AI功能 API 封装
 * 智能估值、图像识别、价格预测、欺诈检测
 */

const app = getApp();

const BASE_URL = app.globalData.baseUrl || 'http://localhost:8080/api';

function request(method, url, data = {}, options = {}) {
  return new Promise((resolve, reject) => {
    const fullUrl = url.startsWith('http') ? url : `${BASE_URL}${url}`;
    const token = app.globalData.token || wx.getStorageSync('token');
    const header = { 'Content-Type': 'application/json' };
    if (token) header['Authorization'] = `Bearer ${token}`;

    let requestUrl = fullUrl;
    let requestData = data;

    if (method === 'GET') {
      const params = [];
      for (const key in data) {
        if (data[key] !== undefined && data[key] !== null) {
          params.push(`${encodeURIComponent(key)}=${encodeURIComponent(data[key])}`);
        }
      }
      if (params.length > 0) {
        requestUrl += (fullUrl.includes('?') ? '&' : '?') + params.join('&');
      }
      requestData = undefined;
    }

    wx.showNavigationBarLoading();
    wx.request({
      url: requestUrl,
      method: method,
      data: requestData,
      header: header,
      timeout: options.timeout || 20000,
      dataType: 'json',
      success(res) {
        if (res.statusCode >= 200 && res.statusCode < 300) {
          resolve(res.data);
        } else {
          reject({ code: res.statusCode, message: (res.data && res.data.message) || '请求失败' });
        }
      },
      fail(err) {
        reject({ code: -1, message: err.errMsg || '网络错误' });
      },
      complete() { wx.hideNavigationBarLoading(); }
    });
  });
}

/** 智能估值 */
function valuateProduct(productId, scenario = 'sale', extraInfo = {}) {
  return request('POST', '/ai/valuation/evaluate', { productId, scenario, extraInfo });
}

function valuateFromDescription(productDesc, brand, model, condition) {
  return request('POST', '/ai/valuation/evaluate-from-desc', { productDesc, brand, model, condition });
}

/** 价格预测 */
function predictPrice(productId, forecastDays = 30) {
  return request('GET', `/ai/price/predict/${productId}`, { forecastDays });
}

function getPriceHistory(productId, days = 90) {
  return request('GET', `/ai/price/history/${productId}`, { days });
}

/** 图片识别 */
function recognizeImage(filePath) {
  return new Promise((resolve, reject) => {
    const token = app.globalData.token || wx.getStorageSync('token');
    const header = {};
    if (token) header['Authorization'] = `Bearer ${token}`;

    wx.uploadFile({
      url: `${BASE_URL}/ai/image/recognize`,
      filePath: filePath,
      name: 'image',
      header: header,
      success(res) {
        try { resolve(JSON.parse(res.data)); }
        catch (e) { reject({ message: '解析失败' }); }
      },
      fail(err) { reject(err); }
    });
  });
}

function searchByImage(filePath) {
  return new Promise((resolve, reject) => {
    const token = app.globalData.token || wx.getStorageSync('token');
    const header = {};
    if (token) header['Authorization'] = `Bearer ${token}`;

    wx.uploadFile({
      url: `${BASE_URL}/ai/image/search`,
      filePath: filePath,
      name: 'image',
      header: header,
      success(res) {
        try { resolve(JSON.parse(res.data)); }
        catch (e) { reject({ message: '解析失败' }); }
      },
      fail(err) { reject(err); }
    });
  });
}

/** 以物易物估值 */
function getExchangeValuation(productId) {
  return request('GET', `/exchange/valuation/${productId}`);
}

function getRecommendedDifference(initiatorProductId, receiverProductId) {
  return request('GET', `/exchange/recommended-diff/${initiatorProductId}/${receiverProductId}`);
}

module.exports = {
  valuateProduct,
  valuateFromDescription,
  predictPrice,
  getPriceHistory,
  recognizeImage,
  searchByImage,
  getExchangeValuation,
  getRecommendedDifference
};
