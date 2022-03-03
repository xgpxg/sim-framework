let app = new Vue({
    el: "#app",
    data: {
        menuDatas: common_util.menuDatas,
        show: false,
        dict: {
            type: ['mysql', 'oracle']
        },
        formData: {
            connectInfoLeft: {
                connectString: null,
                ip: null,
                type: null,
                port: null,
                database: null,
                param: null,
                username: null,
                password: null
            },
            connectInfoRight: {
                connectString: null,
                ip: null,
                type: null,
                port: null,
                database: null,
                param: null,
                username: null,
                password: null
            },
        },
        connectSuccessLeft: false,
        connectSuccessRight: false,
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
        },
        compareType: ['Filed', 'Type', 'Null', 'Default'],
        showNoDifTable: false,
    },
    mounted: function () {
        common_util.selectMenu();
        if (localStorage.getItem('selectedCompareType')) {
            this.compareType = localStorage.getItem('selectedCompareType').split(',');
        }
        if (localStorage.getItem('showNoDifTable')) {
            this.showNoDifTable = localStorage.getItem('showNoDifTable') === 'true';
        }
    },
    watch: {
        connectSuccessLeft: function (val) {
            if (val && this.connectSuccessRight) {

            }
        },
        connectSuccessRight: function (val) {
            if (val && this.connectSuccessLeft) {

            }
        }
    },
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
            that.connectSuccessLeft = false;
            axios.post('/api/db-dif/qryTables', that.formData.connectInfoLeft).then(function (res) {
                that.tablesInfoLeft = res.data;
                that.connectSuccessLeft = true;
            }).catch(function (error) {
                that.$Message({
                    type: 'error',
                    text: "获取表信息异常:" + error
                });
            })
        },
        connectRight: function () {
            let that = this;
            that.connectSuccessRight = false;
            axios.post('/api/db-dif/qryTables', that.formData.connectInfoRight).then(function (res) {
                that.tablesInfoRight = res.data;
                that.connectSuccessRight = true;
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
            console.log(data);

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
                tableName: tableName,
                type:this.formData.connectInfoLeft.type
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
                tableName: tableName,
                type:this.formData.connectInfoRight.type
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

        compare: function () {
            let that = this;

            if (this.selectedTablesLeft.length === 0 && this.selectedTablesRight.length === 0) {
                return;
            }
            that.result = [];
            let leftTableInfo = this.selectedTablesLeft;
            let rightTableInfo = this.selectedTablesRight;
            //左对比右

            // if (leftTableInfo.length >= rightTableInfo.length)
            for (let i in leftTableInfo) {
                let hasTable = false;
                for (let j in rightTableInfo) {
                    let leftTable = leftTableInfo[i];
                    let rightTable = rightTableInfo[j];
                    let leftTableName = leftTable['TABLE_NAME'];
                    let rightTableName = rightTable['TABLE_NAME']
                    if (leftTableName.toLocaleUpperCase() === rightTableName.toLocaleUpperCase()) {
                        hasTable = true;
                        //查询字段(左)
                        let paramsLeft = {
                            dbInfo: that.formData.connectInfoLeft,
                            tableName: leftTable['TABLE_NAME']
                        };
                        $.ajax({
                            url: '/api/db-dif/qryFields',
                            data: JSON.stringify(paramsLeft),
                            dataType: 'json',
                            type: 'post',
                            contentType: 'application/json',
                            async: false,
                            success: function (res) {
                                let leftFields = res;
                                if (!that.selectedTablesLeft[i]) {
                                    that.selectedTablesLeft[i] = {Field: ''};
                                }
                                that.selectedTablesLeft[i].fields = leftFields;
                                //查询字段(右)
                                let paramsRight = {
                                    dbInfo: that.formData.connectInfoRight,
                                    tableName: rightTable['TABLE_NAME']
                                };
                                $.ajax({
                                    url: '/api/db-dif/qryFields',
                                    data: JSON.stringify(paramsRight),
                                    dataType: 'json',
                                    type: 'post',
                                    contentType: 'application/json',
                                    async: false,
                                    success: function (res) {
                                        let rightFields = res;
                                        if (!that.selectedTablesRight[i]) {
                                            that.selectedTablesRight[i] = {Field: ''};
                                        }
                                        that.selectedTablesRight[i].fields = rightFields;
                                        for (let idxL in leftFields) {
                                            let exits = false;
                                            let nL = leftFields[idxL]['Field'];
                                            for (let idxR in rightFields) {
                                                let nR = rightFields[idxR]['Field'];
                                                if (nL.toLocaleLowerCase() === nR.toLocaleLowerCase()) {
                                                    exits = true;
                                                    let idx = idxL;
                                                    let lf = leftFields[idx];
                                                    let rf = rightFields[idx];
                                                    if (!rf) {
                                                        rf = {};
                                                    }
                                                    if (lf['Field'] === rf['Field']) {
                                                        for (let key in lf) {
                                                            if (lf[key] !== rf[key] && that.compareType.indexOf(key) !== -1) {
                                                                tableDifFlag = true;
                                                                that.selectedTablesLeft[i].dif = 'true';
                                                                that.selectedTablesRight[i].dif = 'true';
                                                                that.selectedTablesLeft[i].des = that.selectedTablesLeft[i].des === undefined ? new Set(['差异字段']) : that.selectedTablesLeft[i].des.add('差异字段');
                                                                that.selectedTablesRight[i].des = that.selectedTablesRight[i].des === undefined ? new Set(['差异字段']) : that.selectedTablesRight[i].des.add('差异字段');
                                                                that.selectedTablesLeft[i].fields[idx].dif = 'true';
                                                                that.selectedTablesRight[i].fields[idx].dif = 'true';
                                                                that.selectedTablesLeft[i].fields[idx].difType = that.selectedTablesLeft[i].fields[idx].difType === undefined ? new Set([key]) : that.selectedTablesLeft[i].fields[idx].difType.add(key);
                                                                that.selectedTablesRight[i].fields[idx].difType = that.selectedTablesRight[i].fields[idx].difType === undefined ? new Set([key]) : that.selectedTablesRight[i].fields[idx].difType.add(key);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (!exits) {
                                                that.selectedTablesLeft[i].dif = 'true';
                                                that.selectedTablesRight[i].dif = 'true';
                                                that.selectedTablesLeft[i].des = that.selectedTablesLeft[i].des === undefined ? new Set(['缺失或增加字段']) : that.selectedTablesLeft[i].des.add('缺失或增加字段');
                                                that.selectedTablesRight[i].des = that.selectedTablesRight[i].des === undefined ? new Set(['缺失或增加字段']) : that.selectedTablesRight[i].des.add('缺失或增加字段');
                                                that.selectedTablesLeft[i].fields[idxL].dif = 'true';
                                                that.selectedTablesLeft[i].fields[idxL].missOrAdd = 'true';
                                            }
                                        }
                                        for (let idxR in rightFields) {
                                            let exits = false;
                                            let nR = rightFields[idxR]['Field'];
                                            for (let idxL in leftFields) {
                                                let nL = leftFields[idxL]['Field'];
                                                if (nL.toLocaleLowerCase() === nR.toLocaleLowerCase()) {
                                                    exits = true;
                                                    let idx = idxR;
                                                    let lf = leftFields[idx];
                                                    let rf = rightFields[idx];
                                                    if (!lf) {
                                                        lf = {};
                                                    }
                                                    if (lf['Field'] === rf['Field']) {
                                                        for (let key in lf) {
                                                            if (lf[key] !== rf[key] && that.compareType.indexOf(key) !== -1) {
                                                                that.selectedTablesLeft[i].dif = 'true';
                                                                that.selectedTablesRight[i].dif = 'true';
                                                                that.selectedTablesLeft[i].des = that.selectedTablesLeft[i].des === undefined ? new Set(['差异字段']) : that.selectedTablesLeft[i].des.add('差异字段');
                                                                that.selectedTablesRight[i].des = that.selectedTablesRight[i].des === undefined ? new Set(['差异字段']) : that.selectedTablesRight[i].des.add('差异字段');
                                                                that.selectedTablesLeft[i].fields[idx].dif = 'true';
                                                                that.selectedTablesRight[i].fields[idx].dif = 'true';
                                                                that.selectedTablesLeft[i].fields[idx].difType = that.selectedTablesLeft[i].fields[idx].difType === undefined ? new Set([key]) : that.selectedTablesLeft[i].fields[idx].difType.add(key);
                                                                that.selectedTablesRight[i].fields[idx].difType = that.selectedTablesRight[i].fields[idx].difType === undefined ? new Set([key]) : that.selectedTablesRight[i].fields[idx].difType.add(key);
                                                            }
                                                        }
                                                    }
                                                }

                                            }
                                            if (!exits) {
                                                that.tablesInfoLeft[i].dif = 'true';
                                                that.selectedTablesRight[i].dif = 'true';
                                                that.tablesInfoLeft[i].des = that.tablesInfoLeft[i].des === undefined ? new Set(['缺失或增加字段']) : that.tablesInfoLeft[i].des.add('缺失或增加字段');
                                                that.selectedTablesRight[i].des = that.selectedTablesRight[i].des === undefined ? new Set(['缺失或增加字段']) : that.selectedTablesRight[i].des.add('缺失或增加字段');
                                                that.selectedTablesRight[i].fields[idxR].dif = 'true';
                                                that.selectedTablesRight[i].fields[idxR].missOrAdd = 'true';
                                            }
                                        }
                                    }
                                });
                            }
                        })
                    }
                }
                if (!hasTable) {
                    /*that.result.push({
                        left: {fields: []},//that.selectedTablesLeft[i],
                        right: {fields: []}
                    });*/
                } else
                    that.result.push({
                        left: that.selectedTablesLeft[i],
                        right: that.selectedTablesRight[i]
                    });

            }
            //反之
            /*   if (leftTableInfo.length < rightTableInfo.length)
                   for (let i in rightTableInfo) {
                       let hasTable = false;
                       for (let j in leftTableInfo) {
                           let leftTable = leftTableInfo[j];
                           let rightTable = rightTableInfo[i];
                           let leftTableName = leftTable['TABLE_NAME'];
                           let rightTableName = rightTable['TABLE_NAME']
                           if (leftTableName === rightTableName) {
                               hasTable = true;
                               //查询字段(左)
                               let paramsLeft = {
                                   dbInfo: that.formData.connectInfoLeft,
                                   tableName: leftTable['TABLE_NAME']
                               };
                               $.ajax({
                                   url: '/qryFields',
                                   data: JSON.stringify(paramsLeft),
                                   dataType: 'json',
                                   type: 'post',
                                   contentType: 'application/json',
                                   async: false,
                                   success: function (res) {
                                       let leftFields = res;
                                       that.selectedTablesLeft[i].fields = leftFields;
                                       //查询字段(右)
                                       let paramsRight = {
                                           dbInfo: that.formData.connectInfoRight,
                                           tableName: rightTable['TABLE_NAME']
                                       };
                                       $.ajax({
                                           url: '/qryFields',
                                           data: JSON.stringify(paramsRight),
                                           dataType: 'json',
                                           type: 'post',
                                           contentType: 'application/json',
                                           async: false,
                                           success: function (res) {
                                               let rightFields = res;
                                               that.selectedTablesRight[i].fields = rightFields;

                                               for (let idxL in leftFields) {
                                                   let exits = false;
                                                   let nL = leftFields[idxL]['Field'];
                                                   for (let idxR in rightFields) {
                                                       let nR = rightFields[idxR]['Field'];
                                                       if (nL === nR) {
                                                           exits = true;
                                                           let idx = idxL;
                                                           let lf = leftFields[idx];
                                                           let rf = rightFields[idx];
                                                           if (lf['Field'] === rf['Field']) {
                                                               for (let key in lf) {
                                                                   if (lf[key] !== rf[key]) {
                                                                       that.selectedTablesLeft[i].dif = 'true';
                                                                       that.selectedTablesRight[i].dif = 'true';
                                                                       that.selectedTablesLeft[i].des = that.selectedTablesLeft[i].des === undefined ? new Set(['差异字段']) : that.selectedTablesLeft[i].des.add('差异字段');
                                                                       that.selectedTablesRight[i].des = that.selectedTablesRight[i].des === undefined ? new Set(['差异字段']) : that.selectedTablesRight[i].des.add('差异字段');
                                                                       that.selectedTablesLeft[i].fields[idx].dif = 'true';
                                                                       that.selectedTablesRight[i].fields[idx].dif = 'true';
                                                                       that.selectedTablesLeft[i].fields[idx].difType = that.selectedTablesLeft[i].fields[idx].difType === undefined ? new Set([key]) : that.selectedTablesLeft[i].fields[idx].difType.add(key);
                                                                       that.selectedTablesRight[i].fields[idx].difType = that.selectedTablesRight[i].fields[idx].difType === undefined ? new Set([key]) : that.selectedTablesRight[i].fields[idx].difType.add(key);

                                                                   }
                                                               }
                                                           }
                                                       }
                                                   }
                                                   if (!exits) {
                                                       that.selectedTablesLeft[i].dif = 'true';
                                                       that.selectedTablesRight[i].dif = 'true';
                                                       that.selectedTablesLeft[i].des = that.selectedTablesLeft[i].des === undefined ? new Set(['缺失或增加字段']) : that.selectedTablesLeft[i].des.add('缺失或增加字段');
                                                       that.selectedTablesRight[i].des = that.selectedTablesRight[i].des === undefined ? new Set(['缺失或增加字段']) : that.selectedTablesRight[i].des.add('缺失或增加字段');
                                                       that.selectedTablesLeft[i].fields[idxL].dif = 'true';
                                                       that.selectedTablesLeft[i].fields[idxL].missOrAdd = 'true';
                                                   }
                                               }

                                               for (let idxR in rightFields) {
                                                   let exits = false;
                                                   let nR = rightFields[idxR]['Field'];
                                                   for (let idxL in leftFields) {
                                                       let nL = leftFields[idxL]['Field'];
                                                       if (nL === nR) {
                                                           exits = true;
                                                           let idx = idxR;
                                                           let lf = leftFields[idx];
                                                           let rf = rightFields[idx];
                                                           if (lf['Field'] === rf['Field']) {
                                                               for (let key in lf) {
                                                                   if (lf[key] !== rf[key]) {
                                                                       that.selectedTablesLeft[i].dif = 'true';
                                                                       that.selectedTablesRight[i].dif = 'true';
                                                                       that.selectedTablesLeft[i].des = that.selectedTablesLeft[i].des === undefined ? new Set(['差异字段']) : that.selectedTablesLeft[i].des.add('差异字段');
                                                                       that.selectedTablesRight[i].des = that.selectedTablesRight[i].des === undefined ? new Set(['差异字段']) : that.selectedTablesRight[i].des.add('差异字段');
                                                                       that.selectedTablesLeft[i].fields[idx].dif = 'true';
                                                                       that.selectedTablesRight[i].fields[idx].dif = 'true';
                                                                       that.selectedTablesLeft[i].fields[idx].difType = that.selectedTablesLeft[i].fields[idx].difType === undefined ? new Set([key]) : that.selectedTablesLeft[i].fields[idx].difType.add(key);
                                                                       that.selectedTablesRight[i].fields[idx].difType = that.selectedTablesRight[i].fields[idx].difType === undefined ? new Set([key]) : that.selectedTablesRight[i].fields[idx].difType.add(key);
                                                                   }
                                                               }
                                                           }
                                                       }
                                                   }
                                                   if (!exits) {
                                                       that.tablesInfoLeft[i].dif = 'true';
                                                       that.selectedTablesRight[i].dif = 'true';
                                                       that.tablesInfoLeft[i].des = that.tablesInfoLeft[i].des === undefined ? new Set(['缺失或增加字段']) : that.tablesInfoLeft[i].des.add('缺失或增加字段');
                                                       that.selectedTablesRight[i].des = that.selectedTablesRight[i].des === undefined ? new Set(['缺失或增加字段']) : that.selectedTablesRight[i].des.add('缺失或增加字段');
                                                       that.selectedTablesRight[i].fields[idxR].dif = 'true';
                                                       that.selectedTablesRight[i].fields[idxR].missOrAdd = 'true';
                                                   }
                                               }
                                           }
                                       });
                                   }
                               })
                           }

                       }
                       if (!hasTable) {
                           //that.selectedTablesRight[i]
                       } else
                           that.result.push({right: that.selectedTablesLeft[i], left: that.selectedTablesRight[i]});
                   }*/
            /* for (let j in  rightTableInfo) {
                 for (let i in leftTableInfo) {
                     let leftTable = leftTableInfo[i];
                     let rightTable = rightTableInfo[j];
                     let leftTableName = leftTable['TABLE_NAME'];
                     let rightTableName = rightTable['TABLE_NAME']
                     if (leftTableName === rightTableName) {
                         //查询字段(左)
                         let paramsLeft = {
                             dbInfo: that.formData.connectInfoLeft,
                             tableName: leftTable['TABLE_NAME']
                         };
                         $.ajax({
                             url: '/qryFields',
                             data: JSON.stringify(paramsLeft),
                             dataType: 'json',
                             type: 'post',
                             contentType: 'application/json',
                             async: false,
                             success: function (res) {
                                 let leftFields = res;
                                 that.selectedTablesLeft[j].fields = leftFields;
                                 //查询字段(右)
                                 let paramsRight = {
                                     dbInfo: that.formData.connectInfoRight,
                                     tableName: rightTable['TABLE_NAME']
                                 };
                                 $.ajax({
                                     url: '/qryFields',
                                     data: JSON.stringify(paramsRight),
                                     dataType: 'json',
                                     type: 'post',
                                     contentType: 'application/json',
                                     async: false,
                                     success: function (res) {
                                         let rightFields = res;
                                         that.selectedTablesRight[j].fields = rightFields;

                                         for (let idxL in leftFields) {
                                             let exits = false;
                                             let nL = leftFields[idxL]['Field'];
                                             for (let idxR in rightFields) {
                                                 let nR = rightFields[idxR]['Field'];
                                                 if (nL === nR) {
                                                     exits = true;
                                                     let idx = idxL;
                                                     let lf = leftFields[idx];
                                                     let rf = rightFields[idx];
                                                     if (lf['Field'] === rf['Field']) {
                                                         for (let key in lf) {
                                                             if (lf[key] !== rf[key]) {
                                                                 that.selectedTablesLeft[j].dif = 'true';
                                                                 that.selectedTablesRight[j].dif = 'true';
                                                                 that.selectedTablesLeft[j].des = that.selectedTablesLeft[j].des === undefined ? new Set(['差异字段']) : that.selectedTablesLeft[j].des.add('差异字段');
                                                                 that.selectedTablesRight[j].des = that.selectedTablesRight[j].des === undefined ? new Set(['差异字段']) : that.selectedTablesRight[j].des.add('差异字段');
                                                                 that.selectedTablesLeft[j].fields[idx].dif = 'true';
                                                                 that.selectedTablesRight[j].fields[idx].dif = 'true';
                                                                 that.selectedTablesLeft[j].fields[idx].difType= that.selectedTablesLeft[j].fields[idx].difType=== undefined ? new Set([key]):that.selectedTablesLeft[j].fields[idx].difType.add(key) ;
                                                                 that.selectedTablesRight[j].fields[idx].difType= that.selectedTablesRight[j].fields[idx].difType=== undefined ? new Set([key]):that.selectedTablesRight[j].fields[idx].difType.add(key) ;

                                                             }
                                                         }
                                                     }
                                                 }
                                             }
                                             if (!exits) {
                                                 that.selectedTablesLeft[j].dif = 'true';
                                                 that.selectedTablesRight[j].dif = 'true';
                                                 that.selectedTablesLeft[j].des = that.selectedTablesLeft[j].des === undefined ? new Set(['缺失或增加字段']) : that.selectedTablesLeft[j].des.add('缺失或增加字段');
                                                 that.selectedTablesRight[j].des = that.selectedTablesRight[j].des === undefined ? new Set(['缺失或增加字段']) : that.selectedTablesRight[j].des.add('缺失或增加字段');
                                                 that.selectedTablesLeft[j].fields[idxL].dif = 'true';
                                                 that.selectedTablesLeft[j].fields[idxL].missOrAdd = 'true';
                                             }
                                         }
                                         for (let idxR in rightFields) {
                                             let exits = false;
                                             let nR = rightFields[idxR]['Field'];
                                             for (let idxL in leftFields) {
                                                 let nL = leftFields[idxL]['Field'];
                                                 if (nL === nR) {
                                                     exits = true;
                                                     let idx = idxR;
                                                     let lf = leftFields[idx];
                                                     let rf = rightFields[idx];
                                                     if (lf['Field'] === rf['Field']) {
                                                         for (let key in lf) {
                                                             if (lf[key] !== rf[key]) {
                                                                 that.selectedTablesLeft[j].dif = 'true';
                                                                 that.selectedTablesRight[j].dif = 'true';
                                                                 that.selectedTablesLeft[j].des = that.selectedTablesLeft[j].des === undefined ? new Set(['差异字段']) : that.selectedTablesLeft[j].des.add('差异字段');
                                                                 that.selectedTablesRight[j].des = that.selectedTablesRight[j].des === undefined ? new Set(['差异字段']) : that.selectedTablesRight[j].des.add('差异字段');
                                                                 that.selectedTablesLeft[j].fields[idx].dif = 'true';
                                                                 that.selectedTablesRight[j].fields[idx].dif = 'true';
                                                                 that.selectedTablesLeft[j].fields[idx].difType= that.selectedTablesLeft[j].fields[idx].difType=== undefined ? new Set([key]):that.selectedTablesLeft[j].fields[idx].difType.add(key) ;
                                                                 that.selectedTablesRight[j].fields[idx].difType= that.selectedTablesRight[j].fields[idx].difType=== undefined ? new Set([key]):that.selectedTablesRight[j].fields[idx].difType.add(key) ;
                                                             }
                                                         }
                                                     }
                                                 }

                                             }
                                             if (!exits) {
                                                 that.tablesInfoLeft[j].dif = 'true';
                                                 that.selectedTablesRight[j].dif = 'true';
                                                 that.tablesInfoLeft[j].des = that.tablesInfoLeft[j].des === undefined ? new Set(['缺失或增加字段']) : that.tablesInfoLeft[j].des.add('缺失或增加字段');
                                                 that.selectedTablesRight[j].des = that.selectedTablesRight[j].des === undefined ? new Set(['缺失或增加字段']) : that.selectedTablesRight[j].des.add('缺失或增加字段');
                                                 that.selectedTablesRight[j].fields[idxR].dif = 'true';
                                                 that.selectedTablesRight[j].fields[idxR].missOrAdd = 'true';
                                             }
                                         }
                                     }
                                 });
                             }
                         })
                     }
                 }

                 that.result.push({left: that.selectedTablesLeft[j], right: that.selectedTablesRight[j]});

             }*/
            console.log(that.result);

            //save compare result
            let data = {};
            let list = [];
            //problem for Set can't to be convert json
            that.result.forEach(item => {
                if (item.left.dif === 'true') {
                    let fieldsLeft = item.left.fields;
                    let fieldsRight = item.right.fields;
                    let f = {};
                    f.tableName = item.left['TABLE_NAME'];
                    f.difFields = [];
                    f.difDes = item.left.des;
                    fieldsLeft.forEach(filedL => {
                        let df = {};
                        if (filedL.dif === 'true') {
                            df.field1 = filedL;
                            fieldsRight.forEach(filedR => {
                                if (filedR.dif === 'true' && filedL['Field'] === filedR['Field']) {
                                    df.field2 = filedR;
                                }
                            });
                            if (!df.field2) {
                                df.field2 = {};
                            }
                            f.difFields.push(df);
                        }
                    });

                    list.push(f);
                }

                if (item.right.dif === 'true') {
                    let fieldsLeft = item.left.fields;
                    let fieldsRight = item.right.fields;
                    let f = {};
                    f.tableName = item.left['TABLE_NAME'];
                    f.difFields = [];
                    f.difDes = item.right.des;
                    fieldsRight.forEach(filedR => {
                        let df = {};
                        if (filedR.dif === 'true') {
                            df.field2 = filedR;
                            fieldsLeft.forEach(filedL => {
                                if (filedL.dif === 'true' && filedR['Field'] === filedL['Field']) {
                                    df.field1 = filedL;
                                }
                            });
                            if (!df.field1) {
                                df.field1 = {};
                            }
                            let t = list.filter((i) => {
                                if (i.tableName === f.tableName) {
                                    let a = i.difFields.filter(j => {
                                        return j['Field'] === df.field1['Field']
                                    })
                                    return a.length <= 0;
                                }
                            })
                            if (t.length <= 0)
                                f.difFields.push(df);
                        }
                    });

                    list.forEach(li => {
                        if (li.tableName === f.tableName) {
                            f.difFields.forEach(i => {
                                li.difFields.push(i);
                            })
                        }
                    })
                }
            });
            //if don't show no different tables, remove it.
            let temp = [];
            let finaResult = [];
            if (!that.showNoDifTable) {
                for (let idx in that.result) {
                    console.log(that.result[idx].left.TABLE_NAME + ':' + that.result[idx].left.dif)
                    if (that.result[idx].left.dif === 'true') {
                        finaResult.push(that.result[idx]);
                    }
                }
                that.result = finaResult;
            }
            $.extend(true, temp, that.result);
            for (let i in temp) {
                if (temp[i].left.dif === 'true') {
                    temp[i].left.des = [];
                    that.result[i].left.des.forEach(val => {
                        temp[i].left.des.push(val);
                    })
                    for (let j in temp) {
                        if (temp[i].left.fields[j]) {
                            temp[i].left.fields[j].difType = [];
                            if (that.result[i].left.fields[j].difType) {
                                that.result[i].left.fields[j].difType.forEach(val => {
                                    temp[i].left.fields[j].difType.push(val);
                                })
                            }
                        }
                    }
                }
                if (temp[i].right.dif === 'true') {
                    temp[i].right.des = [];
                    that.result[i].right.des.forEach(val => {
                        temp[i].right.des.push(val);
                    })
                    for (let j in temp) {
                        if (temp[i].right.fields[j]) {
                            temp[i].right.fields[j].difType = [];
                            if (that.result[i].right.fields[j].difType) {
                                that.result[i].right.fields[j].difType.forEach(val => {
                                    temp[i].right.fields[j].difType.push(val);
                                })
                            }
                        }
                    }
                }
            }
            data.dfJsonOriginal = JSON.stringify(temp);
            data.dfJson = JSON.stringify(list);
            that.formData.connectInfoLeft.tableNum = that.tablesInfoLeft.length;
            that.formData.connectInfoRight.tableNum = that.tablesInfoRight.length;
            data.db1 = JSON.stringify(that.formData.connectInfoLeft);
            data.db2 = JSON.stringify(that.formData.connectInfoRight);
            this.saveHistory(data);

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
            if (data.difType !== undefined && data.difType !== null && data.difType.length > 0) {
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
                    if (res.code === 0) {

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
            if (url.indexOf('mysql')!==-1) {
                let ip_port_db = url.match(/\:\/\/.*(\\?)?/)[0].replace('://', '');
                let ip = ip_port_db.substr(0, ip_port_db.indexOf(':'));
                let port = ip_port_db.substr(ip_port_db.indexOf(':') + 1, ip_port_db.indexOf('/') - ip_port_db.indexOf(':') - 1);
                // let db = ip_port_db.substr(ip_port_db.indexOf('/') + 1, ip_port_db.length - ip_port_db.indexOf('/'));
                let db = '';
                if(ip_port_db.indexOf('?')!==-1)
                    db  = ip_port_db.match(/\/.*.\?/)[0].replace('/','').replace('?','');
                else
                    db = ip_port_db.substr(ip_port_db.indexOf('/') + 1, ip_port_db.length - ip_port_db.indexOf('/'));
                let param = url.indexOf('?')===-1 ? '': url.substr(url.indexOf('?') + 1, url.length - ip_port_db.indexOf('?'));
                console.log(param);
                return {ip: ip, port: port, db: db, param: param};
            } else if (url.indexOf('oracle')!==-1) {
                let ip_port_db = url.match(/\@.*(\\?)?/)[0].replace('@', '');
                let ip = ip_port_db.substr(0, ip_port_db.indexOf(':'));
                let port = ip_port_db.substr(ip_port_db.indexOf(':') + 1, ip_port_db.lastIndexOf(':') - ip_port_db.indexOf(':') - 1);
                // let db = ip_port_db.substr(ip_port_db.indexOfqryFields('/') + 1, ip_port_db.length - ip_port_db.indexOf('/'));
                let db = '';
                if(ip_port_db.indexOf('?')!==-1)
                    db  = ip_port_db.substr(ip_port_db.lastIndexOf(':') + 1, ip_port_db.indexOf('?') - ip_port_db.lastIndexOf(':')-1);
                else
                    db = ip_port_db.substr(ip_port_db.lastIndexOf(':') + 1, ip_port_db.length - ip_port_db.lastIndexOf(':'));
                let param = url.indexOf('?')===-1 ? '' : url.substr(url.indexOf('?') + 1, url.length - ip_port_db.indexOf('?'));
                console.log(param);
                return {ip: ip, port: port, db: db, param: param};
            }
        }
    }
});
