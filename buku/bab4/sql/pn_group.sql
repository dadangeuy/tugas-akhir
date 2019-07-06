create table pn_group
(
    pn_group_id uniqueidentifier not null,
    name varchar(100),
    created_at datetime not null,
    update_at datetime not null,
    constraint PK_PN_GROUP
        primary key (pn_group_id)
)
