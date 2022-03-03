import user from '../store/modules/user'
import { R } from './R'
import axios from 'axios'

/**
 * 获取地址栏参数
 */
export const getUrlStr = (name) => {
  const reg = new RegExp('(^|\\?|&)' + name + '=([^&]*)(\\s|&|$)', 'i')
  if (reg.test(window.location.href)) {
    return unescape(RegExp.$2.replace(/\+/g, ' '))
  }
  return undefined
}

// 表单序列化
export const serialize = data => {
  const list = []
  Object.keys(data).forEach(ele => {
    list.push(`${ele}=${data[ele]}`)
  })
  return list.join('&')
}
export const getObjType = obj => {
  const toString = Object.prototype.toString
  const map = {
    '[object Boolean]': 'boolean',
    '[object Number]': 'number',
    '[object String]': 'string',
    '[object Function]': 'function',
    '[object Array]': 'array',
    '[object Date]': 'date',
    '[object RegExp]': 'regExp',
    '[object Undefined]': 'undefined',
    '[object Null]': 'null',
    '[object Object]': 'object'
  }
  if (obj instanceof Element) {
    return 'element'
  }
  return map[toString.call(obj)]
}
export const getViewDom = () => {
  return window.document.getElementById('avue-view').getElementsByClassName('el-scrollbar__wrap')[0]
}
/**
 * 对象深拷贝
 */
export const deepClone = data => {
  const type = getObjType(data)
  let obj
  if (type === 'array') {
    obj = []
  } else if (type === 'object') {
    obj = {}
  } else {
    // 不再具有下一层次
    return data
  }
  if (type === 'array') {
    for (let i = 0, len = data.length; i < len; i++) {
      obj.push(deepClone(data[i]))
    }
  } else if (type === 'object') {
    for (const key in data) {
      obj[key] = deepClone(data[key])
    }
  }
  return obj
}
/**
 * 设置灰度模式
 */
export const toggleGrayMode = (status) => {
  if (status) {
    document.body.className = document.body.className + ' grayMode'
  } else {
    document.body.className = document.body.className.replace(' grayMode', '')
  }
}
/**
 * 设置主题
 */
export const setTheme = (name) => {
  document.body.className = name
}

/**
 * 加密处理
 */
export const encryption = (params) => {
  const {
    data,
    type,
    param,
    key
  } = params
  const result = JSON.parse(JSON.stringify(data))
  if (type == 'Base64') {
    param.forEach(ele => {
      result[ele] = btoa(result[ele])
    })
  } else if (type == 'Aes') {
    param.forEach(ele => {
      result[ele] = window.CryptoJS.AES.encrypt(result[ele], key).toString()
    })
  }
  return result
}

/**
 * 浏览器判断是否全屏
 */
export const fullscreenToggel = () => {
  if (fullscreenEnable()) {
    exitFullScreen()
  } else {
    reqFullScreen()
  }
}
/**
 * esc监听全屏
 */
export const listenfullscreen = (callback) => {
  function listen() {
    callback()
  }

  document.addEventListener('fullscreenchange', function() {
    listen()
  })
  document.addEventListener('mozfullscreenchange', function() {
    listen()
  })
  document.addEventListener('webkitfullscreenchange', function() {
    listen()
  })
  document.addEventListener('msfullscreenchange', function() {
    listen()
  })
}
/**
 * 浏览器判断是否全屏
 */
export const fullscreenEnable = () => {
  const isFullscreen = document.isFullScreen || document.mozIsFullScreen || document.webkitIsFullScreen
  return isFullscreen
}

/**
 * 浏览器全屏
 */
export const reqFullScreen = () => {
  if (document.documentElement.requestFullScreen) {
    document.documentElement.requestFullScreen()
  } else if (document.documentElement.webkitRequestFullScreen) {
    document.documentElement.webkitRequestFullScreen()
  } else if (document.documentElement.mozRequestFullScreen) {
    document.documentElement.mozRequestFullScreen()
  }
}
/**
 * 浏览器退出全屏
 */
export const exitFullScreen = () => {
  if (document.documentElement.requestFullScreen) {
    document.exitFullScreen()
  } else if (document.documentElement.webkitRequestFullScreen) {
    document.webkitCancelFullScreen()
  } else if (document.documentElement.mozRequestFullScreen) {
    document.mozCancelFullScreen()
  }
}
/**
 * 递归寻找子类的父类
 */

