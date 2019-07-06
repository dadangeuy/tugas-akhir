create table device_token
(
    device_token_id uniqueidentifier not null,
    client_id uniqueidentifier not null,
    user_id uniqueidentifier not null,
    device_token varchar(255),
    device_type char,
    active numeric(1),
    reg_date datetime,
    invalidate_date datetime,
    constraint PK_DEVICE_TOKEN
        primary key (device_token_id),
    constraint FK_DEVICE_T_CLIENT_DE_OAUTH_CL
        foreign key (client_id) references oauth_client,
    constraint FK_DEVICE_T_USER_DEVI_USER_ACC
        foreign key (user_id) references user_account
)
