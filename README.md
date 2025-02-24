# dennys

This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:

- [Ktor Documentation](https://ktor.io/docs/home.html)
- [RabbitMQ BASIC Example](https://www.rabbitmq.com/tutorials/tutorial-one-java)

## Configuration

This application depends on 3 environment variables:

`Set in /src/main/resources/riot.yaml`
- `RIOT_API_KEY` : Riot API token

`Set in /src/main/resources/database.yaml`
- `LBLCS_JDBC_URL` : JDBC connection string to the LBLCS database
- `LBLCS_JDBC_PW` : Password for LBLCS database

These local config values are picked up by docker compose via the .env file.

## Building & Running

The `Makefile` contains many useful commands to build/run this application locally, as well as quickly run the tests. 

- `make build` and `make rebuild` will build the docker image from source.

- `make run` will run the previously built Docker image as well as several
dependencies.

- `make run-dev` will run everything as well as a pgadmin instance

Note that the postgres database migration init script relies on sql files in
`build/resources/migrations/`. These migrations are created with the
`GenerateMainDatabaseMigrations` gradle task, easily accessible in the sidebar
Gradle menu in Intellij. THis task MUST be run everytime a sqldelight migration
is added, otherwise the schema changes will not be reflected in the container
db.

- `make test` will run tests in src/test locally- this requires jdk17 installed, as it does not run in Docker.

- `make debug-build` will run a docker build with readable output, useful for
debugging build failures.

- `make stop` will stop each container, then remove them. This command must be
run to actually stop the dependency containers after a ctrl-C to kill Dennys.

- `make drop-db` will delete the pgdata volume. This will nuke the local
database.
