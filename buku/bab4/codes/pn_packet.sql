create table pn_packet
(
    pn_packet_id uniqueidentifier not null,
    pn_batch_id uniqueidentifier not null,
    device_token_id uniqueidentifier,
    sent_at datetime,
    reason varchar(255),
    packet_status numeric(1) not null,
    created_at datetime not null,
    update_at datetime not null,
    constraint PK_PN_PACKET
        primary key (pn_packet_id),
    constraint FK_PN_PACKE_DEVICE_TO_DEVICE_T
        foreign key (device_token_id) references device_token,
    constraint FK_PN_PACKE_PN_BATCH__PN_BATCH
        foreign key (pn_batch_id) references pn_batch
)
