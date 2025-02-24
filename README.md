# dennys

This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:


## Configuration

This application depends on 3 environment variables:

`Set in /src/main/resources/riot.yaml`
- `RIOT_API_KEY` : Riot API token

`Set in /src/main/resources/database.yaml`
- `LBLCS_JDBC_URL` : JDBC connection string to the LBLCS database
- `LBLCS_JDBC_PW` : Password for LBLCS database

These local config values are picked up by docker compose via the .env file.

## Building & Running

Before the application can run, the postgres database migration init script 
relies on sql files in `build/resources/migrations/`. These migrations are 
created with the `GenerateMainDatabaseMigrations` gradle task, easily accessible 
in the Gradle menu in Intellij. This task MUST be run everytime a sqldelight 
query or migration is changed.

When a code change is made, deleting the container is not enough. You must also
rebuild the docker image, which takes about a minute.

The `Makefile` contains many useful commands to build/run this application locally, as well as quickly run the tests. 

- `make build` and `make rebuild` will build the docker image from source.

- `make run` will run the previously built Docker image as well as several
dependencies.

- `make run-dev` will run everything as well as a pgadmin instance

- `make test` will run tests in src/test locally- this requires jdk17 installed, as it does not run in Docker.

- `make debug-build` will run a docker build with readable output, useful for
debugging build failures.

- `make stop` will stop each container, then remove them. This command must be
run to actually stop the dependency containers after a ctrl-C to kill Dennys.

- `make drop-db` will delete the pgdata volume. This will nuke the local
database.
