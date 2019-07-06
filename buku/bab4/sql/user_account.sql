create table user_account
(
    user_id uniqueidentifier not null,
    name varchar(150) not null,
    nickname varchar(20),
    username varchar(255),
    password varchar(256) not null,
    email varchar(255),
    email_verified numeric(1) not null,
    scope varchar(4000),
    alternate_email varchar(255),
    alternate_email_verified numeric(1) not null,
    phone varchar(18),
    phone_verified numeric(1) not null,
    enabled numeric(1),
    picture varbinary(max),
    gender char,
    birthdate date,
    zoneinfo varchar(40),
    locale varchar(10),
    integra_id numeric(12),
    reg_id varchar(25),
    must_change_pwd numeric(1),
    sandbox numeric(1),
    locked datetime,
    suspended datetime,
    has_suspended numeric(1),
    group_id int not null,
    auth_method_id numeric(2),
    created_at datetime not null,
    update_at datetime not null,
    constraint PK_USER_ACCOUNT
        primary key (user_id),
    constraint FK_USER_ACC_AUTH_METH_AUTH_MET
        foreign key (auth_method_id) references auth_method,
    constraint FK_USER_ACC_USER_GROU_USER_GRO
        foreign key (group_id) references user_group
)
