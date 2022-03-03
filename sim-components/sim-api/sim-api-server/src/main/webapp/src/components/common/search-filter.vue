<style lang='less'>
.search-filter-wrap {
  display: flex;
  margin-bottom: 10px;
  .search-picker-title {
    font-weight: bold;
    line-height: 20px;
    //color: rgba(0, 0, 0, 0.65);
    width: 80px;
    margin-right: 15px;
    color: rgba(0,0,0,.85);
  }
  .search-picker-item-group {
    flex: 1;
  }
  .picker-item {
    cursor: pointer;
    margin-right: 8px;
    padding: 3px 8px;
    border-radius: 2px;
    transition: 0.2s;
    &:hover, &.selected {
      background: @primary-color;
      color: #FFF;
    }
  }
}
</style>
<template>
  <div class="search-filter-wrap">
    <div class="search-picker-title">{{title}}</div>
    <div class="search-picker-item-group">
      <span v-if="!search"  @click="clear" class="picker-item" :class="{selected: isEmpty}">不限</span>
      <span v-if="!search" v-for="d of datas" @click="change(d)" :key="d.itemCode" :class="{selected: isSelected(d)}" class="picker-item">{{d.itemText}}</span>
      <span v-if="search" ><Search @search="searchData" v-model="text" showSearchButton></Search></span>
<!--
      <span><Select :datas="datas"></Select></span>
-->
      <slot></slot>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    title: String,
    datas: Array,
    prop: String,
    value: Object,
    multiple: {
      type: Boolean,
      default: false
    },
    search:{
      type:Boolean,
      default:false
    },
    range: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      text:null
    };
  },
  mounted() {
  },
  methods: {
    isSelected(data) {
      if (this.range) {
        return data.max == this.nowValue.max && data.min == this.nowValue.min;
      } else if (this.multiple) {
        return this.nowValue.indexOf(data.itemCode) > -1;
      } else {
        return this.nowValue == data.itemCode;
      }
    },
    change(data) {
      let result = null;
      if (this.range) {
        result = { max: data.max, min: data.min };
      } else if (this.multiple) {
        result = Utils.copy(this.nowValue);
        Utils.toggleValue(result, data.itemCode);
      } else {
        result = data.itemCode;
      }
      this.setvalue(result);
    },
    clear() {
      if (this.range) {
        this.setvalue({ min: null, max: null });
      } else if (this.multiple) {
        this.setvalue([]);
      } else {
        this.setvalue(null);
      }
    },
    setvalue(data) {
      let value = Utils.copy(this.value);
      value[this.prop] = data;
      this.$emit('input', value);
    },
    searchData(data){
      this.setvalue(data)
    }
  },
  computed: {
    isEmpty() {
      if (this.range) {
        return !this.nowValue.max && !this.nowValue.min;
      } else if (this.multiple) {
        return !this.nowValue || this.nowValue.length === 0;
      } else {
        return !this.nowValue;
      }
    },
    nowValue() {
      return this.value[this.prop];
    }
  }
};
</script>
