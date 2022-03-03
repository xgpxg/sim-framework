import {constantRoutes} from '@/router'
import User from "./user"
import Layout from '@/layout'
import {U} from "../../utils/util";

/**
 * Use meta.role to determine if the current user has permission
 * @param roles
 * @param route
 */
function hasPermission(roles, route) {
  if (route.meta && route.meta.roles) {
    return roles.some(role => route.meta.roles.includes(role))
  } else {
    return true
  }
}

/**
 * Filter asynchronous routing tables by recursion
 * @param routes asyncRoutes
 * @param roles
 */
export function filterAsyncRoutes(routes, roles) {
  const res = []

  routes.forEach(route => {
    const tmp = {...route}
    if (hasPermission(roles, tmp)) {
      if (tmp.children) {
        tmp.children = filterAsyncRoutes(tmp.children, roles)
      }
      res.push(tmp)
    }
  })

  return res
}

const state = {
  routes: [],
  addRoutes: []
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.addRoutes = routes
    state.routes = constantRoutes.concat(routes)
  }
}

const actions = {
  /**
   * 动态获取路由
   * @param commit
   * @param roles
   * @param token
   * @returns {Promise<Array>}
   */
  generateRoutes({commit}, roles, token) {
    return new Promise(resolve => {
      let accessedRoutes;
      let _user = User.state._user;
      let menus = [];

      //将权限数组转化为树结构
      let tree = U.toTreeData(U.copy(_user['permissions']), 'purviewId', 'parentId', 'children');
      //构建菜单
      buildMenu(tree, menus);
      accessedRoutes = menus || [];
      commit('SET_ROUTES', accessedRoutes);
      resolve(accessedRoutes)
    })
  }
};

/**
 * 构建左侧菜单
 * @param permissions 权限
 * @param menus 构建后的菜单
 */
function buildMenu(permissions, menus) {

  permissions.forEach(item => {
    //非菜单权限不展示
    if (item.purviewType === '2' || item.purviewType === '3') {
      return;
    }
    let menu = {
      path: item.url,
      //非顶级 且 或者 组件不为空 使用配置的组件 否则 默认未Layout
      component: (item['parentId'] && item['parentId'] !== -1 || (item.component && item.component !== 'Layout')) ? resolve => require([`@/views${item.component}`], resolve) : Layout,
      hidden: item.purviewType === '3' || item.hidden,
      meta: {title: item['purviewName'], icon: item['icon'], affix: true},
      //解决当子菜单只有一个时，不显示父级菜单问题
      //子菜单存在 && 子菜单存在菜单权限
      alwaysShow: item.children && item.children.length > 0 && item.children.filter(item => item.purviewType === '1').length > 0,
    };
    //顶层菜单 添加默认的children
    if (!item['parentId'] || item['parentId'] === -1) {
      menu.children = [
        {
          path: item.url,
          //注：此处不支持()=>import的写法
          //item.component须以/开头
          component: resolve => require([`@/views${item.component}`], resolve),
          name: item['purviewCode'],
          meta: {title: item['purviewName'], icon: item['icon'], affix: true}
        }
      ]
    }
    menus.push(menu);

    if (item.children) {
      menu.children = [];
      buildMenu(item.children, menu.children);
    }
  });
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
