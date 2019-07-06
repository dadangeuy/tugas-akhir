create table pn_certificate
(
    cert_id uniqueidentifier not null,
    client_id uniqueidentifier not null,
    bundle_id varchar(255),
    cert_key text,
    type char,
    password varchar(255),
    constraint PK_PN_CERTIFICATE
        primary key nonclustered (cert_id),
    constraint FK_PN_CERTI_OAUTH_CLI_OAUTH_CL
        foreign key (client_id) references oauth_client
)
