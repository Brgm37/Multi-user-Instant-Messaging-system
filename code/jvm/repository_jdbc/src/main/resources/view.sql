create or replace view v_channel as
select
    c.id as channel_id,
    c.name as channel_name,
    c.owner as channel_owner,
    c.accessControl as channel_accessControl,
    c.visibility as channel_visibility,
    u.name as owner_name
from channels c
join users u on c.owner = u.id;