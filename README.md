# dennys

This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:

- [Ktor Documentation](https://ktor.io/docs/home.html)

## Configuration

There are currently 3 configuration files in use:
    `application.yaml` - This configures the Ktor API as well as stores some information used for the Makefile. This is
    checked into source control.
    `rabbitmq.yaml` - This configures the RabbitMQ client connections. This is stored in source control.
    `database.local.yaml` & `database.yaml` - These files store the database secrets. They cascade, with database.yaml.local
    taking precedence. `database.local.yaml` is not checked into source control, so it can be used locally for development.

## Building & Running

The `Makefile` contains many useful commands to build/run this application locally, as well as quickly run the tests. 

`make build` and `make rebuild` will build the docker image from source.

`make run` will run the previously built Docker image.

`make test` will run tests in src/test locally- this requires jdk17 installed, as it does not run in Docker.