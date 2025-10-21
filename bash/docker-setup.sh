#!/bin/bash

echo "Starting Docker PostgreSQL setup..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Docker is not running. Please start Docker first."
    exit 1
fi

# Stop existing PostgreSQL container if running
if docker ps -q --filter "name=postgres" | grep -q .; then
    echo "Stopping existing PostgreSQL container..."
    docker stop postgres
    docker rm postgres
fi

# Remove existing PostgreSQL container if exists
if docker ps -aq --filter "name=postgres" | grep -q .; then
    echo "Removing existing PostgreSQL container..."
    docker rm postgres
fi

# Pull PostgreSQL image
echo "Pulling PostgreSQL image..."
docker pull postgres:15

# Create and start PostgreSQL container
echo "Creating PostgreSQL container..."
docker run --name postgres \
    -e POSTGRES_DB=mydatabase \
    -e POSTGRES_USER=myuser \
    -e POSTGRES_PASSWORD=mypassword \
    -p 5432:5432 \
    -d postgres:15

# Wait for PostgreSQL to be ready
echo "Waiting for PostgreSQL to be ready..."
sleep 10

# Check if container is running
if docker ps --filter "name=postgres" --filter "status=running" | grep -q postgres; then
    echo "PostgreSQL container is running successfully!"
    echo "Database: mydatabase"
    echo "User: myuser"
    echo "Password: mypassword"
    echo "Port: 5432"
else
    echo "Failed to start PostgreSQL container"
    exit 1
fi

echo "Docker PostgreSQL setup completed successfully!"
