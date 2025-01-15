# dennys

This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:

- [Ktor Documentation](https://ktor.io/docs/home.html)

## Configuration

This application depends on 3 environment variables:

- `RIOT_API_KEY` : Riot API token
- `LBLCS_DB_URL` : JDBC connection string to the LBLCS database
- `LBLCSL_DB_PW` : Password for LBLCS database

## Building & Running

The `Makefile` contains many useful commands to build/run this application locally, as well as quickly run the tests. 

- `make build` and `make rebuild` will build the docker image from source.

- `make run` will run the previously built Docker image.

- `make test` will run tests in src/test locally- this requires jdk17 installed, as it does not run in Docker.
