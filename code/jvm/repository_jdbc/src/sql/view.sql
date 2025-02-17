create or replace view v_channel as
select c.id             as channel_id,
       c.name           as channel_name,
       c.owner          as channel_owner,
       c.visibility     as channel_visibility,
       u.name           as owner_name,
       c.access_control as channel_accessControl,
       c.description    as channel_description,
       c.icon           as channel_icon
from channels c
         join users u on c.owner = u.id;


create or replace view v_message as
select m.id        as msgId,
       m.channel   as msgChannelId,
       m.author    as msgAuthorId,
       m.content   as msgContent,
       m.timestamp as msgTimestamp,
       c.name      as msgChannelName,
       u.name      as msgAuthorUsername
from messages m
         join users u on m.author = u.id
         join channels c on m.channel = c.id;
