import axios from 'axios';
import qs from 'qs';
import Utils from './utils';

const DefaultParam = { repeatable: false };

let ajax = {
  PREFIX: '',
  Author: Utils.getAuthor() || 'heyui',
  requestingApi: new Set(),
  extractUrl: function (url) {
    return url ? url.split('?')[0] : '';
  },
  isRequesting: function (url) {
    let api = this.extractUrl(url);
    return this.requestingApi.has(api);
  },
  addRequest: function (url) {
    let api = this.extractUrl(url);
    this.requestingApi.add(api);
  },
  deleteRequest: function (url) {
    let api = this.extractUrl(url);
    this.requestingApi.delete(api);
  },
  get: function (url, param, extendParam) {
    let params = {
      url,
      method: 'GET'
    };
    if (param) {
      ;
      params.params = param;
    }
    return this.ajax(params, extendParam);
  },
  post: function (url, param, extendParam) {
    var params = {
      url,
      method: 'POST'
    };
    if (param) params.data = qs.stringify(param);
    return this.ajax(params, extendParam);
  },
  postJson: function (url, paramJson, extendParam) {
    return this.ajax({
      url,
      method: 'POST',
      data: paramJson
    }, extendParam);
  },
  patchJson: function (url, paramJson, dataType, extendParam) {
    return this.ajax({
      url,
      method: 'PATCH',
      data: paramJson
    }, extendParam);
  },
  delete: function (url, extendParam) {
    return this.ajax({
      url: url,
      method: 'DELETE'
    }, extendParam);
  },
  ajax: function (param, extendParam) {
    let params = Utils.extend({}, DefaultParam, param, extendParam || {});
    params.crossDomain = params.url.indexOf('http') === 0;
    let url = params.url;
    if (!params.crossDomain) {
      url = params.url = this.PREFIX + params.url;
    }
    if (params.method != 'GET') {
      if (this.isRequesting(url)) {
        return new Promise((resolve, reject) => { resolve({ok: false, msg: '重复请求'}); });
      }
      if (params.repeatable === false) {
        this.addRequest(url);
      }
    }
    let header = {
      author: this.Author,
      Authorization: Utils.getLocal('token')
    };
    let defaultParam = {
      headers: header,
      responseType: 'json',
      validateStatus: function (status) {
        return true;
      },
      paramsSerializer: (params) => {
        return qs.stringify(params, { allowDots: true });
      }
    };
    if (params.crossDomain) {
      defaultParam.headers = {};
    }
    let that = this;
    that.loading = true;
    params = Utils.extend({}, defaultParam, params);
    return new Promise((resolve) => {
      return axios.request(params).then((response) => {
        that.deleteRequest(params.url);
        let data = response.data;
        let status = response.status;
        // 如果后端统一封装返回，即所有的请求都是200, 错误码由返回结果提供，则使用以下代码获取状态
        let code;
        if (status === 200) {
          code = data.code;
        }else if(status===404){
          HeyUI.$Message.error('请求不存在');
        }
        if (code !== 0) {
          HeyUI.$Notice['error'](response.data.msg);
          if (code === 5) {
            window.top.location = '/login';
            return;
          }
          if (code === 999) {
            HeyUI.$Message.error('系统未知异常');
          } else if (code === 3) {
            HeyUI.$Message.error('访问受限');
          }
        }
        data.ok = code === 0;
        resolve(data);
      }).catch(() => {
        that.deleteRequest(params.url);
        that.loading = false;
        resolve({
          ok: false
        });
      });
    });
  }
};
export default ajax;
