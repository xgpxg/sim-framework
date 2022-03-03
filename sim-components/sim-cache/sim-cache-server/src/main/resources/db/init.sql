/*==============================================================*/
/* Table: cache_instance                                        */
/*==============================================================*/
create table if not  exists cache_instance
(
    cache_instance_id    bigint not null auto_increment,
    name                 varchar(50) not null,
    type                 varchar(10) not null comment '默认redis',
    connect_type         varchar(10) not null comment 'standalone 单机 cluster集群。 默认单机连接standalone',
    host                 varchar(50) comment '单机连接时不为空',
    port                 int comment '单机连接时不为空',
    nodes                varchar(500) comment '集群连接时不为空',
    status               varchar(10) not null,
    create_user          bigint,
    update_user          bigint,
    create_date          timestamp default CURRENT_TIMESTAMP,
    update_date          timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark               varchar(500),
    primary key (cache_instance_id)
);
