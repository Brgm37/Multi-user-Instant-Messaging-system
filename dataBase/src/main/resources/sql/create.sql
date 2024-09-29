begin transaction;

create table "user" (
    id serial primary key,
    name varchar(255) unique,
    password varchar(255),
    token varchar(255)
);

create table channel (
    id serial primary key,
    name varchar(255) unique,
    owner int references "user"(id)
);

create table message (
    id serial primary key,
    content text,
    date timestamp,
    author int references "user"(id),
    channel int references channel(id)
);

commit;