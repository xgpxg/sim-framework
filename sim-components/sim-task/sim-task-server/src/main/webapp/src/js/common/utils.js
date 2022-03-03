import utils from 'hey-utils';

const rclass = /[\t\r\n\f]/g;

export default utils.extend({}, utils, {
  getClass(elem) {
    return (elem.getAttribute && elem.getAttribute('class')) || '';
  },
  hasClass(elem, selector) {
    let className;
    className = ` ${selector} `;
    if (elem.nodeType === 1 && (` ${this.getClass(elem)} `)
      .replace(rclass, ' ')
      .indexOf(className) > -1) {
      return true;
    }
    return false;
  },
  isEmpty(value) {
    return value === undefined || value === '' || value === null || value.length === 0;
  },
  isNotEmpty(value) {
    return !(value === undefined || value === '' || value === null || value.length === 0)
  },
  isJSON(str) {
    if (typeof str == 'string') {
      try {
        let obj = JSON.parse(str);
        if (typeof obj == 'object' && obj) {
          return true;
        } else {
          return false;
        }

      } catch (e) {
        return false;
      }
    }
  },
  getUUID() {
    let s = [];
    let hexDigits = "0123456789abcdef";
    for (let i = 0; i < 36; i++) {
      s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4";
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);
    s[8] = s[13] = s[18] = s[23] = "-";
    return s.join("");
  },
  getRandomString(length){
    if(length<=0||length>=32){
      return this.getUUID();
    }
    this.getUUID().replace("-").substring(0,length);
  }
});
