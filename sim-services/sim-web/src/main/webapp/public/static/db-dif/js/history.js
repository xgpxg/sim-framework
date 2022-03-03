let app = new Vue({
    el: "#app",
    data: {
        menuDatas: common_util.menuDatas,
        initData: {
            historyList: []
        },
        dfDetailList:[],
        show: false,
        dict: {
            type: ['mysql']
        },
        formData: {
            connectInfoLeft: {
                connectString: null,
                ip: '192.168.10.159',
                type: 'mysql',
                port: 8066,
                database: 'hsi_cpc_center',
                param: null,
                username: 'hsi_cpc_center_user',
                password: '1qaz2wsx'
            },
            connectInfoRight: {
                connectString: null,
                ip: '192.168.10.159',
                type: 'mysql',
                port: 8066,
                database: 'hsi_cop_center',
                param: null,
                username: 'hsi_cop_center_user',
                password: '1qaz2wsx'
            },
        },
        selectedTablesLeft: [],
        selectedTablesRight: [],
        tablesInfoLeft: [],
        tablesInfoRight: [],
        opened: false,
        tableDetailInfo: {},
        fieldsList: [],
        result: [],
        rules: {
            number: ['number'],
            mobile: ['mobile'],
            required: ['ip', 'type', 'port', 'database', 'username', 'password']
        }
    },
    mounted: function () {
        common_util.selectMenu();
        this.init();
    },
    watch: {},
    methods: {
        leftConnectStringChange: function () {
            let l = this.resolveConnectionUrl(this.formData.connectInfoLeft.connectString);
            this.formData.connectInfoLeft.ip = l.ip;
            this.formData.connectInfoLeft.port = l.port;
            this.formData.connectInfoLeft.database = l.db;
            this.formData.connectInfoLeft.param = l.param;
        },
        rightConnectStringChange: function () {
            let r = this.resolveConnectionUrl(this.formData.connectInfoRight.connectString);
            this.formData.connectInfoRight.ip = r.ip;
            this.formData.connectInfoRight.port = r.port;
            this.formData.connectInfoRight.database = r.db;
            this.formData.connectInfoRight.param = r.param;
        },
        openModal: function () {
            this.$Modal({
                title: "title",
                content: "This is a js call box"
            });
        },
        test: function (direction, leftDat, rightData) {

        },
        trSelectLeft: function (data) {
            this.selectedTablesLeft = data;
        },
        trSelectRight: function (data) {
            this.selectedTablesRight = data;
        },
        connect: function (flag) {
            if (flag === 'left') {
                let validResult = this.$refs.formLeft.valid();
                if (validResult.result) {
                    this.connectLeft();
                }
            }
            if (flag === 'right') {
                let validResult = this.$refs.formRight.valid();
                if (validResult.result) {
                    this.connectRight();
                }
            }

        },
        connectLeft: function () {
            let that = this;
            axios.post('/api/db-dif/qryTables', that.formData.connectInfoLeft).then(function (res) {
                that.tablesInfoLeft = res.data;
                console.log(res.data)
            }).catch(function (error) {
                that.$Message({
                    type: 'error',
                    text: "获取表信息异常:" + error
                });
            })
        },
        connectRight: function () {
            let that = this;
            axios.post('/api/db-dif/qryTables', that.formData.connectInfoRight).then(function (res) {
                that.tablesInfoRight = res.data;
            }).catch(function (error) {
                that.$Message({
                    type: 'error',
                    text: "获取表信息异常:" + error
                });
            })
        },
        tableInfoDetailLeft: function (datas, data) {
            this.opened = true;
            this.tableDetailInfo = data;
            this.qryFieldsLeft(data.TABLE_NAME);
        },
        tableInfoDetailRight: function (datas, data) {
            this.opened = true;
            this.tableDetailInfo = data;
            this.qryFieldsRight(data.TABLE_NAME);
        },

        qryFieldsLeft: function (tableName) {
            let that = this;
            let params = {
                dbInfo: this.formData.connectInfoLeft,
                tableName: tableName
            }
            axios.post('/api/db-dif/qryFields', params).then(function (res) {
                that.fieldsList = res.data;
                console.log(res.data)
            }).catch(function (error) {
                that.$Message({
                    type: 'error',
                    text: "获取字段息异常:" + error
                });
            })
        },
        qryFieldsRight: function (tableName) {
            let that = this;
            let params = {
                dbInfo: this.formData.connectInfoRight,
                tableName: tableName
            }
            axios.post('/api/db-dif/qryFields', params).then(function (res) {
                that.fieldsList = res.data;
            }).catch(function (error) {
                that.$Message({
                    type: 'error',
                    text: "获取字段息异常:" + error
                });
            })
        },

        parseDes: function (data) {
            let res = [];
            if (data.des !== undefined) {
                data.des.forEach(function (ele) {
                    res.push(ele);
                })
                return '(' + res.join(',') + ')';
            }
            return '';
        },
        parseFieldDes: function (key, data) {
            let uuid = data['_heyui_uuid'];
            let isDif = false;
            if (data.difType !== undefined && data.difType!==null && data.difType.length>0) {
                data.difType.forEach(function (ele) {
                    if (key === ele) {
                        isDif = true;

                    }
                })
            }
            if (data.missOrAdd === 'true') {
                $('tr[trindex="' + uuid + '"]').addClass('bg-blue-color white-color');
            } else if (data.dif === 'true') {
                $('tr[trindex="' + uuid + '"]').addClass('bg-yellow-color white-color');
            }

            if (isDif) {

                return '<span class="red-color text-ellipsis">' + data[key] + '</span>';
            } else {
                return '<span >' + data[key] + '</span>';
            }
        },
        resultDetail: function (data) {
            this.$set(data, '_expand', !data._expand);
        },
        formatTime: function (date) {
            let fmt = 'yyyy-MM-dd hh:mm:ss';
            let o = {
                "M+": date.getMonth() + 1,
                "d+": date.getDate(),
                "h+": date.getHours(),
                "m+": date.getMinutes(),
                "s+": date.getSeconds(),
                "q+": Math.floor((date.getMonth() + 3) / 3),
                "S": date.getMilliseconds()
            };
            if (/(y+)/.test(fmt))
                fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
            for (let k in o)
                if (new RegExp("(" + k + ")").test(fmt))
                    fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
            return fmt;
        },
        saveHistory: function (data) {
            let that = this;
            $.ajax({
                url: '/api/db-dif/saveResult',
                data: JSON.stringify(data),
                dataType: 'json',
                type: 'post',
                contentType: 'application/json',
                success: function (res) {
                    if (res.code === '0') {

                    } else {
                        that.$Message({
                            type: 'warn',
                            text: "history record save fail:" + error
                        });
                    }
                }
            })
        },
        resolveConnectionUrl: function (url) {
            let ip_port_db = url.match(/\:\/\/.*\?/)[0].replace('://', '').replace('?', '');
            let ip = ip_port_db.substr(0, ip_port_db.indexOf(':'));
            let port = ip_port_db.substr(ip_port_db.indexOf(':') + 1, ip_port_db.indexOf('/') - ip_port_db.indexOf(':') - 1);
            let db = ip_port_db.substr(ip_port_db.indexOf('/') + 1, ip_port_db.length - ip_port_db.indexOf('/'));
            let param = url.substr(url.indexOf('?') + 1, url.length - ip_port_db.indexOf('?'));
            console.log(param);
            return {ip: ip, port: port, db: db, param: param};
        },
        init: function () {
            let that = this;
            $.ajax({
                url: '/api/db-dif/qryHistory',
                //data: JSON.stringify(data),
                dataType: 'json',
                type: 'get',
                contentType: 'application/json',
                success: function (res) {
                    if (res.code === 0) {
                        console.log(res.data);
                        that.initData.historyList = res.data;
                    } else {
                        that.$Message({
                            type: 'warn',
                            text: "history record save fail:" + error
                        });
                    }
                }
            })
        },
        openHistoryDetailModal:function (data) {
            this.dfDetailList = data;
            this.opened = true;
        }
    }
});
