# Config
CONFIG_ROOT = ./src/main/resources/
APP_NAME = dennys
TAG = dev
CONTAINER_NAME = $(APP_NAME)-container
PORT = $(shell yq '.ktor.deployment.port' $(CONFIG_ROOT)/application.yaml)

.PHONY: build run dev stop clean erase rebuild test all debug-build drop migrations refresh

migrations:
	./gradlew generateMainDatabaseMigrations generateMainDatabaseInterface 

# Build the Docker image
build:
	docker build -t $(APP_NAME):$(TAG) -f Dockerfile .

# Build without cacheing and readable output
debug-build:
	docker build --no-cache --progress-plain -t $(APP_NAME):$(TAG) -f Dockerfile .

# Run the Docker container
run:
	docker compose up dennys rabbitmq postgres

# Run all dev containers
dev:
	docker compose up

# Refresh containers and app image
all: erase build run

# Stop the Docker container
stop:
	docker stop $(CONTAINER_NAME) postgres-dennys rabbitmq-dennys pgadmin-dennys || true
	docker rm $(CONTAINER_NAME) postgres-dennys rabbitmq-dennys pgadmin-dennys || true

# Clean up the Docker image
clean:
	docker rmi $(APP_NAME):$(TAG) || true

# Stop and Clean docker files
erase: stop clean

# Rebuild the Docker image
rebuild: erase build

# A full refresh. WARNING: Deletes all data stored in the postgres data volume
refresh: erase drop build run

# Cleans local database
drop:
	docker volume rm $(APP_NAME)_pgdata

# Run tests (not containerized)
test:
	./gradlew test
