create or replace function f_channel_insert()
returns trigger as $$
begin
    insert into channel_members (channel, member) values (new.id, new.owner);
    return new;
end;
$$ language plpgsql;

create or replace trigger tr_channel_insert after insert on channels
    for each row
execute function f_channel_insert();

create or replace function remake_channel_view()
returns trigger as $$
begin
    drop view if exists v_channel;
    create or replace view v_channel as
    select
        c.id as channel_id,
        c.name as channel_name,
        c.owner as channel_owner,
        c.accessControl as channel_accessControl,
        c.visibility as channel_visibility,
        u.id as owner_id,
        u.name as owner_name
    from channels c
    join users u on c.owner = u.id;
    return new;
end;
$$ language plpgsql;

create or replace trigger tr_channel_update after update on channels
    for each statement
execute function remake_channel_view();