/*==============================================================*/
/* Table: ds                                                    */
/*==============================================================*/
create table if not exists ds
(
    ds_id                 bigint      not null auto_increment,
    name                  varchar(50) not null,
    type                  smallint    not null default 1,
    config                text        not null,
    ds_config_template_id bigint      not null,
    status                varchar(10) not null,
    create_user           bigint,
    update_user           bigint,
    create_date           timestamp            default CURRENT_TIMESTAMP,
    update_date           timestamp            default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark                varchar(500),
    primary key (ds_id)
);

/*==============================================================*/
/* Table: ds_config_template                                    */
/*==============================================================*/
create table if not exists ds_config_template
(
    ds_config_template_id bigint      not null auto_increment,
    name                  varchar(50) not null,
    content               text        not null,
    status                varchar(10) not null,
    create_user           bigint,
    update_user           bigint,
    create_date           timestamp default CURRENT_TIMESTAMP,
    update_date           timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark                varchar(500),
    primary key (ds_config_template_id)
);

/*==============================================================*/
/* Table: groups                                                */
/*==============================================================*/
create table if not exists groups
(
    groups_id   bigint      not null auto_increment,
    name        varchar(50) not null,
    type        smallint    not null comment '1:sql片段分组 2:业务场景分组',
    status      varchar(10) not null,
    create_user bigint,
    update_user bigint,
    create_date timestamp default CURRENT_TIMESTAMP,
    update_date timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark      varchar(500),
    primary key (groups_id)
);

/*==============================================================*/
/* Table: history                                               */
/*==============================================================*/
create table if not exists history
(
    history_id      bigint not null auto_increment,
    sql_fragment_id bigint,
    ds_id           char(10),
    in_param        varchar(5000),
    out_param       varchar(5000),
    sql_content     varchar(5000),
    primary key (history_id)
);

/*==============================================================*/
/* Table: scene                                                 */
/*==============================================================*/
create table if not exists scene
(
    scene_id    bigint      not null auto_increment,
    groups_id   bigint,
    name        varchar(50) not null,
    status      varchar(10) not null,
    create_user bigint,
    update_user bigint,
    create_date timestamp default CURRENT_TIMESTAMP,
    update_date timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark      varchar(500),
    primary key (scene_id)
);

/*==============================================================*/
/* Table: scene_sql_fragment_rel                                */
/*==============================================================*/
create table if not exists scene_sql_fragment_rel
(
    scene_sql_fragment_rel_id bigint      not null auto_increment,
    scene_id                  bigint      not null,
    sql_fragment_id           bigint      not null,
    order_num                 int,
    status                    varchar(10) not null,
    create_user               bigint,
    update_user               bigint,
    create_date               timestamp default CURRENT_TIMESTAMP,
    update_date               timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark                    varchar(500),
    primary key (scene_sql_fragment_rel_id)
);

/*==============================================================*/
/* Table: sql_fragment                                          */
/*==============================================================*/
create table if not exists sql_fragment
(
    sql_fragment_id bigint        not null auto_increment,
    ds_id           bigint        not null,
    groups_id       bigint,
    name            varchar(50)   not null,
    sql_content     varchar(5000) not null,
    status          varchar(10)   not null,
    create_user     bigint,
    update_user     bigint,
    create_date     timestamp default CURRENT_TIMESTAMP,
    update_date     timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark          varchar(500),
    primary key (sql_fragment_id)
);



/*==============================================================*/
/* Table: sql_fragment_parameter                                */
/*==============================================================*/
create table if not exists sql_fragment_parameter
(
    parameter_id    bigint      not null auto_increment,
    sql_fragment_id bigint      not null,
    name            varchar(50) not null,
    data_type       varchar(10) not null default 'STRING' comment '数据类型:STRING字符串 NUMBER数字 默认STRING',
    parameter_index smallint    not null,
    default_value   varchar(500),
    not_empty       smallint    not null default 0 comment '是否允许为空:0 可为空 1不允许为空 默认0',
    status          varchar(10) not null,
    create_user     bigint,
    update_user     bigint,
    create_date     timestamp            default CURRENT_TIMESTAMP,
    update_date     timestamp            default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark          varchar(500),
    primary key (parameter_id)
);