export const findParent = (menu, id) => {
  for (let i = 0; i < menu.length; i++) {
    if (menu[i].children.length != 0) {
      for (let j = 0; j < menu[i].children.length; j++) {
        if (menu[i].children[j].id == id) {
          return menu[i]
        } else {
          if (menu[i].children[j].children.length != 0) {
            return findParent(menu[i].children[j].children, id)
          }
        }
      }
    }
  }
}
/**
 * 判断2个对象属性和值是否相等
 */

/**
 * 动态插入css
 */

export const loadStyle = url => {
  const link = document.createElement('link')
  link.type = 'text/css'
  link.rel = 'stylesheet'
  link.href = url
  const head = document.getElementsByTagName('head')[0]
  head.appendChild(link)
}
/**
 * 判断路由是否相等
 */
export const diff = (obj1, obj2) => {
  delete obj1.close
  const o1 = obj1 instanceof Object
  const o2 = obj2 instanceof Object
  if (!o1 || !o2) { /*  判断不是对象  */
    return obj1 === obj2
  }

  if (Object.keys(obj1).length !== Object.keys(obj2).length) {
    return false
    // Object.keys() 返回一个由对象的自身可枚举属性(key值)组成的数组,例如：数组返回下表：let arr = ["a", "b", "c"];console.log(Object.keys(arr))->0,1,2;
  }

  for (const attr in obj1) {
    const t1 = obj1[attr] instanceof Object
    const t2 = obj2[attr] instanceof Object
    if (t1 && t2) {
      return diff(obj1[attr], obj2[attr])
    } else if (obj1[attr] !== obj2[attr]) {
      return false
    }
  }
  return true
}
/**
 * 生成随机len位数字
 */
export const randomLenNum = (len, date) => {
  let random = ''
  random = Math.ceil(Math.random() * 100000000000000).toString().substr(0, len || 4)
  if (date) random = random + Date.now()
  return random
}
/**
 * 打开小窗口
 */
