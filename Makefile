# Config
APP_NAME = dennys
TAG = local
CONTAINER_NAME = $(APP_NAME)-container

.PHONY: all clean stop build debug-build run migrations refresh drop test

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
	docker build -t $(APP_NAME):$(TAG) --target app .

# Build without caching and readable output
debug-build:
	docker build --no-cache --progress=plain -t $(APP_NAME):$(TAG) .

# Run all containers
run: migrations
	docker compose up --attach dennys

migrations:
	./gradlew generateMainDatabaseMigrations 
	cp ./build/resources/migrations/* ./docker/postgres

# A full refresh. WARNING: Deletes all data stored in the postgres data volume
refresh: clean drop build run

# Cleans local database
drop:
	docker volume rm $(APP_NAME)_pgdata
	docker volume rm $(APP_NAME)_pgadmin-data

# Run tests
test:
	docker build -t dennys:test --progress=plain --no-cache --target test .
