# Data Access
The following SQL statements are utilized to access data within the database.

## SQL Statements:

### Users:

#### Create a User:
```sql
INSERT INTO users (name, password)
VALUES (?, ?) RETURNING id
```

#### Find a User by ID:
```sql
SELECT id, name, password
FROM users
WHERE id = ?
```

#### Find a User by Username:
```sql
SELECT id, name, password
FROM users
WHERE name = ?
```

#### Delete a User:
```sql
DELETE FROM users_invitations WHERE user_id = ?
```
```sql
DELETE FROM users_tokens WHERE user_id = ?
```
```sql
DELETE FROM messages WHERE author = ?
```
```sql
DELETE FROM channel_members WHERE member = ?
```
```sql
DELETE FROM users WHERE id = ?
```

#### Create a User Invitation:
```sql
INSERT INTO users_invitations (user_id, invitation, expiration_date)
VALUES (?, ?, ?)
```

#### Find a User Invitation:
```sql
SELECT user_id, invitation, expiration_date
FROM users_invitations
WHERE user_id = ? AND invitation = ?
```

#### Delete a User Invitation:
```sql
DELETE FROM users_invitations
WHERE user_id = ? AND invitation = ?
```

#### Create a User Token:
```sql
INSERT INTO users_tokens (user_id, token, creation, expiration)
VALUES (?, ?, ?, ?)
```

#### Validate a Token:
```sql
SELECT user_id
FROM users_tokens
WHERE token = ?
```

#### Find a User by Token:
```sql
SELECT user_id
FROM users_tokens
WHERE token = ?
```

#### Delete a User Token:
```sql
DELETE FROM users_tokens
WHERE token = ?
```

#### Count User Tokens:
```sql
SELECT COUNT(*) FROM users_tokens WHERE user_id = ?
```

#### Delete Old User Tokens:
```sql
DELETE FROM users_tokens
WHERE token IN (
    SELECT token
    FROM users_tokens
    WHERE user_id = ?
    ORDER BY creation 
    FETCH FIRST 1 ROWS ONLY
)
```

#### Find All Users:
```sql
SELECT id, name, password
FROM users
OFFSET ?
LIMIT ?
```

#### Save a User:
```sql
UPDATE users
SET name = ?, password = ?
WHERE id = ?
```

### Channels:

#### Create a Channel:
```sql
INSERT INTO channels (owner, name, access_control, visibility)
VALUES (?, ?, ?, ?) RETURNING id
```

#### Find a Channel by User ID:
```sql
SELECT 
    channel_id, channel_name, channel_owner, channel_accessControl,
    channel_visibility, owner_name
FROM v_channel
WHERE channel_owner = ?
LIMIT ?
OFFSET ?
```

#### Find a Channel by ID:
```sql
SELECT 
    channel_id, channel_name, channel_owner, channel_accessControl,
    channel_visibility, owner_name
FROM v_channel
WHERE channel_id = ?
```

#### Find All Channels:
```sql
SELECT 
    channel_id, channel_name, channel_owner, channel_accessControl,
    channel_visibility, owner_name
FROM v_channel
WHERE channel_visibility = '${PUBLIC.name}'
LIMIT ?
OFFSET ?
```

#### Delete a Channel:
```sql
DELETE FROM channels
WHERE id = ?
```

#### Join a Channel:
```sql
INSERT INTO channel_members (channel, member, access_control)
VALUES (?, ?, ?)
```

#### Check if a User is a Member of a Channel:
```sql
SELECT member FROM channel_members
WHERE channel = ? AND member = ?
```

#### Find a Channel Invitation:
```sql
SELECT channel_id, expiration_date, invitation, access_control, max_uses
FROM channels_invitations
WHERE channel_id = ?
```

#### Update an Invitation:
```sql
UPDATE channels_invitations
SET max_uses = ?
WHERE channel_id = ?
```

#### Delete a Channel Invitation:
```sql
INSERT INTO channels_invitations (channel_id, expiration_date, invitation, access_control, max_uses)
VALUES (?, ?, ?, ?, ?)
```

#### Find User Access Control:
```sql
SELECT access_control FROM channel_members
WHERE channel = ? AND member = ?
```

#### Save a Channel:
```sql
UPDATE channels
SET owner = ?, name = ?, access_control = ?, visibility = ?
WHERE id = ?
```

### Messages:

#### Create a Message:
```sql
INSERT INTO messages (content, author, channel, timestamp)
VALUES (?, ?, ?, ?) RETURNING id
```

#### Find Messages in a Channel:
```sql
SELECT 
    msgId, msgChannelId, msgContent, msgAuthorId, msgTimestamp,
    msgChannelName, msgAuthorUsername
FROM v_message
WHERE msgChannelId = ?
ORDER BY msgTimestamp DESC
LIMIT ? OFFSET ?
```

#### Find a Message by ID:
```sql
SELECT 
    msgId, msgChannelId, msgContent, msgAuthorId, msgTimestamp,
    msgChannelName, msgAuthorUsername
FROM v_message
WHERE msgid = ?
```

#### Find All Messages:
```sql
SELECT 
    msgId, msgChannelId, msgContent, msgAuthorId, msgTimestamp,
    msgChannelName, msgAuthorUsername
FROM v_message
```

#### Delete a Message:
```sql
DELETE FROM messages
WHERE id = ?
```

#### Save a Message:
```sql
UPDATE messages
SET channel = ?, author = ?, content = ?, timestamp = ?
WHERE id = ?
```