export const openWindow = (url, title, w, h) => {
  // Fixes dual-screen position                            Most browsers       Firefox
  const dualScreenLeft = window.screenLeft !== undefined ? window.screenLeft : screen.left
  const dualScreenTop = window.screenTop !== undefined ? window.screenTop : screen.top

  const width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width
  const height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height

  const left = ((width / 2) - (w / 2)) + dualScreenLeft
  const top = ((height / 2) - (h / 2)) + dualScreenTop
  const newWindow = window.open(url, title, 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=yes, copyhistory=no, width=' + w + ', height=' + h + ', top=' + top + ', left=' + left)

  // Puts focus on the newWindow
  if (window.focus) {
    newWindow.focus()
  }
}

// 常用工具类，注册到了vue属性中，直接使用this.U即可
export const U = {
  isObject: function(input) {
    return Object.prototype.toString.call(input) === '[object Object]'
  },
  isPlainObject: function(obj) {
    let prototype
    return Object.prototype.toString.call(obj) === '[object Object]' &&
      (prototype = Object.getPrototypeOf(obj), prototype === null ||
      prototype === Object.getPrototypeOf({}))
  },
  isArray: function(input) {
    return input instanceof Array || Object.prototype.toString.call(input) === '[object Array]'
  },
  isDate: function(input) {
    return input instanceof Date || Object.prototype.toString.call(input) === '[object Date]'
  },
  isNumber: function(input) {
    return input instanceof Number || Object.prototype.toString.call(input) === '[object Number]'
  },
  isString: function(input) {
    return input instanceof String || Object.prototype.toString.call(input) === '[object String]'
  },
  isBoolean: function(input) {
    return typeof input === 'boolean'
  },
  isFunction: function(input) {
    return typeof input === 'function'
  },
  isNull: function(input) {
    return input === undefined || input === null
  },
  isEmpty: function(input) {
    return input === undefined || input === null || input === ''
  },
  copy(data) {
    return deepClone(data)
  },
  dataUtil: {
    formatDate: function(date, fmt) {
      if (!date) return date
      if (!(date instanceof Date)) {
        const timestamp = Number(date)
        if (isNaN(timestamp)) return date
        date = new Date(timestamp)
      }
      const o = {
        'M+': date.getMonth() + 1,
        'd+': date.getDate(),
        'h+': date.getHours(),
        'm+': date.getMinutes(),
        's+': date.getSeconds(),
        'q+': Math.floor((date.getMonth() + 3) / 3),
        'S': date.getMilliseconds()
      }
      if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length))
      }
      for (const k in o) {
        if (new RegExp('(' + k + ')').test(fmt)) {
          fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (('00' + o[k]).substr(('' + o[k]).length)))
        }
      }
      return fmt
    }
  },
  unique(arr, u_key) {
    const map = new Map()
    arr.forEach((item, index) => {
      if (!map.has(item[u_key])) {
        map.set(item[u_key], item)
      }
    })
    return [...map.values()]
  },
  sleep: (time) => {
    return new Promise((resolve) => setTimeout(resolve, time))
  },
  getPosition(callback) {
    const that = this
    if ('geolocation' in navigator) {
      const mapObj = new AMap.Map('', {})
      let geolocation
      mapObj.plugin(['AMap.Geolocation'], function() {
        geolocation = new AMap.Geolocation({
          enableHighAccuracy: true, //  是否使用高精度定位，默认:true
          timeout: 10000, //  超过10秒后停止定位，默认：无穷大
          maximumAge: 0, // 定位结果缓存0毫秒，默认：0
          convert: true, // 自动偏移坐标，偏移后的坐标为高德坐标，默认：true
          showButton: true, //  显示定位按钮，默认：true
          buttonPosition: 'LB', // 定位按钮停靠位置，默认：'LB'，左下角
          buttonOffset: new AMap.Pixel(10, 20), //  定位按钮与设置的停靠位置的偏移量，默认：Pixel(10, 20)
          showMarker: true, //  定位成功后在定位到的位置显示点标记，默认：true
          showCircle: true, //  定位成功后用圆圈表示定位精度范围，默认：true
          panToLocation: true, //  定位成功后将定位到的位置作为地图中心点，默认：true
          zoomToAccuracy: true //  定位成功后调整地图视野范围使定位位置及精度范围视野内可见，默认：false
        })
        mapObj.addControl(geolocation)
        geolocation.getCurrentPosition()
      })
      AMap.event.addListener(geolocation, 'complete', res => {
        console.log(res)
        // 经度
        const lng = res.position.lng
        // 维度
        const lat = res.position.lat

        const position = {
          lng: lng,
          lat: lat
        }

        const apiParams = {
          key: '7426e1658f3660a8e36663c95c4f1fae',
          location: lng + ',' + lat,
          locations: lng + ',' + lat,
          coordsys: ''
        }
        // 坐标转换
        const convertApi = 'https://restapi.amap.com/v3/assistant/coordinate/convert?parameters'
        R.get(convertApi, apiParams).then(convertResult => {
          const api = 'https://restapi.amap.com/v3/geocode/regeo?parameters'
          const convertlng = convertResult.locations.split(',')[0]
          const convertlat = convertResult.locations.split(',')[1]
          apiParams.location = convertlng + ',' + convertlat

          R.get(api, apiParams).then(r => {
            const province = r.regeocode.addressComponent.province
            const city = r.regeocode.addressComponent.city
            const cityCode = r.regeocode.addressComponent.citycode
            // 省份
            position.province = province
            // 城市
            position.city = city
            // 城市编码
            position.cityCode = cityCode
            // 获取IP

            axios.get('/ip/cityjson', {}).then(ipInfo => {
              // ip地址
              const ipAddr = JSON.parse(ipInfo.data.replace('let returnCitySN = ', '').replace(';', '')).cip
              position.ipAddr = ipAddr.trim()
              callback(position)
            }).catch((error) => {
              console.error(error)
              callback(position)
            })
          })
        })
      })

      /*  navigator.geolocation.getCurrentPosition((res) => {

        })*/
    } else {
      this.$message('位置服务不可用')
      callback({})
    }
  },
  isMobile() {
    return navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i)
  },
  /**
   * 计算时间间隔
   * @param date1
   * 开始时间
   * @param date2
   * 结束时间
   */
  getTimeInterval(date1, date2) {
    const date3 = date2.getTime() - date1.getTime() // 时间差秒
    // 计算出相差天数
    const days = Math.floor(date3 / (24 * 3600 * 1000))

    // 计算出小时数
    const leave1 = date3 % (24 * 3600 * 1000) // 计算天数后剩余的毫秒数
    const hours = Math.floor(leave1 / (3600 * 1000))

    // 计算相差分钟数
    const leave2 = leave1 % (3600 * 1000) // 计算小时数后剩余的毫秒数
    const minutes = Math.floor(leave2 / (60 * 1000))

    // 计算相差秒数
    const leave3 = leave2 % (60 * 1000) // 计算分钟数后剩余的毫秒数
    const seconds = Math.round(leave3 / 1000)
    return {
      days: days > 0 ? days : 0,
      hours: hours > 0 ? hours : 0,
      minutes: minutes > 0 ? minutes : 0,
      seconds: seconds > 0 ? seconds : 0,
      allSeconds: Math.floor((date2 - date1) / 1000)
    }
  },
  randomString(e) {
    e = e || 32
    const t = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678'
    const a = t.length
    let n = ''
    for (let i = 0; i < e; i++) n += t.charAt(Math.floor(Math.random() * a))
    return n
  },
  cartesianProductOf() {
    return Array.prototype.reduce.call(arguments, function(a, b) {
      const ret = []
      a.forEach(function(a) {
        b.forEach(function(b) {
          ret.push(a.concat([b]))
        })
      })
      return ret
    }, [[]])
  },

  toTreeData(source, id, parentId, children) {
    const cloneData = JSON.parse(JSON.stringify(source))
    return cloneData.filter(father => {
      const branchArr = cloneData.filter(child => father[id] === child[parentId])
      branchArr.length > 0 ? father[children] = branchArr : ''
      return father[parentId] === -1 || father[parentId] === 0 || father[parentId] === null // 如果第一层不是parentId=0，请自行修改
    })
  },
  /**
   * 对象在数组中的索引位置
   * @param arr
   * @param obj
   * @param key 数组里对象的唯一标识key
   * @returns {number}
   */
  objIndexOf(arr, obj, key) {
    let index = -1
    for (let i = 0; i < arr.length; i++) {
      if (arr[i][key] === obj[key]) {
        index = i
      }
    }
    return index
  },
  /**
   * 判断是否有权限
   * @param limitId
   * 权限ID 单个ID或数组
   * @returns {boolean}
   */
  hasPermission(limitId) {
    const pms = user.state._user.permissions
    let filter = []
    if (typeof (limitId) === 'number') {
      filter = pms.filter(limit => parseInt(limit['purviewId']) === parseInt(limitId))
    } else {
      filter = pms.filter(limit => limitId.indexOf(parseInt(limit['purviewId'])) > -1)
    }
    // todo 权限编码匹配

    return filter.length > 0
  },
  /**
   * 判断是否具有多个权限(OR关系)
   * @param limitIds
   * 权限ID数组
   * @returns {boolean}
   */
  hasPermissions(limitIds) {
    let has = false
    limitIds.forEach(item => {
      if (this.hasPermission(item)) {
        has = true
      }
    })
    return has
  },
  /**
   * 下载文件
   * @param api
   * 请求地址,必填
   * @param method
   * 请求方式:只支持GET或POST,,可选,默认GET
   * @param params
   * 请求参数:GET请求追加在URL后,POST放在body中,可选
   * @param headers
   * 请求头,可选
   */
  download(api, method, params, headers) {
    let r
    if (method.toLowerCase() === 'post') {
      r = axios.post(api, params, {
        responseType: 'blob',
        headers: {
          'Content-Type': 'application/json; application/octet-stream',
          ...headers
        }
      })
    } else if (!method || method.toLowerCase() === 'get') {
      r = axios.post(api, {
        params: params,
        responseType: 'blob',
        headers: {
          'Content-Type': 'application/json; application/octet-stream',
          ...headers
        }
      })
    } else {
      console.error('只支持以GET或POST请求方式下载')
      return
    }

    r.then((res) => {
      const { data, headers } = res
      if (headers['error-msg']) {
        this.$message({
          type: 'error',
          message: decodeURIComponent(headers['error-msg']).replace(/[+]/g, ' ')
        })
        return false
      }
      const fileName = headers['content-disposition'].replace(/\w+; filename=(.*)/, '$1')
      const blob = new Blob([data], { type: 'application/vnd.ms-excel' })
      const dom = document.createElement('a')
      const url = window.URL.createObjectURL(blob)
      dom.href = url
      dom.download = decodeURI(fileName)
      dom.style.display = 'none'
      document.body.appendChild(dom)
      dom.click()
      dom.parentNode.removeChild(dom)
      window.URL.revokeObjectURL(url)
    }).catch((err) => {
      this.$message({
        type: 'error',
        message: err
      })
    })
  }

}
