begin transaction;

create table if not exists users
(
    id       serial primary key,
    name     varchar(128) not null unique,
    password varchar(128) not null
);

create table if not exists users_tokens
(
    user_id    integer      not null,
    token      varchar(128) not null unique,
    creation   timestamp    not null,
    expiration timestamp    not null,
    foreign key (user_id) references users (id),
    primary key (user_id, token)
);

create table if not exists users_invitations
(
    user_id        integer      not null,
    invitation     varchar(128) not null,
    expiration_date timestamp    not null,
    foreign key (user_id) references users (id),
    primary key (user_id, invitation)
);

create table if not exists channels
(
    id            serial primary key,
    name          varchar(128)                                                       not null unique,
    owner         integer                                                            not null,
    visibility    varchar(10) check ( visibility in ('PUBLIC', 'PRIVATE') )          not null,
    access_control varchar(10) check ( access_control in ('READ_ONLY', 'READ_WRITE') ) not null,
    foreign key (owner) references users (id)
);

create table if not exists channels_invitations
(
    channel_id     integer                                                            not null,
    invitation     varchar(128)                                                       not null,
    expiration_date timestamp                                                          not null,
    access_control  varchar(10) check ( access_control in ('READ_ONLY', 'READ_WRITE') ) not null,
    max_uses        integer,
    foreign key (channel_id) references channels (id),
    primary key (channel_id)
);

create table if not exists channel_members
(
    id            serial primary key,
    channel       integer                                                            not null,
    member        integer                                                            not null,
    access_control varchar(10) check ( access_control in ('READ_ONLY', 'READ_WRITE') ) not null,
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