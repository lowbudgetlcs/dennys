# Config
CONFIG_FILE = ./src/main/resources/application.yaml
APP_NAME = dennys
PORT = $(shell yq '.ktor.deployment.port' $(CONFIG_FILE))

.PHONY: build run stop clean erase rebuild ps psa test deploy

# Build the Docker image
build:
	docker build -t $(APP_NAME) -f Dockerfile .

# Run the Docker container
run:
	docker run \
	-p $(PORT):$(PORT) \
	--network=rabbitmq.docker \
	--name $(APP_NAME)-container $(APP_NAME)

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
rebuild: erase build

# Deploy with compose
deploy:
	docker compose up

# List running containers
ps:
	docker ps

# List all containers
psa:
	docker ps -a

test:
	./gradlew test
