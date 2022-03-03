/*==============================================================*/
/* Table: route                                                 */
/*==============================================================*/
create table if not exists route
(
    route_id             bigint not null auto_increment,
    id varchar(100) not null ,
    service_id           varchar(100) not null,
    url                  varchar(500),
    type                 varchar(10),
    prefix               varchar(100),
    path                 varchar(100) not null,
    service_name         varchar(50) not null,
    strip_prefix          smallint default 1,
    retryable            smallint default 0,
    sensitive_headers     varchar(100),
    custom_sensitive_hHeaders varchar(100),
    status               smallint not null default 1 comment '0 未生效 1正常 2停用 9已删除',
    version              varchar(20),
    primary key (route_id)
);

create table if not exists  route_rate_limit_policy
(
    route_rate_limit_policy_id bigint not null auto_increment,
    route_id             bigint,
    "limit"              bigint not null,
    quota                bigint not null,
    refresh_interval     bigint not null,
    match_type           varchar(50) not null,
    break_on_match       bool not null,
    primary key (route_rate_limit_policy_id)
);


