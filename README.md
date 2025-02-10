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

- `make run` will run the previously built Docker image.

- `make test` will run tests in src/test locally- this requires jdk17 installed, as it does not run in Docker.

## Dependencies

This application depends on the RabbitMQ message broker, which can be run as a
container with the accompanying compose file. The Makefile uses the `yq` utility
to parse some basic configuration information.
