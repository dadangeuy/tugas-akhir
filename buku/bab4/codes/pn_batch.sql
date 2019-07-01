create table pn_batch
(
    pn_batch_id uniqueidentifier not null,
    title varchar(255),
    body varchar(255),
    image varchar(255),
    sound varchar(255),
    action varchar(255),
    additional_data text,
    delivery_date datetime,
    started_at datetime,
    finished_at datetime,
    is_allowed numeric(1) not null,
    user_sender_id uniqueidentifier,
    client_sender_id uniqueidentifier,
    client_dest_id uniqueidentifier,
    created_at datetime not null,
    update_at datetime not null,
    constraint PK_PN_BATCH
        primary key nonclustered (pn_batch_id),
    constraint FK_PN_BATCH_CLIENT_DE_OAUTH_CL
        foreign key (client_dest_id) references oauth_client,
    constraint FK_PN_BATCH_CLIENT_SE_OAUTH_CL
        foreign key (client_sender_id) references oauth_client,
    constraint FK_PN_BATCH_USER_SEND_USER_ACC
        foreign key (user_sender_id) references user_account
)
