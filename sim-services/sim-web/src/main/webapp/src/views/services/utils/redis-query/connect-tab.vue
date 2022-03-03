<template>
  <div class="page">

    <el-form :inline="true" :model="formData" class="demo-form-inline"
             label-width="80px" :label-position="'left'" :rules="rules"
             size="small">
      <el-form-item label="数据类型" prop="dataType">
        <el-select v-model="formData.dataType" placeholder="">
          <el-option label="string" value="string"></el-option>
          <el-option label="hash" value="hash"></el-option>
          <el-option label="list" value="list"></el-option>
          <el-option label="set" value="set"></el-option>
          <el-option label="zset" value="zset"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="键值" prop="key">
        <el-input v-model="formData.key" placeholder="key"></el-input>
      </el-form-item>
      <el-form-item label="字段" v-if="formData.dataType==='hash'">
        <el-input v-model="formData.field" placeholder="hash key"
        ></el-input>

      </el-form-item>
      <el-row>
        <el-col :space="24">
          <el-form-item label="起始下标"
                        v-if="['list','zset'].indexOf(formData.dataType)>-1">
            <el-input v-model="formData.start" placeholder="起始下标"
            ></el-input>
          </el-form-item>
          <el-form-item label="结束下标"
                        v-if="['list','zset'].indexOf(formData.dataType)>-1">
            <el-input v-model="formData.end" placeholder="结束下标"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :space="24">
          <el-form-item label="格式化为">
            <el-select v-model="formData.format" placeholder=""
                       @change="formatChange">
              <el-option label="文本" value="text"></el-option>
              <el-option label="Json" value="json"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onSubmit">查询</el-button>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="drawer = true">命令行</el-button>
          </el-form-item>
        </el-col>
      </el-row>

    </el-form>
    <el-card class="box-card" :shadow="'never'">
      <el-scrollbar style="height: 300px;">
        <json-viewer v-if="formData.format === 'json'" :value="output"
                     :expand-depth=4 copyable sort></json-viewer>
        <span v-if="formData.format === 'text'">{{output}}</span>
      </el-scrollbar>
    </el-card>
    <small class="expire" v-if="expire"><span class="pr-5">TTL:</span>
      {{expire}}
    </small>

    <!--<el-input
      type="textarea"
      :rows="10"
      readonly
      v-model="output">
    </el-input>-->

    <el-drawer
      title="Redis命令行工具"
      :visible.sync="drawer"
      :show-close="true"
      :wrapperClosable="true"
      size="63%"
      :modal="false"
      :withHeader="false"
    >
      <CommandLine></CommandLine>
    </el-drawer>

  </div>
</template>

<script>
  import CommandLine from "./command-line";
  import JsonViewer from 'vue-json-viewer'

  export default {
    name: "connect-tab",
    components: {CommandLine, JsonViewer},
    data() {
      return {
        rules: {},

        formData: {
          dataType: 'string',
          key: null,
          format: 'text',
          start: 0,
          end: -1
        },


        drawer: false,
        textarea: null,

        output: null,

        expire: null
      }
    },
    methods: {
      addTab(targetName) {
        let newTabName = ++this.tabIndex + '';
        this.editableTabs.push({
          title: 'New Tab',
          name: newTabName,
          content: 'New Tab content'
        });
        this.editableTabsValue = newTabName;
      },
      removeTab(targetName) {
        let tabs = this.editableTabs;
        let activeName = this.editableTabsValue;
        if (activeName === targetName) {
          tabs.forEach((tab, index) => {
            if (tab.name === targetName) {
              let nextTab = tabs[index + 1] || tabs[index - 1];
              if (nextTab) {
                activeName = nextTab.name;
              }
            }
          });
        }

        this.editableTabsValue = activeName;
        this.editableTabs = tabs.filter(tab => tab.name !== targetName);
      },
      formatChange(val) {
        if (val === 'json') {
          this.output = JSON.parse(this.output);
        }
      },
      onSubmit() {
        let that = this;
        let reqParam = this.U.copy(this.formData);
        that.output = null;
        that.R.get('/api/cache-service/query', reqParam).then(res => {
          if (!res.data.value) {
            that.$notify({
              title: '提示',
              message: '所查询的key无数据',
              duration: 2000
            });
          }
          if (res.data.value) {
            if (that.formData.format === 'json') {
              that.output = Object.keys(JSON.parse(res.data.value)).length === 0 ? null : JSON.parse(res.data.value);
            } else {
              that.output = res.data.value;
            }
          }
          that.expire = res.data.expire;
        })
      }
    }
  }
</script>

<style scoped>
  .page {
    padding: 20px;
  }

  .box-card {
    max-width: 100%;
    min-height: 300px;
    font-size: 0.6rem;
  }

  .expire {
    float: right;
    color: #959da8;
    margin-top: 10px;
    bottom: 0;
  }

  /deep/ .el-scrollbar__wrap {
    overflow-x: hidden;
  }
</style>
