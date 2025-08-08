# Spring Music Collection - Cloud Foundry Edition

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-brightgreen.svg)
![Cloud Foundry](https://img.shields.io/badge/Cloud%20Foundry-blue.svg)
![PostgreSQL](https://img.shields.io/badge/postgres-15.1-red.svg)

This repository contains a Spring Boot music collection application optimized for Cloud Foundry deployment. The application provides a web interface for managing music albums with support for multiple database backends.

## Architecture

The application is built with Spring Boot 3.3.2 and supports:
- Multiple database backends (PostgreSQL, MySQL, H2, Redis, MongoDB)
- Cloud Foundry service binding
- RESTful API for album management
- Modern web interface with AngularJS and Bootstrap

## Prerequisites
- Cloud Foundry CLI installed
- Access to a Cloud Foundry environment
- Java 17 or later
- Maven 3.6+

## Running the Application

### Local Development

```bash
# Build the application
./mvnw clean package

# Run locally
java -jar target/spring-metal-0.6.jar
```

### Cloud Foundry Deployment

```bash
# Login to Cloud Foundry
cf login -u YOUR_USERNAME -p YOUR_PASSWORD
cf target -o YOUR_ORG -s YOUR_SPACE

# Build the application
./mvnw clean package

# Deploy to Cloud Foundry
cf push
```

The application will be deployed with:
- 1GB memory allocation
- Java 17 runtime
- HTTP/2 enabled
- Random route assignment

### Database Configuration

The application supports multiple database backends:

#### PostgreSQL
```bash
# Create PostgreSQL service
cf create-service postgresql-db development postgres-db

# Bind to application
cf bind-service spring-metal postgres-db
```

#### MySQL
```bash
# Create MySQL service
cf create-service mysql-db development mysql-db

# Bind to application
cf bind-service spring-metal mysql-db
```

#### Redis
```bash
# Create Redis service
cf create-service redis-db development redis-db

# Bind to application
cf bind-service spring-metal redis-db
```

## Application Features

- **Album Management**: Add, edit, delete, and view music albums
- **Search and Sort**: Sort albums by title, artist, year, genre, review, or score
- **Multiple Views**: Grid and list view options
- **Responsive Design**: Works on desktop and mobile devices
- **Cloud Native**: Designed for Cloud Foundry deployment with service binding

## API Endpoints

- `GET /albums` - List all albums
- `POST /albums` - Create a new album
- `PUT /albums/{id}` - Update an album
- `DELETE /albums/{id}` - Delete an album
- `GET /appinfo` - Application information and profiles
- `GET /request` - Request information

## Configuration

The application uses Spring Boot's auto-configuration and supports:
- Database auto-detection via Cloud Foundry service binding
- Profile-based configuration (`http2`, `mysql`, `postgresql`)
- Environment-based configuration

## Contributing
Contributions to this project are welcome. Please ensure to follow the existing coding style and add unit tests for any new or changed functionality.


