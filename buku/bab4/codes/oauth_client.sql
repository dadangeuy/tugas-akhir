create table oauth_client
(
    client_id uniqueidentifier not null,
    client_name varchar(100) not null,
    client_description varchar(250),
    client_secret varchar(255),
    expires_at datetime,
    logo varchar(100),
    redirect_uri varchar(2000),
    post_logout_redirect_uris varchar(2048),
    frontchannel_logout_uri varchar(255),
    frontchannel_logout_session_required numeric(1),
    backchannel_logout_uri varchar(255),
    backchannel_logout_session_required numeric(1),
    base_uri varchar(255),
    api_base_uri varchar(255),
    app_type char,
    contact_name varchar(255),
    contact_email varchar(255),
    preauthorized numeric(1) not null,
    grant_types varchar(80),
    scope varchar(4000),
    sandbox numeric(1) not null,
    is_moderated numeric(1) not null,
    visible numeric(1) not null,
    category_id int,
    auth_type_id char,
    user_id uniqueidentifier,
    provider_id uniqueidentifier not null,
    created_at datetime not null,
    update_at datetime not null,
    constraint PK_OAUTH_CLIENT
        primary key (client_id),
    constraint FK_OAUTH_CL_AUTH_TYPE_AUTH_TYP
        foreign key (auth_type_id) references auth_type,
    constraint FK_OAUTH_CL_CLIENT_CL_CLIENT_C
        foreign key (category_id) references client_category,
    constraint FK_OAUTH_CL_PROVIDER__PROVIDER
        foreign key (provider_id) references provider,
    constraint FK_OAUTH_CL_USER_PIC_USER_ACC
        foreign key (user_id) references user_account
)
