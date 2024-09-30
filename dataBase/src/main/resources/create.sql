begin transaction;
create table if not exists users (
    id serial primary key,
    name varchar(128) not null unique,
    password varchar(128) not null,
    token varchar(128)
);

create table if not exists channels (
    id serial primary key,
    name varchar(128) not null unique,
    owner integer not null,
    accessControl varchar(10) check ( accessControl in ('READ_ONLY', 'READ_WRITE') ) not null,
    visibility varchar(10) check ( visibility in ('PUBLIC', 'PRIVATE') ) not null,
    foreign key (owner) references users(id)
);

create table if not exists channel_members (
    id serial primary key,
    channel integer not null,
    member integer not null,
    foreign key (channel) references channels(id),
    foreign key (member) references users(id)
);

create table if not exists messages (
    id serial primary key,
    channel integer not null,
    author integer not null,
    content text not null,
    timestamp timestamp not null,
    foreign key (channel) references channels(id),
    foreign key (author) references users(id)
);

commit;
