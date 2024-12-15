# Build and Run Instructions

- This directory contains the source code for the JavaScript implementation of the DAW project.

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
  - `admin` with password `@dmin123Chimp`

