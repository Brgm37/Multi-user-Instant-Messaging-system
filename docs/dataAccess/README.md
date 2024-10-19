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
