import Vue from 'vue'

import Cookies from 'js-cookie'

import 'normalize.css/normalize.css' // a modern alternative to CSS resets

import Element from 'element-ui'
import './styles/element-variables.scss'

import '@/styles/index.scss' // global css

import App from './App'
import store from './store'
import router from './router'

import i18n from './lang' // internationalization
import './icons' // icon
import './permission' // permission control
import './utils/error-log' // error log

import * as filters from './filters'
import {R} from "./utils/R";
import {U} from "./utils/util"; // global filters

import permission from '@/directive/permission/index.js' // 权限判断指令

//自定义扩展指令
//import directives from './utils/directive-extends'

import dataV from '@jiaminghi/data-view'


/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online ! ! !
 */
if (process.env.NODE_ENV === 'production') {
  const { mockXHR } = require('../mock')
  mockXHR()
}

Vue.use(Element, {
  size: Cookies.get('size') || 'medium', // set element-ui default size
  i18n: (key, value) => i18n.t(key, value)
})

Vue.use(dataV)

// register global utility filters
Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
})

Vue.config.productionTip = false


Vue.prototype.R = R;
Vue.prototype.U = U;



new Vue({
  router,
  store,
  i18n,
  render: h => h(App),
}).$mount('#app');

//自定义指令注册 不用这个了
//Object.keys(directives).forEach(k => Vue.directive(k, directives[k]));
//权限指令
Vue.directive('permission', permission);
