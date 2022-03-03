import store from '@/store'
//{code:['xxx','yyy'],type:'disabled']} or {code:'xxx',type:'disabled']}
function checkPermission(el, binding) {
  const { value } = binding;
  //const roles = store.getters && store.getters.roles
  const permissions = store.getters && store.getters.permissions.map(v=>v.purviewCode);
  if(!value){
    return;
  }
  debugger

  let hasPermission = false;
  if(typeof(value)=='string'){
    hasPermission = permissions.some(p=>p===value);
    if (!hasPermission) {
      el.parentNode && el.parentNode.removeChild(el)
    }
  }else if(value instanceof Object){
    if(value.code instanceof Array){
      hasPermission = permissions.some(p=>value.code.includes(p));
    }else{
      hasPermission = permissions.some(p=>p===value.code);
    }
    if (!hasPermission) {
      if(value.type==='disabled'){
        el.disabled = 'disabled';
        el.className += ' is-disabled';
      }else{
        el.parentNode && el.parentNode.removeChild(el)
      }
    }
  }
}

export default {
  inserted(el, binding) {
    checkPermission(el, binding)
  },
  update(el, binding) {
    checkPermission(el, binding)
  }
}
