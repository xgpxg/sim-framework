import request from '@/utils/request'
import {R} from '@/utils/R'

export function login(data) {
  /*return request({
    url: '/vue-element-admin/user/login',
    method: 'post',
    data
  })*/
  return R.postJson('/api/auth/security/login', data);
}

export function getInfo(token) {
  /*return request({
    url: '/vue-element-admin/user/info',
    method: 'get',
    params: { token }
  })*/
  return R.get('/api/auth/user/token', {token:token});
}

export function logout() {
  /*return request({
    url: '/vue-element-admin/user/logout',
    method: 'post'
  })*/
  return R.get('/api/auth/security/logout',{});
}
