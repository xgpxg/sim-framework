let common_util={
    menuDatas: [
        {
            title: '快速分析',
            key: 'home',
            icon: 'h-icon-task',
            href: '/home',
            nativeLink: true
        },
        {
            title: '历史记录',
            key: 'history',
            icon: 'h-icon-search',
            href: '/history',
            nativeLink: true
        },
        {
            title: '对比设置',
            key: 'setting',
            icon: 'h-icon-setting',
            href: '/setting',
            nativeLink: true
        },
        {
            title: '浏览文档',
            key: 'git',
            icon: 'h-icon-help',
            href: 'https://gitee.com/xgpxg/sim-framework/tree/master/sim-db-dif',
            nativeLink: true,
            target: '_black'
        }
    ],
    selectMenu:function () {

        $('.h-menu-show').each((i,ele)=>{
            $(ele).removeClass('h-menu-li-selected');
        });
        $('.h-menu-show').each((i,ele)=>{
            var text = $(ele).find('.h-menu-show-desc').text();
            this.menuDatas.forEach(m=>{
                if (window.location.pathname===m.href && m.title===text && m.target!=='_black'){
                    $(ele).addClass('h-menu-li-selected');
                }
            })
        })
    },
    getUrlPath:function () {
        return window.location.pathname;
    }
}
