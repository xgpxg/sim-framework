<template>
  <el-form :size="size" :inline="inline" :model="form" :label-width="labelWidth"
           :label-position="labelPosition">
    <template v-for="option in options">
      <search-item :ref="option.prop" v-model="form[option.prop]"
                   :option="option"></search-item>
    </template>
    <template>
      <slot></slot>
    </template>
    <template>
      <el-form-item>
        <el-button v-if="showSearchButton" @click="search" type="primary"
                   :size="size">
          <svg-icon icon-class="search"></svg-icon>
          查询
        </el-button>
        <el-button v-if="showResetButton" @click="reset" type="" :size="size">
          <svg-icon icon-class="refresh"></svg-icon>
          重置
        </el-button>
        <el-button v-if="showAddButton" @click="add" type="success"
                   :size="size">
          <svg-icon icon-class="add2"></svg-icon>
          新增
        </el-button>
        <slot name="button"></slot>
      </el-form-item>
    </template>
  </el-form>
</template>

<script>
  import SearchItem from "./search-item";


  export default {
    name: "search",
    components: {SearchItem},
    props: {
      options: {
        type: Array
      },
      size: {
        type: String,
        default: 'small'
      },
      inline: {
        type: Boolean,
        default: true
      },
      labelWidth: {
        type: String,
        default: 'auto'
      },
      labelPosition: {
        type: String,
        default: 'right'
      },
      showSearchButton: {
        type: Boolean,
        default: true
      },
      showResetButton: {
        type: Boolean,
        default: true
      },
      showAddButton: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        form: {}
      }
    },
    watch: {
      form: {
        handler(newVal) {
          this.$emit('input', newVal);
        },
        deep: true
      }
    },
    methods: {
      search() {
        this.$emit('search', {...this.form})
      },
      reset() {
        let that = this;
        this.options.forEach(item => {
          that.$refs[item.prop][0].reset();
        })
      },
      add() {
        this.$emit('add', {...this.form})
      }
    }
  }
</script>

<style scoped>

</style>
