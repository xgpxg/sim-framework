<template>
  <div>
    <el-form size="mini" label-width="auto" label-position="left">
      <div v-if="type==='input'">
        <el-form-item :label="label">
          <el-input v-model="filterText" @change="inputChange"
                    :placeholder="placeholder" style="width: 50%">
          </el-input>
          <span class="ml10">
            <el-button type="primary" @click="search">搜索</el-button>
            <el-button @click="reset">重置</el-button>
          </span>
        </el-form-item>
      </div>
      <div v-if="type==='radio'">
        <el-form-item :label="label">
          <el-radio-group v-model="value" size="mini">
            <el-radio-button v-for="d in data" :label="d.itemCode">
              {{d.itemText}}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>
      </div>
      <div v-if="type==='checkbox'">
        <el-form-item :label="label">
          <el-checkbox-group v-model="arrayValue">
            <el-checkbox-button v-for="(d,idx) in data" v-if="idx===0"
                                :label="d.itemCode" @change="checkAll">
              {{d.itemText}}
            </el-checkbox-button>
            <el-checkbox-button v-for="(d,idx) in data" v-if="idx>0"
                                :label="d.itemCode" @change="checkboxChange">
              {{d.itemText}}
            </el-checkbox-button>
          </el-checkbox-group>
        </el-form-item>
      </div>
      <div v-if="type==='select'">
        <el-form-item :label="label">
          <el-select v-model="value" placeholder="请选择">
            <el-option v-for="d in data" :label="d.itemText"
                       :value="d.itemCode"></el-option>
          </el-select>
        </el-form-item>
      </div>
    </el-form>
    <!--{{value}}-->
  </div>
</template>

<script>
  export default {
    name: "SearchBarItem",
    props: {
      type: {
        type: String,
        default: 'radio'
      },
      data: {
        type: Array
      },
      label: {
        type: String
      },
      code: {
        type: String,
        default: 'itemCode'
      },
      text: {
        type: String,
        default: 'itemText'
      },
      placeholder: {
        type: String,
        default: ''
      }
    },
    data() {
      return {
        value: null,
        arrayValue: [],
        filterText: ''
      }
    },
    watch: {
      value(newValue, oldValue) {
        this.$emit('input', newValue)
      },
      filterText(newValue, oldValue) {
        this.$emit('input', newValue)
      }
    },
    methods: {
      /**
       * 输入框值变化
       * @param value
       */
      inputChange(value) {
        this.$emit('input', value)
      },
      /**
       * 多选框值变化
       * @param value true或false
       */
      checkboxChange(value) {
        //选中全选
        if (this.arrayValue.length === this.data.length - 1 && value) {
          this.arrayValue = this.data.map(item => item.itemCode);
        }
        //取消选中全选
        else if (this.arrayValue.length === this.data.length - 1 && !value) {
          this.arrayValue.splice(this.arrayValue.indexOf(''), 1);
        }
        this.$emit('input', this.arrayValue)
      },
      /**
       * 多选框全选/取消全选
       */
      checkAll() {
        if (this.arrayValue.length === this.data.length - 1) {
          this.arrayValue = [];
        } else {
          this.arrayValue = this.data.map(item => item.itemCode);
        }
        this.$emit('input', this.arrayValue)
      },
      search(){
        this.$emit('input', this.filterText)
      },
      reset(){
        this.$emit('reset')
      }
    }
  }
</script>

<style scoped>
  span.el-radio-button__inner {
    background-color: #8b63f9 !important;
  }

  /deep/ .el-form-item__label {
    color: grey;
    font-weight: 600;

  }
</style>
