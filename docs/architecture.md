# Architecture

## Architectural Style

YeriBank is currently implemented as a **modular monolith** built with **Hexagonal Architecture**.

This keeps the first version easier to evolve while preserving clear seams for future extraction into services.

## Current Structure

The backend is organized into four visible layers:

- `domain`
- `application`
- `infrastructure`
- `config`

At a high level:

```text
Clients -> REST Controllers -> Application Use Cases -> Domain Model
                                  |
                                  -> Output Ports -> Persistence / Security / Kafka
```

## Hexagonal Layers

### Domain

The domain layer contains the business entities and rules that should remain independent from frameworks.

Current model includes:

- `User`
- `Account`
- `Transfer`
- `RefreshToken`
- `RiskProfile`
- `TransferRiskAssessment`
- `FraudAlert`
- `FinancialProfile`
- `LoanApplication`
- `AuditLog`
- Roles, statuses, risk, fraud, and loan enums under `domain.model.enums`

### Application

The application layer coordinates use cases and declares ports.

Current use cases include:

- User creation
- Login
- Refresh token rotation
- Account creation
- Account listing
- Balance retrieval
- Transfer execution
- Account movement history
- Dashboard summary retrieval
- Fraud alert querying
- Loan application querying
- Audit log querying

Current outbound ports cover:

- User persistence
- Account persistence
- Transfer persistence
- Refresh token persistence
- Audit log persistence
- Token generation
- Transfer event publication

### Infrastructure

The infrastructure layer provides adapters for the application ports.

Current adapters include:

- REST controllers
- Spring Data JPA persistence adapters
- JWT security services
- Kafka event publisher
- OpenAPI / Swagger configuration
- API request logging filter
- Global exception handling

## Security Architecture

Security is based on:

- JWT access tokens
- Refresh token rotation
- Role-based authorization with `ADMIN` and `USER`

Authorization is enforced through the web layer and through caller context passed into application use cases.

## Data and Consistency

YeriBank uses PostgreSQL as the transactional store and Flyway for schema evolution.

Transfers run inside a transactional boundary, and the codebase models account versioning to support optimistic locking strategies in concurrent financial updates.

## Event-Driven Design

The current implementation publishes:

- `TransferCreatedEvent`

This event is sent to Kafka after a successful transfer, providing a clean integration point for future downstream consumers.

## Current vs Planned Capabilities

### Implemented now

- Hexagonal backend structure
- REST adapters
- PostgreSQL persistence
- Kafka producer for transfer events
- JWT and refresh-token flow
- Global API error handling
- Persistent audit logging for core actions
- Paginated list endpoints for API-facing collections
- Risk, fraud, and loan domain foundations

### Planned next

- Fraud analysis consumers
- Scoring workflows
- Notification consumers
- Richer observability and audit querying

## Evolution Path

The intended long-term decomposition is by bounded context:

- Identity
- Accounts
- Transfers
- Fraud
- Scoring
- Notifications

If that transition happens, the current modular monolith already provides the architectural seams needed to extract services incrementally rather than redesigning the system from scratch.
