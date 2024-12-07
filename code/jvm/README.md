# Build and Run Instructions

- This directory contains the source code for the JVM implementation of the DAW project.

## Requirements
- The following environment variables must be set:
    - `DB_URL`: The URL of the database.
    - `DB_USER`: The username to access the database.
    - `DB_PASSWORD`: The password to access the database.
    - `DB_POOL_SIZE`: The size of the database connection pool.

## Build
- To build the project, run the following command:
```bash
./gradlew build
```

## Run
- To run the project, run the following command:
```bash
./gradlew bootRun
```