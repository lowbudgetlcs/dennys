# Config
CONFIG_ROOT = ./src/main/resources/
APP_NAME = dennys
TAG = dev
CONTAINER_NAME = $(APP_NAME)-container
PORT = $(shell yq '.ktor.deployment.port' $(CONFIG_ROOT)/application.yaml)

.PHONY: build run run-dev stop clean erase rebuild ps psa test all build-debug 

# Build the Docker image
build:
	docker build -t $(APP_NAME):$(TAG) -f Dockerfile .

# Build without cacheing and readable output
build-debug:
	docker build --no-cache --progress-plain -t $(APP_NAME):$(TAG) -f Dockerfile .

# Run the Docker container
run:
	docker-compose up dennys

# Run all dev containers
run-dev:
	docker-compose up

# Refresh
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

# Run tests (not containerized)
test:
	./gradlew test
