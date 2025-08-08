# Spring Music Collection - Cloud Foundry Edition

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-brightgreen.svg)
![Cloud Foundry](https://img.shields.io/badge/Cloud%20Foundry-blue.svg)
![H2 Database](https://img.shields.io/badge/H2-Database-green.svg)

This repository contains a Spring Boot music collection application optimized for Cloud Foundry deployment. The application provides a web interface for managing music albums with support for multiple database backends.

## Architecture

The application is built with Spring Boot 3.3.2 and supports:
- **Default**: H2 in-memory database (no external dependencies)
- **Multiple database backends**: PostgreSQL, MySQL, H2, Redis, MongoDB
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

# Run locally (uses H2 in-memory database by default)
java -jar target/spring-metal-0.6.jar
```

The application will start with:
- **H2 in-memory database** (default, no external dependencies)
- **H2 Console** available at `http://localhost:8080/h2-console`
- **Web interface** at `http://localhost:8080`
- **REST API** at `http://localhost:8080/albums`

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
- **H2 in-memory database by default** (no service binding required)

### Database Configuration

The application intelligently detects and uses the appropriate database:

#### Default (No Service Binding)
- **H2 in-memory database** - starts immediately without any external dependencies
- Perfect for development and testing
- Data persists for the duration of the application session

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
- **Zero Dependencies**: Runs with H2 in-memory database by default

## API Endpoints

- `GET /albums` - List all albums
- `POST /albums` - Create a new album
- `PUT /albums/{id}` - Update an album
- `DELETE /albums/{id}` - Delete an album
- `GET /appinfo` - Application information and profiles
- `GET /request` - Request information
- `GET /h2-console` - H2 database console (when using H2)

## Configuration

The application uses Spring Boot's auto-configuration and supports:
- **Automatic database detection** via Cloud Foundry service binding
- **H2 in-memory database** as default when no external database is bound
- Profile-based configuration (`h2`, `http2`, `mysql`, `postgresql`)
- Environment-based configuration

## Development Workflow

1. **Start without dependencies**: The app runs with H2 in-memory database by default
2. **Add vector database later**: Bind a vector database service when ready for AI features
3. **Switch databases**: Bind different database services to change the backend
4. **Local development**: Use H2 for quick development and testing

## Contributing
Contributions to this project are welcome. Please ensure to follow the existing coding style and add unit tests for any new or changed functionality.


