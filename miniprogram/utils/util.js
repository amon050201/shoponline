function formatTime(date) {
  const y = date.getFullYear(), m = date.getMonth() + 1, d = date.getDate();
  const h = date.getHours(), mi = date.getMinutes(), s = date.getSeconds();
  return [y, m, d].map(pad).join('-') + ' ' + [h, mi, s].map(pad).join(':');
}
function formatDate(date) {
  const y = date.getFullYear(), m = date.getMonth() + 1, d = date.getDate();
  return [y, m, d].map(pad).join('-');
}
function pad(n) { return n < 10 ? '0' + n : '' + n; }

const statusMap = {
  0: { label: '待支付', cls: 'status-0' },
  1: { label: '已支付', cls: 'status-1' },
  2: { label: '已发货', cls: 'status-2' },
  3: { label: '已完成', cls: 'status-3' },
  4: { label: '已取消', cls: 'status-4' },
  5: { label: '退款中', cls: 'status-5' },
  6: { label: '已退款', cls: 'status-5' }
};
function getOrderStatus(status) { return statusMap[status] || { label: '未知', cls: '' }; }

const exchangeStatusMap = {
  0: { label: '待确认', cls: 'status-0' },
  1: { label: '已确认', cls: 'status-1' },
  2: { label: '已完成', cls: 'status-3' },
  3: { label: '已取消', cls: 'status-4' },
  4: { label: '已拒绝', cls: 'status-4' }
};
function getExchangeStatus(status) { return exchangeStatusMap[status] || { label: '未知', cls: '' }; }

function showToast(title, icon = 'none') { wx.showToast({ title, icon, duration: 2000 }); }
function showLoading(title = '加载中...') { wx.showLoading({ title, mask: true }); }
function hideLoading() { wx.hideLoading(); }

module.exports = {
  formatTime, formatDate, getOrderStatus, getExchangeStatus,
  showToast, showLoading, hideLoading
};
