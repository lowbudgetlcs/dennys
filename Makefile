# Config
APP_NAME = dennys
TAG = dev
CONTAINER_NAME = $(APP_NAME)-container

.PHONY: all erase clean stop build debug-build run dev migrations rebuild refresh drop test

# Refresh containers and app image
all: erase build run

# Stop and Clean docker files
erase: stop clean

# Clean up the Docker image
clean:
	docker rmi $(APP_NAME):$(TAG) || true

# Stop the Docker container
stop:
	docker stop $(CONTAINER_NAME) postgres-dennys pgadmin-dennys || true
	docker rm $(CONTAINER_NAME) postgres-dennys pgadmin-dennys || true

# Build the Docker image
build:
	docker build -t $(APP_NAME):$(TAG) -f Dockerfile .

# Build without cacheing and readable output
debug-build:
	docker build --progress=plain -t $(APP_NAME):$(TAG) -f Dockerfile .

# Run the Docker container
run:
	docker compose up dennys postgres

# Run all dev containers
dev: migrations
	docker compose up

migrations:
	./gradlew generateMainDatabaseMigrations generateMainDatabaseInterface 

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
