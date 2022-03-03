import axios from 'axios'
import qs from 'qs';
import {Loading, Message} from 'element-ui'
import {U} from "./util";

const DefaultParam = {loading: false, repeatable: false};

export const R = {
  PREFIX: '',
  requestingApi: new Set(),
  target: {
    userApiPrefix: '/api/auth',
  },
  get forUserApi() {
    let that = this;
    return {
      get(url, param, extendParam) {
        return that.get(that.target.userApiPrefix + url, param, extendParam);
      },
      post(url, param, extendParam) {
        return that.post(that.target.userApiPrefix + url, param, extendParam);
      },
      postJson(url, paramJson, extendParam) {
        return that.postJson(that.target.userApiPrefix + url, paramJson, extendParam);
      },
      patchJson(url, paramJson, dataType, extendParam) {
        return that.patchJson(that.target.userApiPrefix + url, paramJson, dataType, extendParam);
      },
      delete(url, extendParam) {
        return that.delete(that.target.userApiPrefix + url, extendParam);
      },
      getDict(code) {
        let url = that.target.userApiPrefix + '/common/dic';
        return that.get(url, {code: code})
      },
    }
  },
  /**
   * 请求数据字典
   * @param code
   * @param showAll 是否查询"全部选项"
   * @param u
   * @returns {*}
   */
  getDict(code,showAll ,u) {
    let url = u ? u + '/api/auth/common/dic' : '/api/auth/common/dic';
    return this.get(url, {code: code,showAll:(showAll === null || showAll === undefined)});
  },
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
    let params = this.extend({}, DefaultParam, param, extendParam || {});
    params.crossDomain = params.url.indexOf('http') === 0;
    let url = params.url;
    if (!params.crossDomain) {
      url = params.url = this.PREFIX + params.url;
    }

    if (params.method != 'GET') {
      if (this.isRequesting(url)) {
        return new Promise((resolve, reject) => {
          resolve({ok: false, msg: 'Duplicate request'});
        });
      }
      if (params.repeatable === false) {
        this.addRequest(url);
      }
    }
    let header = {};
    let defaultParam = {
      headers: header,
      responseType: 'json',
      validateStatus: function (status) {
        return true;
      },
      paramsSerializer: (params) => {
        return qs.stringify(params, {allowDots: true});
      }
    };
    if (params.crossDomain) {
      defaultParam.headers = {};
    }
    let that = this;
    if (params.loading)
      Loading.service({text: "Loading..."});

    params = this.extend({}, defaultParam, params);
    return new Promise((resolve) => {
      return axios.request(params).then((response) => {
        that.deleteRequest(params.url);
        let data = response.data;
        let status = response.status;

        if (status > 300) {
          let errMsg = `${response.status} ${response.statusText} : ${JSON.stringify(data)}`;
          if(status===404){
            errMsg = `${response.status} ${response.statusText} : ${url}`
          }
          //let errMsg = data.error;
          Message.error(errMsg);
          throw new Error(errMsg);
        } else {
          //服务端统一返回码
          if(data.code===6){
            Message.error(`${data.msg}`);
            throw Error(`${data.msg}`);
          }else if (data.code) {
            Message.error(`request url:${params.url} error: ${data.msg}`);
            throw Error(`${data.msg}`);
          }

          if (typeof data == 'object') {
            data.ok = true;
          } else {
            data = {};
            data.ok = true;
          }
        }
        resolve(data);
      }).catch((err) => {
        that.deleteRequest(params.url);
        if (params.loading)
          Loading.service().close();
        resolve({
          ok: false
        });
      });
    });
  },
  extend() {
    let options, name, src, copy, copyIsArray, clone,
      target = arguments[0] || {},
      i = 1,
      length = arguments.length,
      deep = false;

    if (typeof target === "boolean") {
      deep = target;
      target = arguments[1] || {};

      i = 2;
    }
    if (typeof target !== "object" && !jQuery.isFunction(target)) {
      target = {};
    }
    if (length === i) {
      target = this;

      --i;
    }
    for (; i < length; i++) {
      if ((options = arguments[i]) != null) {
        for (name in options) {
          src = target[name];
          copy = options[name];

          if (target === copy) {
            continue;
          }

          if (deep && copy && (U.isPlainObject(copy) || (copyIsArray = U.isArray(copy)))) {
            if (copyIsArray) {
              copyIsArray = false;
              clone = src && U.isArray(src) ? src : [];
            } else {
              clone = src && U.isPlainObject(src) ? src : {};
            }

            target[name] = this.extend(deep, clone, copy);

          } else if (copy !== undefined) {
            target[name] = copy;
          }
        }
      }
    }
    return target;
  }
};
