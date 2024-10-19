# 2425-LEIC52D-G04
# Data Modeling

## Introduction

This project focuses on an instant messaging system where users can communicate with each other through channels and messages. The primary entities of the system include:

- User
- Channel
- Message

Detailed [UML diagrams](./Conceptual_Module/README.md) represent the main entities of the system.

## Key Aspects

- Domain classes are implemented as Data classes.
- Domain classes are immutable.
- Domain classes have constructors that receive all properties.
- Apart from domain classes and controllers, all other classes implement interfaces.
- Interfaces define the necessary methods for each application module, establishing a contract for the implementing classes.

## Conceptual Model Restrictions

- Each username is unique.
- Each channel has a unique identifier (**cId**).
- Each message has a unique identifier (**msgId**).
- Each user has a unique identifier (**uId**).

# Physical Model

The [physical model](../code/jvm/repository_jdbc/src/sql/create.sql) is depicted in the following diagram:

<img src="../code/jvm/repository_jdbc/src/sql/ChIMP-scheme.svg">

## Key Aspects

- The database comprises the following tables:
    - **users**: Represents the users of the system.
    - **channels**: Represents the channels of the system.
    - **messages**: Represents the messages of the system.
    - **channel_members**: Represents the members of the channels.
    - **channel_invitations**: Represents the invitations to the channels.
    - **users_invitations**: Represents the invitations to the users.
    - **users_tokens**: Represents the tokens of the users.
- The tables contain the following columns:
    - **users**:
        - **id**: Unique identifier of the user.
        - **username**: Unique username of the user.
        - **password**: Password of the user.
    - **channels**:
        - **id**: Unique identifier of the channel.
        - **name**: Name of the channel.
        - **visibility**: Visibility status of the channel.
        - **owner**: Unique identifier of the user who owns the channel.
        - **accessControl**: Access control settings of the channel.
    - **messages**:
        - **id**: Unique identifier of the message.
        - **channel**: Unique identifier of the channel.
        - **author**: Unique identifier of the user who authored the message.
        - **content**: Content of the message.
        - **timestamp**: Creation date of the message.
    - **channel_members**:
        - **id**: Unique identifier of the channel member.
        - **channel**: Unique identifier of the channel.
        - **user**: Unique identifier of the user.
        - **accessControl**: Access control settings of the user in the channel.
    - **channel_invitations**:
        - **channel_id**: Unique identifier of the channel.
        - **invitation**: Token of the invitation.
        - **expiration_date**: Expiration date of the invitation.
        - **access_control**: Access control settings of the invitation.
        - **max_uses**: Maximum number of uses for the invitation.
    - **users_invitations**:
        - **user_id**: Unique identifier of the user.
        - **invitation**: Token of the invitation.
        - **expiration_date**: Expiration date of the invitation.
    - **users_tokens**:
        - **user_id**: Unique identifier of the user.
        - **token**: Unique token of the user.
        - **creation**: Creation date of the token.
        - **expiration**: Expiration date of the token.

## Data Access

Data access is facilitated through the repository_jdbc module, with the relevant [queries](./dataAccess/README.md).

# OpenAPI Specification

The OpenAPI Specification is available [here](http://localhost:8080/swagger-ui/index.html#/).