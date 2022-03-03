create table if not exists db_dif_history
(
    history_id       int auto_increment not null,
    db_1             varchar(5000)      not null,
    db_2             varchar(5000)      not null,
    df_json          varchar(max),
    df_json_original varchar(max),
    create_date      timestamp default current_timestamp,
    update_date      timestamp default current_timestamp,
    primary key (history_id)
);