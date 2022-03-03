let app = new Vue({
    el: "#app",
    data: {
        menuDatas: common_util.menuDatas,
        initData: {
            compareTypes: [
                {key: 'Filed', title: '字段名称'},
                {key: 'Type', title: '字段类型'},
                {key: 'Null', title: '允许空值'},
                {key: 'Key', title: '键类型'},
                {key: 'Default', title: '默认值'},
                {key: 'Privileges', title: '字段权限'},
                {key: 'Comment', title: '注释'}
            ],
            default: {
                selectedCompareType: ['Filed', 'Type', 'Null', 'Default'],
                showNoDifTable: false
            }
        },
        selectedCompareType: [],
        showNoDifTable: false,
    },
    mounted: function () {
        common_util.selectMenu();
        this.init();
    },
    watch: {},
    methods: {
        init: function () {
            this.selectedCompareType = localStorage.getItem('selectedCompareType').split(',');
            this.showNoDifTable = localStorage.getItem('showNoDifTable') === 'true';
        },
        checkAll() {
            let that = this;
            if (that.selectedCompareType.length === that.initData.compareTypes.length) {
                that.selectedCompareType.splice(0, that.initData.compareTypes.length);
            } else {
                that.selectedCompareType = [];
                that.initData.compareTypes.forEach(item => {
                    that.selectedCompareType.push(item.key);
                })
            }
        },
        save: function () {
            localStorage.setItem('selectedCompareType', this.selectedCompareType);
            localStorage.setItem('showNoDifTable', this.showNoDifTable);
            this.$Message({
                type: 'success',
                text: `保存成功`
            });
        },
        reset: function () {
            this.selectedCompareType = this.initData.default.selectedCompareType;
            this.showNoDifTable = this.initData.default.showNoDifTable;
            localStorage.setItem('selectedCompareType', this.selectedCompareType);
            localStorage.setItem('showNoDifTable', this.showNoDifTable);
        }
    }
});