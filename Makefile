# Config
APP_NAME = dennys
TAG = local
CONTAINER_NAME = $(APP_NAME)-container

.PHONY: all erase clean stop build debug-build run dev migrations rebuild refresh drop test

# Refresh containers and app image
all: clean build run

# Clean up the Docker image
clean: stop
	docker rmi $(APP_NAME):$(TAG) || true

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
run:
	docker compose up --attach dennys

migrations:
	./gradlew generateMainDatabaseMigrations 

# A full refresh. WARNING: Deletes all data stored in the postgres data volume
refresh: clean drop build run

# Cleans local database
drop:
	docker volume rm $(APP_NAME)_pgdata
	docker volume rm $(APP_NAME)_pgadmin-dat

# Run tests
test:
	docker build -t dennys:test --progress=plain --no-cache --target test .
