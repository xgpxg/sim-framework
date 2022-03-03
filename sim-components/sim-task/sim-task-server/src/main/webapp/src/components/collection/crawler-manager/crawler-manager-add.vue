<!--新增爬虫组件-->
<style lang='less'>
</style>
<template>
  <div class="search-list-vue frame-page h-panel">
    <div class="h-panel-bar"><span class="h-panel-title">新建爬虫</span></div>
    <div class="h-panel-bar">
      <Steps :datas="stepData" :step="step" style="margin-left: 30px;margin-right: 30px"></Steps>
    </div>
    <div class="h-panel-body">
      <div v-show="step===0">
        <Row :space="50">
          <Cell width="14">
            <Form ref="crawlerInfoForm" :label-width="110" :model="formData.crawlerConfig" :rules="rules" >
              <FormItem label="爬虫名称" prop="crawlerName">
                <input type="text" v-model="formData.crawlerConfig.crawlerName"/>
              </FormItem>
              <FormItem label="网站名称" prop="websiteName">
                <input type="text" v-model="formData.crawlerConfig.websiteName"/>
              </FormItem>
              <FormItem label="源地址" prop="baseUrl">
                <input type="text" v-model="formData.crawlerConfig.baseUrl"/>
              </FormItem>
              <FormItem label="地址规则" prop="matchUrl">
                <input type="text" v-model="formData.crawlerConfig.matchUrl"/>
              </FormItem>
              <FormItem label="源站类型" prop="websiteType">
                <Select v-model="formData.crawlerConfig.websiteType" :datas="this.initData.websiteType"></Select>
              </FormItem>
              <FormItem label="采集模式" prop="crawlMode">
                <Select v-model="formData.crawlerConfig.crawlMode" :datas="initData.crawlMode"></Select>
              </FormItem>
              <FormItem label="是否共享" prop="isShare">
                <div>
                  <h-switch v-model="formData.crawlerConfig.shareType" trueValue="1" falseValue="0"
                            :small="false"></h-switch>
                </div>
              </FormItem>
              <p class="text-right">
                <button class="h-btn h-btn-blue" color="primary" @click="nextStep">下一步</button>
              </p>
            </Form>
          </Cell>
          <Cell width="10">
            <div>说明</div>
          </Cell>
        </Row>
      </div>
      <div v-show="step===1">
        <Row :space-x="50" :space-y="5">
          <Cell width="14" :loading="true">
            <Form ref="fieldRuleForm" :label-width="110" :model="formData.fieldConfig" :rules="rules">
              <FormItem label="字段名称" prop="fieldName">
                <input type="text" v-model="formData.fieldConfig.fieldName"/>
              </FormItem>
              <FormItem label="字段规则" prop="fieldRule">
                <textarea type="text" v-model="formData.fieldConfig.fieldRule" rows="3"></textarea>
              </FormItem>
              <FormItem label="字段分组" prop="fieldRule">
                <textarea type="text" v-model="formData.fieldConfig.fieldGroup" rows="3"></textarea>
              </FormItem>
              <FormItem label="采集类型" prop="fieldType">
                <Select v-model="formData.fieldConfig.fieldType" :datas="initData.fieldType"></Select>
              </FormItem>
              <FormItem label="属性值" prop="attrValue" v-if="formData.websiteType==='5000'">
                <input type="text"/>
              </FormItem>
              <FormItem label="规则描述" prop="fieldDesc">
                <textarea type="text" v-model="formData.fieldConfig.fieldDesc"></textarea>
              </FormItem>
              <FormItem>
                <button class="h-btn" @click="saveField"><i class="h-icon-inbox"></i> 保存字段</button>
                <button class="h-btn" @click="andAddNewField"><i class="h-icon-inbox"></i> 添加新字段</button>
              </FormItem>
              <p class="text-right">
                <button class="h-btn h-btn-blue" @click="lastStep">上一步</button>
                <button class="h-btn h-btn-blue" @click="nextStep">下一步</button>
              </p>
            </Form>
          </Cell>
          <Cell width="10">
            <button class="h-btn h-btn-blue h-btn-s" @click="importFieldRule" v-if="type==='ADD'">导入字段规则</button>
            <Modal v-model="importFieldRuleOpened" :closeOnMask="false">
              <div slot="header">导入字段规则</div>
              <div style="width: 600px">
                <Form :ref="'fieldForm'" :label-width="0">
                  <FormItem>
                    <textarea rows="10" v-model="importFieldRuleData" placeholder="请粘贴从采集插件复制的内容"></textarea>
                  </FormItem>
                </Form>
              </div>
              <div slot="footer">
                <button class="h-btn" color="primary" @click="cancel">关闭</button>
                <button class="h-btn h-btn-blue" @click="okImportFieldRule">确认</button>
              </div>
            </Modal>
            <p></p>
            <Table :datas="submitData.fieldRule" :border="true" :stripe="true" :loading="false" :height="400">
              <TableItem title="序号" prop="$serial" :width="60"></TableItem>
              <TableItem title="字段名称" prop="fieldName" :align="'center'"></TableItem>
              <TableItem title="字段分组" prop="fieldGroup" :align="'center'"></TableItem>
              <TableItem title="操作" :width="130" :align="'center'">
                <template slot-scope="{data}">
                  <button class="h-btn h-btn-xs h-btn-red" @click="remove(submitData.fieldRule, data)">删除</button>
                  <button class="h-btn h-btn-xs h-btn-blue" @click="edit(submitData.fieldRule, data)">修改</button>
                </template>
              </TableItem>
              <div slot="empty">暂无数据</div>
            </Table>
            <p></p>
            <small class="gray-color">已配置 {{submitData.fieldRule.length}} 个字段，最大支持100个采集字段</small>
          </Cell>
        </Row>
      </div>
      <div v-show="step===2">
        <Row :space-x="50" :space-y="5">
          <Cell width="14" :loading="true">
            <div class="h-panel h-panel-no-border">
              <div class="h-panel-bar">
                <span class="h-panel-title" v-font="16">爬虫配置信息</span>
              </div>
              <div class="h-panel-body">
                <Form :readonly="true">
                  <FormItem label="爬虫名称">{{formData.crawlerConfig.crawlerName}}</FormItem>
                  <FormItem label="源地址" prop="baseUrl" class="text-ellipsis" :title="formData.crawlerConfig.baseUrl">
                    {{formData.crawlerConfig.baseUrl}}
                  </FormItem>
                  <FormItem label="地址规则" prop="baseUrl" class="text-ellipsis" :title="formData.crawlerConfig.baseUrl">
                    {{formData.crawlerConfig.matchUrl}}
                  </FormItem>
                  <FormItem label="源站类型" prop="websiteType">{{formData.crawlerConfig.websiteType}}</FormItem>
                  <FormItem label="是否共享" prop="isShare">{{formData.crawlerConfig.shareType==='1'?'共享':'私有'}}</FormItem>
                </Form>
              </div>
            </div>
            <div class="h-panel h-panel-no-border">
              <div class="h-panel-bar">
                <span class="h-panel-title" v-font="16">字段配置信息</span>
              </div>
              <div class="h-panel-body">
                <Table :datas="submitData.fieldRule" :stripe="true" :loading="false" border>
                  <TableItem title="序号" prop="$serial" :width="60"></TableItem>
                  <TableItem title="字段名称" prop="fieldName" :align="'center'"></TableItem>
                  <TableItem title="字段分组" prop="fieldGroup" :align="'center'"></TableItem>
                  <div slot="empty">暂无数据</div>
                </Table>
              </div>
            </div>
            <p class="text-right">
              <button class="h-btn h-btn-blue" @click="lastStep">上一步</button>
              <button class="h-btn h-btn-blue" @click="submit">提交</button>
            </p>
          </Cell>
          <Cell width="10">
            <div></div>
          </Cell>
        </Row>
      </div>
      <div v-show="step===3">
        <Row :space-x="50" :space-y="5">
          <Cell width="24" :loading="true">
            <p class="text-center">
              <h-button noBorder class="h-icon-completed" text-color="green" style="font-size: 150px;"></h-button>
              <br>
              <br>
              <span>恭喜，爬虫配置成功</span>，快去
              <h-button noBorder text-color="blue">
                <router-link :to="{name:'CreateTask'}">
                  创建采集任务
                </router-link>
              </h-button>
              开始采集数据吧

            </p>
            <button class="h-btn h-btn-blue" @click="lastStep">上一步</button>

          </Cell>
        </Row>
      </div>
    </div>
    <div class="h-panel-bar">
    </div>
  </div>
