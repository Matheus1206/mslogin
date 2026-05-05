# mslogin

A Spring Boot microservice for user authentication and identity management. It exposes a RESTful API for credential validation using Spring Security with stateless session handling and BCrypt password hashing.

## Table of Contents

- [Requirements](#requirements)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Database Setup](#database-setup)
  - [Running with Docker Compose](#running-with-docker-compose)
  - [Running Locally](#running-locally)
  - [Building the Docker Image](#building-the-docker-image)
- [API Reference](#api-reference)
  - [Login](#login)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [Architecture Overview](#architecture-overview)
- [Related Services](#related-services)
- [Contributing](#contributing)
- [License](#license)

## Requirements

- **Java** 17+
- **Maven** 3.8+ (or use the included Maven Wrapper)
- **MySQL** 8.0+
- **Docker** and **Docker Compose** (optional, for containerised setup)

## Tech Stack

| Technology             | Purpose                          |
|------------------------|----------------------------------|
| Spring Boot 3.3        | Application framework            |
| Spring Security        | Authentication & authorisation    |
| Spring Data JPA        | Database access (ORM)            |
| MySQL                  | Relational database              |
| Lombok                 | Boilerplate reduction            |
| Bean Validation        | Request payload validation       |
| Docker                 | Containerisation                 |
| Maven Wrapper          | Reproducible builds              |

## Project Structure

```
mslogin/
├── src/
│   ├── main/
│   │   ├── java/br/com/fiap/mslogin/
│   │   │   ├── config/          # Security configuration (filter chain, BCrypt, AuthenticationManager)
│   │   │   ├── controller/      # REST endpoints (/api/v1/login)
│   │   │   ├── model/           # JPA entities (Usuario) and DTOs (UsuarioRequest)
│   │   │   ├── repository/      # Spring Data JPA interfaces
│   │   │   ├── service/         # Business logic (UserDetailsService implementation)
│   │   │   └── MsloginApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/br/com/fiap/mslogin/
│           └── MsloginApplicationTests.java
├── Dockerfile           # Multi-stage build (Maven + OpenJDK 17)
├── compose.yaml         # Docker Compose with MySQL service
├── pom.xml              # Maven dependencies and build config
└── mvnw / mvnw.cmd      # Maven Wrapper scripts
```

## Getting Started

### Database Setup

The application requires a MySQL instance with a database named `mslogindb`. You can either use Docker Compose (recommended) or configure a local MySQL server.

**Create the database manually** (if not using Docker Compose):

```sql
CREATE DATABASE mslogindb;
```

### Running with Docker Compose

The `compose.yaml` file defines a MySQL service pre-configured for the application.

> **Note:** The `compose.yaml` contents are currently commented out. Uncomment them before running.

```bash
docker compose up -d
```

This starts a MySQL container on port **3307** (mapped to internal port 3306) with the `mslogindb` database.

### Running Locally

1. **Ensure MySQL is running** and the `mslogindb` database exists.

2. **Update connection settings** in `src/main/resources/application.properties` if your MySQL instance differs from the defaults:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/mslogindb
   spring.datasource.username=root
   spring.datasource.password=1234
   ```

3. **Build and run** with the Maven Wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```

   The application starts on **http://localhost:8080** by default.

### Building the Docker Image

> **Note:** The `Dockerfile` contents are currently commented out. Uncomment them before building.

```bash
docker build -t mslogin .
docker run -p 8080:8080 mslogin
```

## API Reference

### Login

Authenticates a user against the database using Spring Security's `AuthenticationManager`.

```
POST /api/v1/login
Content-Type: application/json
```

**Request body:**

```json
{
  "login": "username",
  "senha": "password"
}
```

**Responses:**

| Status | Description                        |
|--------|------------------------------------|
| `200`  | Authentication successful          |
| `401`  | Invalid credentials                |
| `400`  | Validation error (missing fields)  |

**Example (cURL):**

```bash
curl -X POST http://localhost:8080/api/v1/login \
  -H "Content-Type: application/json" \
  -d '{"login": "user@example.com", "senha": "s3cur3P@ss"}'
```

## Configuration

Key application properties in `src/main/resources/application.properties`:

| Property                               | Default Value                                 | Description                    |
|----------------------------------------|-----------------------------------------------|--------------------------------|
| `spring.application.name`              | `mslogin`                                     | Application name               |
| `spring.datasource.url`               | `jdbc:mysql://localhost:3306/mslogindb`       | MySQL connection URL           |
| `spring.datasource.username`           | `root`                                        | Database username              |
| `spring.datasource.password`           | `1234`                                        | Database password              |
| `spring.jpa.hibernate.ddl-auto`        | `update`                                      | Schema auto-update strategy    |
| `spring.jpa.show-sql`                  | `true`                                        | Log SQL statements             |
| `spring.jpa.database-platform`         | `org.hibernate.dialect.MySQLDialect`          | Hibernate dialect              |

> **Important:** Override `spring.datasource.password` with a secure value in production via environment variables or a secrets manager.

## Running Tests

```bash
./mvnw test
```

## Architecture Overview

```
┌──────────┐       ┌──────────────────┐       ┌───────────────────┐
│  Client  │──────▶│  UsuarioController│──────▶│  AuthenticationMgr│
└──────────┘       │  POST /api/v1/   │       │  (Spring Security)│
                   │     login        │       └────────┬──────────┘
                   └──────────────────┘                │
                                                       ▼
                                              ┌────────────────┐
                                              │ UsuarioService  │
                                              │ (UserDetails    │
                                              │  Service)       │
                                              └───────┬────────┘
                                                      │
                                                      ▼
                                              ┌────────────────┐
                                              │UsuarioRepository│──▶ MySQL
                                              │ (JPA)          │    (mslogindb)
                                              └────────────────┘
```

- **SecurityConfig** — Disables CSRF, enforces stateless sessions, configures BCrypt password encoding.
- **UsuarioController** — Receives login requests and delegates to Spring Security's `AuthenticationManager`.
- **UsuarioService** — Implements `UserDetailsService`; loads user records by login name.
- **Usuario** — JPA entity implementing `UserDetails`; every user is granted `ROLE_USER`.
- **UsuarioRepository** — Spring Data JPA interface with a custom `findByLogin` query method.

## Related Services

This microservice is part of a larger system architecture alongside:

- **mspedidos** — Order management microservice
- **msitens** — Item and inventory management microservice
- **mscatalogo** — Product catalog microservice

## Contributing

Contributions are welcome. Please open an issue to discuss proposed changes before submitting a pull request.

## License

This project does not currently specify a license. Contact the maintainers for usage terms.

---

_Originally written and maintained by contributors and [Devin](https://app.devin.ai), with updates from the core team._
