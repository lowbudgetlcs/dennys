# Config
CONFIG_ROOT = ./src/main/resources/
APP_NAME = dennys
PORT = $(shell yq '.ktor.deployment.port' $(CONFIG_ROOT)/application.yaml)
RIOT_API_KEY = $(shell yq '.apiKey' $(CONFIG_ROOT)/riot.local.yaml)
LBLCS_DB_URL = $(shell yq '.lblcs.url' $(CONFIG_ROOT)/database.local.yaml)
LBLCS_DB_PW = $(shell yq '.lblcs.pass' $(CONFIG_ROOT)/database.local.yaml)

.PHONY: build run stop clean erase rebuild ps psa test deploy

# Build the Docker image
build:
	DOCKER_BUILDKIT=1 docker build -t $(APP_NAME) -f Dockerfile .

# Run the Docker container
run:
	docker run \
	-e LBLCS_DB_URL=$(LBLCS_DB_URL) \
	-e LBLCS_DB_PW=$(LBLCS_DB_PW) \
	-e RIOT_API_KEY=$(RIOT_API_KEY) \
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
