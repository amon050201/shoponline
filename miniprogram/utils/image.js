var BASE_URL = 'http://127.0.0.1:8080';
function imgUrl(url) {
  if (!url) return '/images/default-product.svg';
  if (url.indexOf('http') === 0) return url;
  if (url.indexOf('//') === 0) return 'http:' + url;
  if (url.indexOf('/') === 0) return BASE_URL + url;
  return BASE_URL + '/' + url;
}
module.exports = { imgUrl: imgUrl };
