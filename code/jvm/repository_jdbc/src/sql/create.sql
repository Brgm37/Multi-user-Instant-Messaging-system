begin transaction;

create table if not exists users
(
    id       serial primary key,
    name     varchar(128) not null unique,
    password varchar(128) not null,
    token    varchar(128) unique
);

create table if not exists users_invitations
(
    user_id        integer      not null,
    invitation     varchar(128) not null,
    expirationDate timestamp    not null,
    foreign key (user_id) references users (id),
    primary key (user_id, invitation)
);

create table if not exists channels
(
    id            serial primary key,
    name          varchar(128)                                                       not null unique,
    owner         integer                                                            not null,
    visibility    varchar(10) check ( visibility in ('PUBLIC', 'PRIVATE') )          not null,
    accessControl varchar(10) check ( accessControl in ('READ_ONLY', 'READ_WRITE') ) not null,
    foreign key (owner) references users (id)
);

create table if not exists channels_invitations
(
    channel_id     integer                                                            not null,
    invitation     varchar(128)                                                       not null,
    expirationDate timestamp                                                          not null,
    accessControl  varchar(10) check ( accessControl in ('READ_ONLY', 'READ_WRITE') ) not null,
    maxUses        integer,
    foreign key (channel_id) references channels (id),
    primary key (channel_id)
);

create table if not exists channel_members
(
    id            serial primary key,
    channel       integer                                                            not null,
    member        integer                                                            not null,
    accessControl varchar(10) check ( accessControl in ('READ_ONLY', 'READ_WRITE') ) not null,
    foreign key (channel) references channels (id),
    foreign key (member) references users (id)
);

create table if not exists messages
(
    id        serial primary key,
    channel   integer   not null,
    author    integer   not null,
    content   text      not null,
    timestamp timestamp not null,
    foreign key (channel) references channels (id),
    foreign key (author) references users (id)
);

commit;
