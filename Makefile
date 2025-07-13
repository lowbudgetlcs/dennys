# Config
APP_NAME = dennys
TAG = local
CONTAINER_NAME = $(APP_NAME)-container

.PHONY: all clean stop build debug-build run db swagger refresh jooq test

# Build and run by default
all: build run

# Clean up the Docker image
clean: stop
	docker rmi $(APP_NAME):$(TAG) || true
	docker rmi $(APP_NAME):test || true

# Stop the Docker container
stop:
	docker compose down

# Build the application
build:
	docker build -t $(APP_NAME):$(TAG) --target app -f ./docker/Dockerfile .

# Build without caching and readable output
debug-build:
	docker build --no-cache --progress=plain -t $(APP_NAME):$(TAG) -f ./docker/Dockerfile . 

# Run all containers
run: 
	docker compose up --attach dennys

# Start database tools
db:
	docker compose up db pgadmin

swag:
	docker pull docker.swagger.io/swaggerapi/swagger-editor
	docker run -d --name swagger -p 80:8080 -e SWAGGER_FILE=docs/openapi.yaml docker.swagger.io/swaggerapi/swagger-editor

kswag:
	docker stop swagger

# A full refresh. WARNING: Deletes all data stored in the postgres data volume
refresh: clean drop build run

# Generate new JOOQ data classes from sql migrations
jooq:
	./gradlew generateJooq 

# Run tests
test:
	docker build -t dennys:test --progress=plain --no-cache --target test -f ./docker/Dockerfile .
