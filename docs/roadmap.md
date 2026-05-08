# Roadmap

## Current State

The project already includes:

- Modular Spring Boot backend
- Hexagonal architecture boundaries
- JWT authentication with refresh token rotation
- Role-based access control
- PostgreSQL persistence
- Flyway migrations
- Kafka transfer event publication
- OpenAPI / Swagger UI
- UI-oriented account list and dashboard endpoints
- Account transfer history by account number
- Persistent audit log with admin query endpoint
- Paginated list endpoints for users, accounts, account movements, audit logs, fraud alerts, and loan applications
- Integration tests for core banking flows

## In Progress

- Broader integration-test coverage and hardening
- Documentation alignment between README, architecture notes, and public docs
- Request and response examples for the main Swagger flows

## Next Milestones

### Near Term

- Expand API documentation with request and response examples
- Add more detailed sequence diagrams for login, account creation, and transfers
- Improve error catalog and operational documentation
- Expand audit coverage to fraud, loans, and profile updates
- Move high-volume list pagination from in-memory slicing to repository-level database pagination

### Mid Term

- Internal consumers for transfer-related events
- Fraud analysis workflow
- Scoring and behavioral modeling
- Richer observability and tracing

### Long Term

- Progressive extraction by bounded context
- API gateway introduction
- Saga-style coordination for distributed transfer workflows
- Optional cloud-native deployment model
