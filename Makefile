# Config
APP_NAME = dennys
TAG = local
CONTAINER_NAME = $(APP_NAME)-container

.PHONY: all clean stop build debug-build run db swagger refresh jooq test dev

# Build and run by default
all: build run

dev: build
	docker compose up --attach dennys

# Clean up the Docker image
clean: stop
	docker rmi $(APP_NAME):$(TAG) || true

# Stop the Docker container
stop:
	docker compose down

# Build the application
build:
	docker build -t $(APP_NAME):$(TAG) --target localapp -f ./docker/Dockerfile .

# Build without caching and readable output
debug-build:
	docker build --no-cache --progress=plain -t $(APP_NAME):$(TAG) --target localapp -f ./docker/Dockerfile .

# Run all containers
run: 
	docker compose up dennys db --attach dennys

# Start database tools
db:
	docker compose up db 

swag:
	docker compose up swagger-editor

# A full refresh. WARNING: Deletes all data stored in the postgres data volume
refresh: clean drop build run

# Generate new JOOQ data classes from sql migrations
jooq:
	./gradlew generateJooq 

# Run tests
test:
	./gradlew test itest
