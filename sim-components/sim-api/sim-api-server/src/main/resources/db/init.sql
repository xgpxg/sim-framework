create table if not exists sa_api
(
    api_id          int                        not null AUTO_INCREMENT,
    app_name        varchar(100) comment '应用名称',
    app_addr        varchar(500) comment '应用地址',
    api_name        varchar(100) comment 'api名称',
    api_url         varchar(500) comment 'api地址',
    req_method      varchar(20) comment '请求方法',
    res_data        varchar(max) comment '响应数据',
    res_type        varchar(200) comment '响应类型',
    api_type        varchar(10) default '1000' not null comment 'api类型:1000 已存在的,2000 手动添加的',
    api_desc        varchar(500) comment '描述',
    method          varchar(10000) comment 'api实现方法元数据',
    create_time     timestamp   default current_timestamp,
    update_time     timestamp   default current_timestamp,
    open_simulation int         default 0 comment '是否开启模拟,0不开启 1开启',
    status          varchar(10) default '1000' comment 'API状态,1000:有效,2000:无效',
    primary key (api_id)
);


