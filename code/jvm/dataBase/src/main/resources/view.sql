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


create or replace view v_message as
select
    m.id as msgId,
    m.channel as msgChannel,
    m.author as msgAuthor,
    m.content as msgContent,
    m.timestamp as msgTimestamp,
    c.id as msgChannelId,
    c.owner as msgChannelName,
    u.id as authorId,
    u.name as authorUsername
from messages m
        join users u on m.author = u.id
        join channels c on m.channel = c.id;
