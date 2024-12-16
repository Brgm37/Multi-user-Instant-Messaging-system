# Build and Run Instructions

- This directory contains the source code for the JavaScript implementation of the DAW project.

## Requirements
- file **envConfig.json** in the root directory with the following content:
```json
{
  "expiration_date": 1,
  "session": "session",
  "saltRounds": 4,
  "channels_limit": 100,
  "messages_limit": 150,
  "default_messages_limit": 50,
  "default_limit": 13,
  "default_offset": 0,
  "public_channels_limit": 100
}
```

## Build
- To build the project, run the following command:
* `npm install` - installs the project dependencies.
* `npm run build` - builds the project.

## Run
- To run the project, follow the steps described in the [jvm](../jvm/README.md) directory.

## Notes
- The JS service by default is available at `http://localhost:8000`.
- The JS service is a static server that serves the frontend application.
- The JS service is configured to proxy requests to the JVM service at `http://localhost:8080`.
- The JS service is started with the following users:
  - `Admin` with password `@dmin123Chimp`
  - `User` with password `Us&r123Chimp`