</template>
<script>
  import Button from "heyui/lib";
  import Utils from '../../../js/common/utils';
  import Request from "../../../js/common/request";
  import Ajax from "../../../js/common/ajax";

  export default {
    components: {Button},
    data() {
      return {
        stepData: {
          0: '基本信息配置',
          1: '采集字段配置',
          2: '配置总览',
          3: '完成'
        },
        step: 0,
        initData: {
          websiteType: [],
          timeInterval: [{itemCode: 500, itemText: '0.5秒'}, {itemCode: 1000, itemText: '1秒'}, {
            itemCode: 2000,
            itemText: '2秒'
          }],
          fieldType: [],
          crawlMode:[],
        },
        type:'ADD',//新增或编辑模式:ADD新增 MOD修改
        required: true,
        importFieldRuleOpened: false,
        importFieldRuleData: null,
        formData: {
          crawlerConfig: {
            crawlerName: null,
            baseUrl: null,
            matchUrl: null,
            websiteType: null,
            shareType: 0,
            crawlMode: null,
            websiteName: null
          },
          fieldConfig: {
            fieldName: null,
            fieldRule: null,
            fieldDesc: null,
            fieldGroup: null,
            fieldType: null
          }
        },
        rules: {
          //url: ['baseUrl'],
          required: ['crawlerName','websiteName','baseUrl','matchUrl','websiteType', 'fieldCode', 'fieldName', 'fieldRule','crawlMode']
        },
        submitData: {
          crawlerConfig: {},
          fieldRule: [],
        },
      };
    },
    created() {
      let that = this;
      //加载采集网站类型
      Request.Dict.get('website_type').then((res) => {
        that.initData.websiteType = res.data || [];
      });
      //加载采集字段类型
      Request.Dict.get('field_type').then((res) => {
        that.initData.fieldType = res.data || [];
      });
      //加载采集模式
      Request.Dict.get("crawl_mode").then((res)=>{
        that.initData.crawlMode = res.data;
      });
    },
    mounted() {
      let that = this;
      let crawlerId = this.$route.query.crawlerId;
      //如果crawlerId不为空,则为编辑
      if(!Utils.isEmpty(crawlerId)){
        that.type = 'MOD';
        //加载数据
        Ajax.get(`/manager/crawler/qryCrawlerDetail/${crawlerId}`,this.params).then((res)=>{
          console.log(res)
          that.formData.crawlerConfig = res.data;
        });
        Ajax.get(`/manager/crawler/qryCrawlerField/${crawlerId}`,this.params).then((res)=>{
          console.log(res)
          that.submitData.fieldRule = res.data;
        });
      }
    },
    watch: {
      formData() {
        console.log(this.formData);
      }
    },
    methods: {
      nextStep() {
        if (this.step === 0) {
          let crawlerInfoValidResult = this.$refs.crawlerInfoForm.valid();
          if (!crawlerInfoValidResult.result && this.submitData.fieldRule.length === 0) {
            return;
          }
          this.submitData.crawlerConfig = this.formData.crawlerConfig;
        }
        if (this.step === 1) {
          let fieldRuleValidResult = this.$refs.fieldRuleForm.valid();
          if (!fieldRuleValidResult.result && this.submitData.fieldRule.length === 0) {
            return;
          }
        }
        this.step = this.step < 3 ? (this.step + 1) : this.step;
      },
      lastStep() {
        this.step = this.step > 0 ? (this.step - 1) : this.step;
      },
      saveField() {
        let that = this;
        let validResult = this.$refs.fieldRuleForm.valid();
        if (!validResult.result) {
          return;
        }
        let data = Utils.copy(this.formData.fieldConfig);
        let exits = false;
        //验证是否已存在
        for (let index in this.submitData.fieldRule) {
          let d = this.submitData.fieldRule[index];
          if (d.fieldName === data.fieldName) {
            exits = true;
            break;
          }
          if (d.fieldRule === data.fieldRule) {
            exits = true;
            break;
          }
        }
        //字段不存在
        if (!exits) {
          this.submitData.fieldRule.push(data);
          if(this.type==='ADD') {
            this.$Message({type: 'success', text: '添加成功'});
            this.andAddNewField();
          }else if(this.type==='MOD'){
            Ajax.postJson('/manager/crawler/addOneField', that.formData.fieldConfig).then((res) => {
              console.log(res);
              if (res.code === 1) {
                this.$Message({type: 'success', text: res.data});
                this.andAddNewField();
              }
            })
          }
        }
        //字段已存在
        else {
          if(this.type==='ADD') {
            this.$Message({type: 'success', text: '修改成功'});
          }else if(this.type==='MOD'){
            Ajax.postJson('/manager/crawler/updateField', that.formData.fieldConfig).then((res) => {
              console.log(res);
              if (res.code === 1) {
                this.$Message({type: 'success', text: res.data});
              }
            })
          }
        }
      },
      andAddNewField() {
        this.formData.fieldConfig = {
          fieldName: null,
          fieldRule: null,
          fieldDesc: null,
          fieldGroup: null
        };
      },
      submit() {
        let that = this;
        if(that.type==='ADD') {
          //新增爬虫
          Ajax.postJson('/manager/crawler/createCrawler', this.submitData).then((res) => {
            console.log(res);
            if (res.code === 1) {
              that.nextStep();
            }
          })
        }else if(that.type==='MOD'){
          //修改爬虫
          Ajax.postJson('/manager/crawler/updateCrawler', this.submitData).then((res) => {
            console.log(res);
            if (res.code === 1) {
              that.nextStep();
            }
          })
        }
      },
      reset() {
        this.$refs.form.resetValid();
      },
      importFieldRule() {
        if (this.submitData.fieldRule.length > 0) {
          this.$Confirm('导入数据将会覆盖现有数据，确定导入？', '提示', {
            distinguishCancelAndClose: true,
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.importFieldRuleOpened = true;
          }).catch(() => {
          });
        } else {
          this.importFieldRuleOpened = true;
        }
      },
      okImportFieldRule() {
        if (Utils.isJSON(this.importFieldRuleData)) {
          let res = JSON.parse(this.importFieldRuleData);
          this.submitData.fieldRule = res;
          this.parseJsonData();
          this.importFieldRuleOpened = false;
        } else this.$Message({type: 'warn', text: '数据格式有误'});
      },
      cancel() {
        this.importFieldRuleOpened = false;
      },
      edit(datas, data) {
        this.formData.fieldConfig = data;
      },
      remove(datas, data) {
        if(this.type==='ADD') {
          datas.splice(datas.indexOf(data), 1);
        }else if(this.type==='MOD'){
          this.$Confirm('删除字段后该字段所关联的数据将无法查询或导出，确认删除吗?', '警告', {
            distinguishCancelAndClose: true,
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            Ajax.post('/manager/crawler/delField', {fieldId:data.id}).then((res) => {
              console.log(res);
              if (res.code === 1) {
                datas.splice(datas.indexOf(data), 1);
                this.$Message({type: 'success', text: res.data});
              }
            })
          }).catch(()=>{

          });
        }
      },
      //json数据转换，将{group_0:[{},{}}]}转换为[{group:'group_0'}]的形式
      parseJsonData() {
        let fieldRuleObj = this.submitData.fieldRule;
        let res = [];
        for (let key in fieldRuleObj) {
          let ruleArr = fieldRuleObj[key];
          for (let index in ruleArr) {
            let rule = ruleArr[index];
            rule.fieldGroup = key;
            res.push(rule);
          }
        }
        console.log(res);
        this.submitData.fieldRule = res;
      }
    },
    computed: {},
    beforeRouteLeave: function (to, from, next) {
      if (this.step !== 3) {
        next(false);
        this.$Confirm('您将离开本页面，当前数据还未保存,将会丢失数据', '提示', {
          distinguishCancelAndClose: true,
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          next(true)
        }).catch(() => {
          next(false)
        });
      } else {
        next(true);
      }
    }
  };
</script>
