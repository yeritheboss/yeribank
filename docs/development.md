# Development

## Local Run

The project includes a Docker Compose setup for local development.

```bash
docker compose up --build
```

Default local services:

- Application: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- PostgreSQL: `localhost:5432`
- Kafka: `localhost:9092`

## Backend Stack

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- Spring for Apache Kafka
- springdoc OpenAPI

## Current Data and Messaging Concerns

### Database

PostgreSQL is the primary transactional store.

Flyway migrations currently cover:

- Base schema initialization
- Refresh token persistence

### Messaging

Kafka is currently used for transfer event publication.

The initial topic is:

- `transfer-created`

For the local Docker-based setup, Kafka runs together with Zookeeper.

See [Messaging Infrastructure](messaging.md) for the full runtime and configuration overview.

## Testing Status

Integration test coverage is already present for:

- Authentication
- Accounts
- Transfers

Additional domain and application-layer tests can be added over time to improve confidence and make architecture rules more explicit.

## Documentation Conventions

To keep the documentation aligned with the real project state:

- Document implemented features as current behavior
- Document aspirational features as planned or target-state behavior
- Prefer controller, configuration, migration, and test sources as the basis for truth
- Update roadmap and API docs when new modules or endpoints are merged
