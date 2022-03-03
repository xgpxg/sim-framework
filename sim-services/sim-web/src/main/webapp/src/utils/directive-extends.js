//自定义指令
//######已废弃#####/////

import {U} from "./util";
import user from '../store/modules/user';

export default {
  /**
   * 封装个权限 v-permission
   * 参数：权限ID(或id数组)或一个对象{id:xxx(或id数组),type:'hide'}
   * type: hide隐藏元素 disabled禁用元素 默认disabled
   * 多个权限之间 或 关系
   */
  permission: {
    bind(el, binding, vnode) {
    },
    inserted(el, binding, vnode) {
      let pms = user.state._user.permissions;
      if (!pms || pms.length === 0) {

      } else {
        binding.def.permission(el, binding, vnode)
      }
    },
    update(el, binding, vnode) {

    },
    permission(el, binding, vnode) {
      if (typeof (binding.value) === 'object') {
        let opt = binding.value;
        if (typeof (opt.id) === 'number') {
          binding.def.deal(el, [opt.id], opt.type);
        } else if (opt.id instanceof Array) {
          binding.def.deal(el, opt.id, opt.type);
        }
      } else {
        let id = binding.value;
        if (!U.hasPermission(id)) {
          el.disabled = 'disabled';
          el.className += ' is-disabled';
        }
      }
    },
    deal(el, ids, type) {
      if (ids) {
        if (!U.hasPermissions(ids)) {
          type = type ? type : 'disabled';
          switch (type) {
            case 'disabled':
              el.disabled = "disabled";
              el.className += ' is-disabled';
              break;
            case 'hide':
              el.style.display = 'none';
              break;
          }

        }
      }
    }
  },


}
