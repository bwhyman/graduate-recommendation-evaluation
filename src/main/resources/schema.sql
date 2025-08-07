/**/
create table if not exists `user`
(
    id          bigint primary key,
    name        varchar(15) not null,
    account     varchar(15) not null,
    password    varchar(65) not null,
    mobile      varchar(11) null,
    role        char(5)     not null,
    coll_id     bigint      null,
    major_id    bigint      null,
    cat_id      bigint      null,

    create_time datetime    not null default current_timestamp,
    update_time datetime    not null default current_timestamp on update current_timestamp,

    unique (account),
    index (major_id)
);

create table if not exists `college`
(
    id          bigint primary key,
    name        varchar(20) not null,

    create_time datetime    not null default current_timestamp,
    update_time datetime    not null default current_timestamp on update current_timestamp
);

/* 主键为用户名共同索引，业务层实现 */
create table if not exists `weighted_score`
(
    id          bigint primary key,
    score       decimal(6, 3)    not null check ( score > 0 and score <= 100.00),
    ranking     tinyint unsigned not null,
    comment     text             null,
    verified    tinyint unsigned not null default 0,

    create_time datetime         not null default current_timestamp,
    update_time datetime         not null default current_timestamp on update current_timestamp
);

create table if not exists `weighted_score_log`
(
    id          bigint primary key,
    student_id  bigint      not null,
    user_id     bigint      not null,

    comment     text        null,
    create_time datetime    not null default current_timestamp,

    index (student_id)
);

create table if not exists `category`
(
    id          bigint primary key,
    name        varchar(20) not null,
    coll_id     bigint      not null,
    comment     text        null,
    weighting   json        not null comment '{score, compositeScore}',
    due_time    datetime    null,

    create_time datetime    not null default current_timestamp,
    update_time datetime    not null default current_timestamp on update current_timestamp,

    index (coll_id)
);

create table if not exists `user_category`
(
    id          bigint primary key,
    user_id     bigint   not null,
    cat_id      bigint   not null,

    create_time datetime not null default current_timestamp,
    update_time datetime not null default current_timestamp on update current_timestamp,

    index (user_id),
    index (cat_id)
);

/**/
create table if not exists `major`
(
    id          bigint primary key,
    name        varchar(20) not null,
    cat_id      bigint      not null,

    create_time datetime    not null default current_timestamp,
    update_time datetime    not null default current_timestamp on update current_timestamp,

    index (cat_id)
);

create table if not exists `item`
(
    id          bigint primary key,
    name        varchar(200)           not null,
    cat_id      bigint                 not null,
    max_points  decimal(5, 2) unsigned not null comment '上限点数',
    max_items   tinyint unsigned       null comment '限项数',
    parent_id   bigint                 null comment '上级指标',
    comment     text                   null,

    create_time datetime               not null default current_timestamp,
    update_time datetime               not null default current_timestamp on update current_timestamp,

    index (parent_id, cat_id)
);


create table if not exists `student_item`
(
    id           bigint primary key,
    user_id      bigint                 not null,
    root_item_id bigint                 not null,
    item_id      bigint                 not null,
    point        decimal(5, 2) unsigned null check ( point >= 0 and point <= 100 ),
    name         varchar(200)           not null,
    comment      text                   null,
    status       char(4)                not null comment '已提交；已驳回，已认定；待修改',

    create_time  datetime               not null default current_timestamp,
    update_time  datetime               not null default current_timestamp on update current_timestamp,

    index (user_id, root_item_id, item_id, status)
);

create table if not exists `student_item_file`
(
    id              bigint primary key,
    student_item_id bigint       not null,
    path            varchar(100) not null,
    filename        varchar(100) not null,
    create_time     datetime     not null default current_timestamp,
    update_time     datetime     not null default current_timestamp on update current_timestamp,

    index (student_item_id)
);


create table if not exists `student_item_log`
(
    id              bigint primary key,
    student_item_id bigint      not null,
    user_id         bigint      not null,
    comment         text        null,
    create_time     datetime    not null default current_timestamp,

    index (student_item_id)
);


/*create table if not exists `item_log`
(
    id          bigint primary key,
    user_id     bigint not null,
    item_id     bigint not null,
    comment     text       null,
    create_time datetime   not null default current_timestamp
);
*/



