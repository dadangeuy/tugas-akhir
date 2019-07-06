create table pn_group_destination
(
    pn_batch_id uniqueidentifier not null,
    pn_group_id uniqueidentifier not null,
    created_at datetime not null,
    update_at datetime not null,
    constraint PK_PN_GROUP_DESTINATION
        primary key (pn_batch_id, pn_group_id),
    constraint FK_PN_GROUP_BATCH_GRO_PN_BATCH
        foreign key (pn_batch_id) references pn_batch,
    constraint FK_PN_GROUP_GROUP_PN__PN_GROUP
        foreign key (pn_group_id) references pn_group
)
