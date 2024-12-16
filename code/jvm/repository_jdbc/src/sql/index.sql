create index if not exists idx_users_name on users (name);

create index if not exists idx_users_tokens_token on users_tokens (token);

create index if not exists idx_channels_name on channels (name);

create index if not exists idx_channels_owner on channels (owner);

create index if not exists idx_channel_members_channel on channel_members (channel);

create index if not exists idx_channel_members_member on channel_members (member);

create index if not exists idx_messages_channel on messages (channel);

create index if not exists idx_messages_author on messages (channel, author);

create index if not exists idx_channel_invitation_token on channels_invitations (invitation);

create index if not exists idx_users_invitation_token on users_invitations (invitation);
