# Data Access


## The SQL statements used are:

### On users:
---
- to create a user:
```sql
INSERT INTO users (name, password)
VALUES (?, ?) RETURNING id
```
---
- to find a user by id:
```sql
SELECT id, name, password
FROM users
WHERE id = ?
```
---
- to find a user by username:
```sql
SELECT id, name, password
FROM users
WHERE name = ?
```
---
-to delete a user:
```sql
DELETE FROM users_invitations WHERE user_id = ?
```
```sql
DELETE FROM users_tokens WHERE user_id = ? = ?
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
---
to create a user invitation:
```sql
INSERT INTO users_invitations (user_id, invitation, expiration_date)
VALUES (?, ?, ?)
```
---
- to find a user invitation:
```sql
SELECT user_id, invitation, expiration_date
FROM users_invitations
WHERE user_id = ? AND invitation = ?
```
---
- to delete a user invitation:
```sql
DELETE FROM users_invitations
WHERE user_id = ? AND invitation = ?
```
---
- to create a user token:
```sql
INSERT INTO users_tokens (user_id, token, creation, expiration)
VALUES (?, ?, ?, ?)
```
---
- to validate a token:
```sql
SELECT user_id
FROM users_tokens
WHERE token = ?
```	
---
-find a user by token:
```sql
SELECT user_id
FROM users_tokens
WHERE token = ?
```
---
- to delete a user token:
```sql
DELETE FROM users_tokens
WHERE token = ?
```
---
- count the number of user tokens:
```sql
SELECT COUNT(*) FROM users_tokens WHERE user_id = ?
```
---
- delete old user token:
```sql
DELETE FROM users_tokens
WHERE token IN (
    SELECT token
    FROM users_tokens
    WHERE user_id = ?
    ORDER BY creation 
        FETCH 
        FIRST 1 ROWS ONLY
)
```
---
- find all users:
```sql
SELECT id, name, password
FROM users
OFFSET ?
LIMIT ?
```
---
- save a user:
```sql
UPDATE users
SET name = ?, password = ?
WHERE id = ?
```

### On channels:
---
- to create a channel:
```sql
INSERT INTO channels (owner, name, access_control, visibility)
VALUES (?, ?, ?, ?) RETURNING id
```
---
- to find a channel by user id:
```sql
SELECT 
    channel_id, channel_name, channel_owner, channel_accessControl,
    channel_visibility, owner_name
FROM v_channel
WHERE channel_owner = ?
LIMIT ?
OFFSET ?
```
---
- to find a channel by id:
```sql
SELECT 
    channel_id, channel_name, channel_owner, channel_accessControl,
    channel_visibility, owner_name
FROM v_channel
WHERE channel_id = ?
```
---
- find all channels:
```sql
SELECT 
    channel_id, channel_name, channel_owner, channel_accessControl,
    channel_visibility, owner_name
FROM v_channel
WHERE channel_visibility = '${PUBLIC.name}'
LIMIT ?
OFFSET ?
```
---
- to delete a channel:
```sql
DELETE FROM channels
WHERE id = ?
```
---
- to join a channel:
```sql
INSERT INTO channel_members (channel, member, access_control)
VALUES (?, ?, ?)
```
---
- to check if a user is a member of a channel:
```sql
SELECT member from channel_members
WHERE channel = ? AND member = ?
```
---
- find a channel invitation:
```sql
SELECT channel_id, expiration_date, invitation, access_control, max_uses
FROM channels_invitations
WHERE channel_id = ?
```	
---
- to update an invitation:
```sql
UPDATE channels_invitations
SET max_uses = ?
WHERE channel_id = ?
```
---
- to delete a channel invitation:
```sql
INSERT INTO channels_invitations (channel_id, expiration_date, invitation, access_control, max_uses)
VALUES (?, ?, ?, ?, ?)
```
---
- to find user access control:
```sql
SELECT access_control from channel_members
WHERE channel = ? AND member = ?
```
---
-save a channel:
```sql
UPDATE channels
SET owner = ?, name = ?, access_control = ?, visibility = ?
WHERE id = ?
```

### On messages:
---
- to create a message:
```sql
INSERT INTO messages (content, author, channel, timestamp)
VALUES (?, ?, ?, ?) RETURNING id
```
---
- find messages in a channel:
```sql
SELECT 
    msgId, msgChannelId, msgContent, msgAuthorId, msgTimestamp,
    msgChannelName, msgAuthorUsername
FROM v_message
WHERE msgChannelId = ?
ORDER BY msgTimestamp DESC
LIMIT ? OFFSET ?
```
---
- find a message by id:
```sql
SELECT 
    msgId, msgChannelId, msgContent, msgAuthorId, msgTimestamp,
    msgChannelName, msgAuthorUsername
FROM v_message
WHERE msgid = ?
```
---
- find all messages:
```sql
SELECT 
    msgId, msgChannelId, msgContent, msgauthorid, msgTimestamp,
    msgChannelName, msgAuthorUsername
FROM v_message
```	
---
- to delete a message:
```sql
DELETE FROM messages
WHERE id = ?
```
---
- save a message:
```sql
UPDATE messages
SET channel = ?, author = ?, content = ?, timestamp = ?
WHERE id = ?
```