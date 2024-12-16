# WebApp Documentation

## Table of Contents
1. [Introduction](#introduction)
2. [Features](#features)
3. [Key Aspects](#key-aspects)

## Introduction
This project was developed for the "Desenvolvimento de Aplicações Web" course.
The main goal of this project is to develop a multi-user Instant Messaging (IM) system that allows users to communicate in real-time through various channels and messages. The system will consist of a backend component and a frontend application, each providing key functionalities.

## Features
- User registration and authentication.
- Channel and message management.
- Public and private channels.
- Message posting and viewing.
- User and channel invitations.

## Key Aspects
- The frontend application is developed using React.
- The application uses the Tailwind library for the user interface.
- The application uses the Fetch API to communicate with the backend.
- The application was developed using typescript.
- The application uses the React Router library for navigation.
- The application uses the React Context API for state management.
- The application uses the WebPack bundler.

## Pages

### Login
The login page allows users to authenticate themselves by providing their credentials. It includes form validation and error handling to ensure a smooth user experience.

### Register
The register page enables new users to create an account by providing necessary information such as username, password AND invitation code. It includes form validation and feedback messages.

### Channel
The channel page allows users to view and post messages within a specific channel. It supports real-time updates to ensure that users can see new messages as they are posted.

### FindChannels
The find channels page provides a search functionality to help users discover new channels. It includes filters and sorting options.

### CreateChannel
The create channel page allows users to create new channels by providing necessary details such as channel name, visibility, and access control. It includes form validation and feedback messages.

### EditChannel
The edit channel page allows users to modify existing channels by updating their details such as visibility, description and icon image.

### CreateUserInvitation
The create user invitation page allows users to invite other users to join the system.

### CreateChannelInvitation
The create channel invitation page allows users to invite other users to join a specific channel.

### About
The about page provides information about the development team. It includes contact information and links to relevant resources.