# Build and Run Instructions

- This directory contains the source code for the JVM implementation of the DAW project.

## Requirements
- To build and run this project, you need to first follow the instructions in the [js](../js/README.md) directory to build and run the frontend.

## Build images
- To build the project images, run the following command:
* `./gradlew buildImageJvm` - builds the JVM image with the Chimp backend HTTP API
* `./gradlew buildImagePostgres` - builds the Postgres image used by the backend
* `./gradlew buildImageNginx` - builds the Nginx image
* `./gradlew buildAllImages` - builds all images


## Start, scale, and stop services
* `./gradlew allUp` - starts all services.
* `./gradlew allDown` - stops all services.
* On the `host` folder, `docker compose up -d --scale chimp-jvm=N` - scales the dynamic JVM service. Replace `N` with the number of instances you want to run.


## Notes
- The JVM service by default is available at `http://localhost:8080`.
- The Chimp API is started with the following users:
  - `Admin` with password `@dmin123Chimp`
  - `User` with password `Us&r123Chimp`