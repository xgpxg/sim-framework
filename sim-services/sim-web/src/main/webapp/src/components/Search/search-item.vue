<template>
  <span v-if="type==='empty'">
    <div>&nbsp;</div>
  </span>
  <span v-else>
    <el-form-item :label="option.label">
      <template v-if="type === 'radio'">
        <el-radio-group v-model="data" @change="change">
          <template v-for="item in option.items">
            <el-radio :label="item.itemCode">{{item.itemText}}</el-radio>
          </template>
        </el-radio-group>
      </template>
      <template v-if="type === 'radio-button'">
        <el-radio-group v-model="data" @change="change">
          <template v-for="item in option.items">
            <el-radio-button :label="item.itemCode">{{item.itemText}}
            </el-radio-button>
          </template>
        </el-radio-group>
      </template>
      <template v-if="type === 'checkbox'">
        <el-checkbox-group v-model="array" @change="change">
          <template v-for="item in option.items">
            <el-checkbox :label="item.itemCode">{{item.itemText}}</el-checkbox>
          </template>
        </el-checkbox-group>
      </template>
      <template v-if="type === 'checkbox-button'">
        <el-checkbox-group v-model="array" @change="change">
          <template v-for="item in option.items">
            <el-checkbox-button :label="item.itemCode">{{item.itemText}}
            </el-checkbox-button>
          </template>
        </el-checkbox-group>
      </template>
      <template v-if="type === 'select'">
        <el-select v-model="data"
                   :placeholder="option.placeholder?option.placeholder:'请选择'"
                   @change="change">
          <el-option
            v-for="item in option.items"
            :key="item.itemCode"
            :label="item.itemText"
            :value="item.itemCode">
          </el-option>
        </el-select>
      </template>
      <template v-if="type === 'input'">
        <el-input v-model="data" :placeholder="option.placeholder">
        </el-input>
      </template>
    </el-form-item>
  </span>
</template>

<script>
  export default {
    name: "search-item",
    props: {
      option: {
        type: Object
      }
    },
    data() {
      return {
        data: '',
        array: ['']
      }
    },
    computed: {
      type() {
        return this.option.type;
      }
    },
    watch: {
      data: {
        handler(newVal) {
          this.$emit('input', newVal);
        }
      },
      array: {
        handler(newVal) {
          this.$emit('input', newVal.filter(item => !!item));
        },
        deep: true
      }
    },
    created() {
      if (this.option.code) {
        this.R.getDict(this.option.code).then(res => {
          this.option.items = res.data;
        })
      }
    },
    methods: {
      change(val) {
        if (this.option.change) {
          if (['radio', 'radio-button', 'select', 'input'].indexOf(this.type) > -1) {
            this.dataChange(val);
            this.option.change(val);
          } else if (['checkbox', 'checkbox-button'].indexOf(this.type) > -1) {
            this.arrayChange(val);
            this.option.change(val.filter(item => !!item));
          }
        }

      },
      dataChange(val) {

      },
      arrayChange(val) {
        //全选点击
        if (!val[val.length - 1]) {
          this.array.splice(0, this.array.length);
          this.array.push('');
        }
        //非全选点击
        else {
          //全选取消选中
          if (this.array.indexOf('') > -1) {
            this.array.splice(this.array.indexOf(''), 1);
          }
        }
      },
      reset() {
        if (['radio', 'radio-button', 'select', 'input'].indexOf(this.type) > -1) {
          this.data = this.option.initValue ? this.option.initValue : '';
        } else if (['checkbox', 'checkbox-button'].indexOf(this.type) > -1) {
          this.array = this.option.initValue ? this.option.initValue : [''];
          if (this.array.length === 0) {
            this.array.push('');
          }
        }
      }
    }
  }
</script>

<style scoped>

</style>
