create table pn_group_member
(
    pn_group_id uniqueidentifier not null,
    user_id uniqueidentifier not null,
    created_at datetime not null,
    update_at datetime not null,
    constraint PK_PN_GROUP_MEMBER
        primary key (pn_group_id, user_id),
    constraint FK_PN_GROUP_PN_GROUP__PN_GROUP
        foreign key (pn_group_id) references pn_group,
    constraint FK_PN_GROUP_USER_PN_G_USER_ACC
        foreign key (user_id) references user_account
)
