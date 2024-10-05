create index if not exists idx_users_name on users(name);

create index if not exists idx_channels_name on channels(name);

create index if not exists idx_channel_members_channel on channel_members(channel);

create index if not exists idx_messages_channel on messages(channel);

create index if not exists idx_messages_author on messages(channel, author);