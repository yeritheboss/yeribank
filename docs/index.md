# YeriBank

YeriBank is a modular digital banking core built to showcase backend engineering practices that matter in fintech-style systems: transactional integrity, secure APIs, architecture boundaries, and event-driven evolution.

This documentation is structured to work both as internal project documentation and as a public GitHub Pages site.

## What YeriBank is today

The current implementation is a **Spring Boot modular monolith** following **Hexagonal Architecture**, with a clear path toward future service extraction.

### Current stack

- Spring Boot
- Hexagonal Architecture
- JWT authentication and role-based access control
- PostgreSQL
- Flyway migrations
- Kafka event publishing
- OpenAPI / Swagger UI

### Implemented domains

- User registration
- Authentication with access token and refresh token rotation
- Account creation and balance lookup
- Transfers with transactional consistency

### Current API surface

- `POST /users`
- `POST /auth/login`
- `POST /auth/refresh`
- `POST /accounts`
- `GET /accounts/{id}/balance`
- `POST /transfers`

### Current quality status

- Integration tests already exist for authentication, accounts, and transfers
- OpenAPI is exposed locally through Swagger UI
- Flyway manages schema evolution for users, accounts, transfers, and refresh tokens

## Documentation map

- [Product Overview](product-overview.md): product vision, scope, and positioning
- [Architecture](architecture.md): current technical architecture and evolution path
- [API Overview](api.md): modules, security model, and endpoints
- [Messaging](messaging.md): Kafka and Zookeeper in the current local architecture
- [Development](development.md): local setup and documentation conventions
- [Cloud Deployment](deployment-cloud.md): current low-cost deployment path for demos
- [Roadmap](roadmap.md): implemented items, in-progress work, and next milestones

## Public documentation principle

YeriBank documentation separates:

- **Current implementation**: features that already exist in the codebase
- **Target vision**: planned capabilities such as fraud analysis, scoring, and richer event consumers

That distinction keeps the project credible as a public engineering portfolio.
