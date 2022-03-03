sim-el-table-plus是对element-ui table的二次封装，增加了以下功能：

- 集成分页组件
- 表格列可配置化
- 操作栏列可配置化
- 动态调整分页大小，自适应屏幕高度分页
- 支持复杂表头
- 支持按照指定的key自动合并行

![示例图片](https://gitee.com/xgpxg/sim-framework/raw/sim-framework-1.0.2/images/4.png)

### 1. 安装

    npm install sim-el-table-plus

### 2. 使用示例

    <template>
      <el-table-plus
        ref="permission-table"
        :option-column="optionColumn"
        :load-data="loadData"
        :columns="columns"
        is-compact
        :search-data="searchParam"
        :auto-page-size="true"
      >
      </el-table-plus>
    </template>
    
    <script>
      import ElTablePlus from 'sim-el-table-plus'
    
      export default {
        name: "index",
        components: {
          ElTablePlus,
        },
        data() {
          return {
            //这里是搜索参数的配置,该参数变动后,后自动触发表格的load-data方法重新渲染数据
            searchParam:{
    
            },
            //这里是表格列的配置
            columns: [
              {prop: 'userId', label: 'ID', width: '70', align: 'center'},
              {prop: 'userName', label: '名称', align: 'center'},
              {prop: 'userTypeName', label: '类型', width: '100', align: 'center'},
              {prop: 'roles', label: '归属角色', width: '100', align: 'center'},
              {prop: 'viewPermission',label: '查看权限',width: '100',align: 'center'},
              {prop: 'createDate', label: '创建时间', width: '180', align: 'center'},
              {prop: 'expDate',label: '过期时间',width: '100',align: 'center',isSlot: true},
              {prop: 'statusName', label: '状态', width: '80', align: 'center'}
            ],
            //这里是操作栏列的配置
            optionColumn: {
              options: [
                {
                  type: 'text',
                  text: '编辑',
                  handler: (row) => {
                    //点击改按钮时，你的处理逻辑
                  },
                  disabled: (row) => {
                    //true:禁用,false:不禁用, 默认不禁用
                    return false;
                  }
                }, {
                  type: 'text',
                  text: '解冻',
                  handler: (row) => {
                    //your handler
                  },
                  disabled: (row) => {
                    //true:disabled,false:not disabled
                    return false;
                  }
                }, {
                  type: 'text',
                  text: '冻结',
                  handler: (row) => {
                    //your handler
                  },
                  disabled: (row) => {
                    //true:disabled,false:not disabled
                    return false;
                  }
                }, {
                  type: 'text',
                  text: '停用',
                  handler: (row) => {
                    //your handler
                  },
                  disabled: (row) => {
                    //true:disabled,false:not disabled
                    return false;
                  }
                }, {
                  type: 'text',
                  text: '删除',
                  handler: (row) => {
                    //your handler
                  },
                  disabled: (row) => {
                    //true:disabled,false:not disabled
                    return false;
                  }
                }
              ],
              fixed: 'right',
              width: '230'
            }
          }
        },
        methods:{
          loadData(resolve, pageInfo, searchData) {
            //这里是你请求后台的代码
            //注意：如果需要分页，则resolve参数为一个对象，格式如下
            let data = {
              //总页数
              total: 55,
              //数据列表
              list: []
            };
            //如果不需要分页 则需设置pagination=false 然后直接resolve一个列表即可
            resolve(data)
          },
          /**
           * 搜索
           */
          search(){
            this.$refs['permission-table'].refresh();
          },
        }
      }
    </script>


### 2. el-table-plus扩展属性

| 属性 | 说明 | 类型 | 可选值 | 默认值
|--|--|--|--|--|
| data | 原生属性，表格数据，一个json对象，含分页数据(后端PageHelper分页) | Object | -| []|
| columns| 表头配置，详情见下方columns配置 | Array | - | [] |
| pagination| 是否分页 | Boolean | true/false | true |
| page-size| 分页大小 |Number | - | 10|
|layout|分页布局，与原生属性一致|String|-|'total, prev, pager, next'|
|page-sizes|下拉可选的每页数据条数，与原生属性一致|Array|-|[5, 10, 20, 30, 40, 50, 100]|
| data-url| 加载数据的url，如果只是简单的加载数据，直接传入后台接口即可，默认GET请求 |String | - | - |
| load-data| 加载数据的方法(优先级高于dataUrl) |Function | -| -|
| selection| 是否开启多选 | Boolean | true/false |true |
| row-key|表格行唯一标识key,多选表格必传 | String  | - | -|
| current-page|当前页  | Number | - | 1 |
| option-column| 操作栏列，详情见下方optionColumn配置 | Object| - | - |
| show-loading|是否展示加载遮罩层  |Boolean | true/false | true |
| enable-auto-rowspan|是否允许自动跨行，默认false  |Boolean | true/false | false |
| auto-row-span-key|自动跨行的key（对应表格列的prop属性） | Array  | - | -|
| pagination-style|分页组件的样式 | Object  | - | -|
| size|表格尺寸(原生) | String  | medium / small / mini | -|
| is-compact | 是否是紧凑型表格  | true/false | false| 
| row-style|行样式 | Object  | -| -|
| cell-style|列样式 | Object  | - | -|
| search-data|搜索条件,绑定到一个对象，内容修改时会自动重新加载数据 | Object  | - | -|
| show-summary|超出列宽后自动隐藏并显示提示 | String  | /true/false | false |
| auto-page-size|表格尺寸(原生) | Boolean  | /true/false | -|



###  3. columns配置
columns为一个数组，数组对象内容均为原生element ui的el-table-column属性（部分属性，有用到其他的再添加），已支持的属性如下：

| 属性 | 说明 | 类型 | 可选值 | 默认值
|--|--|--|--|--|
| label| 表头名称（原生） | String| -| -|
| prop| 对应列内容的字段名（原生） | String| -| -|
| align| 列内容对齐方式（原生） | String| -| -|
| showOverflowTooltip| 列内容超出隐藏(原生) | Boolean | -| false |
| width| 列宽。当值为auto时，自动计算表头宽度(基于表头内容宽度);当值为auto-content时，自动计算列宽(取每行最多字符数) | String| auto | -|
| minWidth| 列最小宽度（原生） |String| -| -|
| columns| 嵌套列，用于复杂表头  | Array| - | -|
| autoRowspan| 当前列是否自动跨行  | Boolean| - | -|

#### 3.1 简单表头示例：



    columns: [
        {
            label: '菜单ID', prop: 'menuId', align: 'center', width: '100px'
        },
        {
            label: '菜单名称', prop: 'menuName'
        }
    ]


#### 3.2 复杂表头示例：


	columns: [
          {
            label: '权限ID', prop: 'purviewId', align: 'center', columns: [
              {label: '权限ID', prop: 'purviewId'},
              {label: '权限名称', prop: 'purviewName'},
            ]
          },
          {
            label: '权限名称', prop: 'purviewName'
          },
          {
            label: '权限编码', prop: 'purviewCode'
          }
        ]





###  4. optionColumn配置



optionColumn为一个对象，对象内容均为原生element ui的el-table-column属性（部分属性，有用到其他的再添加），已支持的属性如下：

| 属性 | 说明 | 类型 | 可选值 | 默认值
|--|--|--|--|--|
| label| 表头名称（原生） | String| -| -|
| prop| 对应列内容的字段名（原生） | String| -| -|
| align| 对	齐方式（原生） | String| -| -|
| width| 列宽 | String| -| -|
| options| 操作按钮配置，详情见下方optionColumn.options配置 |Array| -| -|

####  4.1 optionColumn.options配置

| 属性 | 说明 | 类型 | 可选值 | 默认值
|--|--|--|--|--|
| type| 按钮类型（原生） | String| -| -|
| size| 按钮大小（原生） | String| -| -|
| text| 按钮文本 | String| -| -|
| handler| 点击按钮的回调，参数：当前行数据 |Array| -| -|

示例：



	optionColumn: {
	          options: [
	            {
	              type: 'text',
	              size: 'mini',
	              text: '新增',
	              handler: (row) => {
	                //这里进行逻辑处理
	              }
	            }, {
	              type: 'text',
	              size: 'mini',
	              text: '删除',
	              handler: (row) => {
	                //这里进行逻辑处理
	              }
	            }
	          ],
	          width: '100px',
	        }


## 5. el-table-plus事件

| 事件 | 说明 | 类型 | 可选值 | 默认值
|--|--|--|--|--|
| select| 开启多选时，点击checkbox时触发，参数：当前行数据 | Function | -| -|
