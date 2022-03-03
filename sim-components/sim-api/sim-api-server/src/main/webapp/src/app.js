import '@babel/polyfill';
import Vue from 'vue';
import App from 'components/App';

import heyuiConfig from 'js/config/heyui-config';
import routerConfig from 'js/config/router-config';
import store from 'js/vuex/store';
import 'js/vue/components';
import 'js/vue/filters';
import Requset from 'js/common/request'

require('./css/app.less');

// 开发环境判断
// process.env.NODE_ENV == 'development'

// 使用mock文件， 自己开发的时候请删除
//require('./mock/index');

// HeyUI已经设定为全局变量，无需引用
// 设定全局变量请参考根目录下的hey.conf.js文件

heyuiConfig();
Vue.use(HeyUI);

const router = routerConfig();

export default new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app');

/*$(document).ready(function () {
  setTimeout(function () {
    getCssPath(function (path, el) {
      alert($(path).text() + ':' + el.text())
    });

  }, 2000)
});*/
/*
/!**
 * 获取css path
 * @param callback
 * callback接收两个参数:元素的css路径，选择元素的jq对象
 *!/
function getCssPath(callback) {
  let all = $('*');
  all.click(function () {
    let path = cssPath($(this));
    if ($(path).hasClass('_selected')) {
      $(path).removeClass('_selected');
    } else {
      $(path).addClass('_selected');
    }
    callback(path, $(this));
    return false;
  })
}

function cssPath(el) {
  let path = [];
  while (el && el[0].tagName !== 'HTML') {
    let selector = el[0].tagName.toLowerCase();
    if (el.attr('id')) {
      selector += '#' + el.attr('id');
      path.unshift(selector);
      break;
    } else {
      if (el.attr('class') && el.attr('class') !== '' && el.attr('class').trim() !== '_selected' && selector !== 'input') {
        let classStr = el.attr('class');
        let cl = '.' + classStr.trim().split(/\s+/).join('.');
        if ($(selector + cl + '>' + (path.length > 0 ? path.join(" > ") : '')).length === 1) {
          path.unshift(selector + cl);
          break;
        }
        let suffixLen = el.nextAll();
        let nth = el.prevAll().length + 1;
        if (nth !== 1 || suffixLen !== 0)
          selector += ":nth-child(" + nth + ")";
      } else if (selector === 'input' && el.attr('name')) {
        if ($(selector + '[name=' + el.attr('name') + ']').length === 1) {
          path.unshift(selector + '[name=' + el.attr('name') + ']');
          break;
        }
      } else {
        let nth = el.prevAll().length + 1;
        if (nth !== 1)
          selector += ":nth-child(" + nth + ")";
      }
    }
    path.unshift(selector);
    el = el.parent();
  }
  return path.join(" > ");
}*/
