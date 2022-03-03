create table if not exists OAUTH_ACCESS_TOKEN
(
    TOKEN_ID          VARCHAR(256),
    TOKEN             binary,
    AUTHENTICATION_ID VARCHAR(256) not null
        primary key,
    USER_NAME         VARCHAR(256),
    CLIENT_ID         VARCHAR(256),
    AUTHENTICATION    binary,
    REFRESH_TOKEN     VARCHAR(256)
);

create table if not exists OAUTH_APPROVALS
(
    USERID         VARCHAR(256),
    CLIENTID       VARCHAR(256),
    SCOPE          VARCHAR(256),
    STATUS         VARCHAR(10),
    EXPIRESAT      TIMESTAMP(26, 6),
    LASTMODIFIEDAT TIMESTAMP(26, 6)
);

create table if not exists OAUTH_CLIENT_DETAILS
(
    CLIENT_ID               VARCHAR(256) not null
        primary key,
    RESOURCE_IDS            VARCHAR(256),
    CLIENT_SECRET           VARCHAR(256),
    SCOPE                   VARCHAR(256),
    AUTHORIZED_GRANT_TYPES  VARCHAR(256),
    WEB_SERVER_REDIRECT_URI VARCHAR(256),
    AUTHORITIES             VARCHAR(256),
    ACCESS_TOKEN_VALIDITY   INTEGER,
    REFRESH_TOKEN_VALIDITY  INTEGER,
    ADDITIONAL_INFORMATION  VARCHAR(4096),
    AUTOAPPROVE             VARCHAR(256)
);

create table if not exists OAUTH_CLIENT_TOKEN
(
    TOKEN_ID          VARCHAR(256),
    TOKEN             binary,
    AUTHENTICATION_ID VARCHAR(256) not null
        primary key,
    USER_NAME         VARCHAR(256),
    CLIENT_ID         VARCHAR(256)
);

create table if not exists OAUTH_CODE
(
    CODE           VARCHAR(256),
    AUTHENTICATION binary
);

create table if not exists OAUTH_REFRESH_TOKEN
(
    TOKEN_ID       VARCHAR(256),
    TOKEN          binary,
    AUTHENTICATION binary
);

create table if not exists sys_dic_code
(
    dic_code      varchar(50)                           not null
        primary key,
    dic_code_name varchar(50)                           not null,
    description   varchar(100)                          null,
    create_time   timestamp default current_timestamp() not null comment '创建时间',
    is_show       smallint  default 0
);

create table if not exists sys_dic_item
(
    item_code   varchar(50)                            not null comment '字典项',
    item_text   varchar(500)                           null comment '字典值',
    item_value  varchar(5000)                          null,
    dic_code    varchar(50)                            not null comment '字典分组',
    description varchar(100)                           null comment '说明',
    create_time timestamp  default current_timestamp() not null comment '创建时间',
    status      varchar(4) default '1000'              null comment '状态：1000正常 2000 删除'
);

create table if not exists purview
(
    purview_id   bigint auto_increment
        primary key,
    purview_name varchar(50)                         not null,
    purview_type varchar(10)                         not null comment '1000 菜单权限 2000功能权限',
    purview_code varchar(50)                         not null comment '权限编码',
    url          varchar(500)                        null,
    icon         varchar(100)                        null,
    component    varchar(500)                        null,
    order_num    int                                 null,
    parent_id    bigint                              null,
    status       varchar(10)                         not null,
    create_user  bigint                              null,
    update_user  bigint                              null,
    create_date  timestamp default CURRENT_TIMESTAMP not null,
    update_date  timestamp default CURRENT_TIMESTAMP,
    remark       varchar(500)                        null
);

create table if not exists role
(
    role_id     bigint auto_increment
        primary key,
    role_code   varchar(50)                         null,
    role_name   varchar(50)                         null,
    status      varchar(10)                         not null,
    create_user bigint                              null,
    update_user bigint                              null,
    create_date timestamp default CURRENT_TIMESTAMP not null,
    update_date timestamp default CURRENT_TIMESTAMP,
    remark      varchar(500)                        null
);

create table if not exists role_purview_rel
(
    role_purview_rel_id bigint auto_increment
        primary key,
    role_id             bigint                                  not null,
    purview_id          bigint                                  not null,
    eff_date            timestamp default CURRENT_TIMESTAMP     not null,
    exp_date            timestamp default '0000-00-00 00:00:00' not null,
    status              varchar(10)                             not null,
    create_user         bigint                                  null,
    update_user         bigint                                  null,
    create_date         timestamp default CURRENT_TIMESTAMP     not null,
    update_date         timestamp default CURRENT_TIMESTAMP,
    remark              varchar(500)                            null
);

create table if not exists user_purview_rel
(
    user_purview_rel_id bigint auto_increment
        primary key,
    purview_id          bigint                                  not null,
    user_id             bigint                                  null,
    eff_date            timestamp default CURRENT_TIMESTAMP     not null,
    exp_date            timestamp default '0000-00-00 00:00:00' not null,
    status              varchar(10)                             not null,
    create_user         bigint                                  null,
    update_user         bigint                                  null,
    create_date         timestamp default CURRENT_TIMESTAMP     not null,
    update_date         timestamp default CURRENT_TIMESTAMP,
    remark              varchar(500)                            null
);

create table if not exists user_role_rel
(
    user_role_rel_id bigint auto_increment
        primary key,
    user_id          bigint                                  not null,
    role_id          bigint                                  not null,
    eff_date         timestamp default CURRENT_TIMESTAMP     not null,
    exp_date         timestamp default '0000-00-00 00:00:00' not null,
    status           varchar(10)                             not null,
    create_user      bigint                                  null,
    update_user      bigint                                  null,
    create_date      timestamp default CURRENT_TIMESTAMP     not null,
    update_date      timestamp default CURRENT_TIMESTAMP,
    remark           varchar(500)                            null
);

create table if not exists user_attr
(
    user_attr_id bigint auto_increment
        primary key,
    user_id      bigint                              not null,
    attr_code    varchar(50)                         not null comment '关联sys_dic_code.dic_code',
    attr_value   varchar(500)                        null,
    status       varchar(10)                         not null,
    create_user  bigint                              null,
    update_user  bigint                              null,
    create_date  timestamp default CURRENT_TIMESTAMP not null,
    update_date  timestamp default CURRENT_TIMESTAMP,
    remark       varchar(500)                        null
);



create table if not exists user
(
    user_id     bigint auto_increment
        primary key,
    user_name   varchar(50)                         not null,
    user_type   varchar(10)                         null,
    password    varchar(512)                        null,
    phone       varchar(11)                         null,
    email       varchar(100)                        null,
    status      varchar(10)                         not null,
    create_user bigint                              not null,
    update_user bigint                              null,
    create_date timestamp default CURRENT_TIMESTAMP not null,
    update_date timestamp default CURRENT_TIMESTAMP,
    remark      varchar(500)                        null
);

comment on table user is '用户信息';

create table if not exists user_auth
(
    user_auth_id bigint auto_increment
        primary key,
    user_id      bigint                              not null,
    user_type    char(10)  default '1000'            not null comment '1000 普通用户 2000 商户',
    login_type   varchar(10)                         null,
    openid       varchar(128)                        null,
    access_token varchar(256)                        null,
    status       varchar(10)                         null,
    create_date  timestamp default CURRENT_TIMESTAMP not null,
    update_date  timestamp default CURRENT_TIMESTAMP,
    remark       varchar(500)                        null
);




