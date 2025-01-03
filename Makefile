# Config
CONFIG_FILE = ./src/main/resources/application.yaml
APP_NAME = $(shell yq '.app.name' $(CONFIG_FILE))
DOCKERFILE = $(shell yq '.app.build.dockerfile' $(CONFIG_FILE))
PORT = $(shell yq '.ktor.deployment.port' $(CONFIG_FILE))

.PHONY: build run stop clean erase rebuild ps psa test
# Build the Docker image
build:
	docker build -t $(APP_NAME) -f $(DOCKERFILE) .

# Run the Docker container
run:
	docker run -p $(PORT):$(PORT) --name $(APP_NAME)-container $(APP_NAME)

# Stop the Docker container
stop:
	docker stop $(APP_NAME)-container || true
	docker rm $(APP_NAME)-container || true

# Clean up the Docker image
clean:
	docker rmi $(APP_NAME) || true

# Stop and Clean docker files
erase: stop clean

# Rebuild the Docker image
rebuild: stop clean build

# List running containers
ps:
	docker ps

# List all containers
psa:
	docker ps -a

test:
	./gradlew test
