create table pn_user_destination
(
    pn_batch_id uniqueidentifier not null,
    user_id uniqueidentifier not null,
    created_at datetime not null,
    update_at datetime not null,
    constraint PK_PN_USER_DESTINATION
        primary key (pn_batch_id, user_id),
    constraint FK_PN_USER__BATCH_USE_PN_BATCH
        foreign key (pn_batch_id) references pn_batch,
    constraint FK_PN_USER__USER_PN_D_USER_ACC
        foreign key (user_id) references user_account
)
