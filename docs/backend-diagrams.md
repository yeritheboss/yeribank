# Backend Diagrams

This page explains the current YeriBank backend visually and shows the intended evolution path.

## Current Backend Map

```mermaid
flowchart TB
  classDef edge fill:#e8f1ff,stroke:#2f6fed,stroke-width:1.5px,color:#0f172a
  classDef app fill:#fff4e8,stroke:#dd6b20,stroke-width:1.5px,color:#0f172a
  classDef infra fill:#edfdf4,stroke:#2f855a,stroke-width:1.5px,color:#0f172a
  classDef support fill:#f5f0ff,stroke:#6b46c1,stroke-width:1.5px,color:#0f172a

  client["API Clients<br/>Swagger UI / future frontend"]:::edge --> web["REST API Layer<br/>Controllers, DTOs, validation, security filters"]:::edge

  subgraph application["Application Modules"]
    direction LR
    identity["Identity and Auth<br/>Users, roles, JWT, refresh tokens"]:::app
    accounts["Accounts<br/>Account numbers, balances, ownership"]:::app
    transfers["Transfers<br/>Execution, validations, transfer history"]:::app
    dashboard["Dashboard<br/>User summary and recent movements"]:::support
    risk["Risk and Fraud<br/>Risk profiles, assessments, fraud alerts"]:::support
    loans["Loans<br/>Financial profiles and loan applications"]:::support
    audit["Audit<br/>Persistent admin audit trail"]:::support
  end

  web --> identity
  web --> accounts
  web --> transfers
  web --> dashboard
  web --> risk
  web --> loans
  web --> audit

  subgraph platform["Persistence and Platform Services"]
    direction LR
    postgres[("PostgreSQL")]:::infra
    kafka["Kafka<br/>transfer-created"]:::infra
    flyway["Flyway<br/>schema migrations"]:::infra
    swagger["OpenAPI / Swagger UI"]:::infra
  end

  identity --> postgres
  accounts --> postgres
  transfers --> postgres
  dashboard --> postgres
  risk --> postgres
  loans --> postgres
  audit --> postgres
  transfers --> kafka
  postgres --> flyway
  web --> swagger
```

## Current Hexagonal View

```mermaid
flowchart LR
  client["API clients<br/>Swagger UI / future frontend"] --> web["REST API layer<br/>Controllers, DTOs, filters"]

  web --> identity["Identity and Auth<br/>Users, roles, JWT, refresh tokens"]
  web --> accounts["Accounts<br/>IBAN-style account numbers, balances, ownership"]
  web --> transfers["Transfers<br/>Account-number transfers, history, Kafka event"]
  web --> dashboard["Dashboard<br/>User summary, accounts, recent movements"]
  web --> risk["Risk and Fraud<br/>Risk profiles, assessments, fraud alerts"]
  web --> loans["Loans<br/>Financial profiles and loan applications"]
  web --> audit["Audit<br/>Persistent admin audit trail"]

  identity --> db[("PostgreSQL")]
  accounts --> db
  transfers --> db
  dashboard --> db
  risk --> db
  loans --> db
  audit --> db

  transfers --> kafka["Kafka<br/>transfer-created"]
  db --> flyway["Flyway migrations"]
  web --> swagger["OpenAPI / Swagger UI"]
```

## Target Microservice View

```mermaid
flowchart TB
  classDef exp fill:#e8f1ff,stroke:#2f6fed,stroke-width:1.5px,color:#0f172a
  classDef svc fill:#fff4e8,stroke:#dd6b20,stroke-width:1.5px,color:#0f172a
  classDef data fill:#edfdf4,stroke:#2f855a,stroke-width:1.5px,color:#0f172a
  classDef react fill:#f5f0ff,stroke:#6b46c1,stroke-width:1.5px,color:#0f172a

  ui["Angular UI / External Clients"]:::exp --> gateway["API Gateway<br/>Routing, auth propagation, throttling"]:::exp

  subgraph core["Core Banking Services"]
    direction LR
    identitySvc["Identity Service<br/>Users, roles, JWT"]:::svc
    accountSvc["Account Service<br/>Accounts and balances"]:::svc
    transferSvc["Transfer Service<br/>Transfer orchestration"]:::svc
  end

  subgraph eventing["Data and Event Backbone"]
    direction LR
    coreDb[("PostgreSQL")]:::data
    eventBus["Kafka Event Bus"]:::data
  end

  subgraph downstream["Reactive Services"]
    direction LR
    fraudSvc["Fraud Service<br/>Rules and alerting"]:::react
    scoringSvc["Scoring Service<br/>Behavioral scoring"]:::react
    notificationSvc["Notification Service<br/>Email and push"]:::react
    reportingSvc["Reporting Service<br/>Monthly insights"]:::react
  end

  gateway --> identitySvc
  gateway --> accountSvc
  gateway --> transferSvc

  identitySvc --> coreDb
  accountSvc --> coreDb
  transferSvc --> coreDb
  transferSvc --> eventBus

  eventBus --> fraudSvc
  eventBus --> scoringSvc
  eventBus --> notificationSvc
  eventBus --> reportingSvc
```

## Runtime Request Flow

```mermaid
sequenceDiagram
  autonumber
  participant Client as Swagger UI or client app
  participant Security as JWT security filter
  participant Controller as REST controller
  participant UseCase as Application use case
  participant Domain as Domain model
  participant Adapter as Persistence adapter
  participant DB as PostgreSQL
  participant Kafka as Kafka producer
  participant Audit as Audit log service

  Client->>Security: HTTP request with Bearer token
  Security->>Controller: Authenticated caller context
  Controller->>UseCase: Validated command/query DTO
  UseCase->>Domain: Apply business rules
  UseCase->>Adapter: Load or persist through output port
  Adapter->>DB: SQL via Spring Data JPA
  DB-->>Adapter: Entity data
  Adapter-->>UseCase: Domain object
  UseCase-->>Controller: Application response
  opt Business event
    UseCase->>Kafka: Publish transfer-created event
  end
  opt Auditable action
    UseCase->>Audit: Record action, status and actor
  end
  Controller-->>Client: JSON response or structured error
```

## Data Relationships

```mermaid
erDiagram
  APP_USER ||--|| USER_PROFILE : has
  APP_USER ||--o{ ACCOUNT : owns
  APP_USER ||--o{ REFRESH_TOKEN : authenticates
  APP_USER ||--o{ AUDIT_LOG : performs
  APP_USER ||--o{ FINANCIAL_PROFILE : declares
  APP_USER ||--o{ LOAN_APPLICATION : requests
  APP_USER ||--o| RISK_PROFILE : assessed_as

  ACCOUNT ||--o{ TRANSFER : sends
  ACCOUNT ||--o{ TRANSFER : receives

  TRANSFER ||--o| TRANSFER_RISK_ASSESSMENT : assessed_by
  TRANSFER ||--o{ FRAUD_ALERT : may_create

  APP_USER {
    uuid id
    string email
    string password_hash
    string role
    timestamp created_at
  }

  USER_PROFILE {
    uuid user_id
    string full_name
    int age
    string job_title
  }

  ACCOUNT {
    uuid id
    uuid user_id
    string account_number
    decimal balance
    string status
    int version
  }

  TRANSFER {
    uuid id
    uuid from_account_id
    uuid to_account_id
    decimal amount
    string status
    timestamp created_at
  }

  AUDIT_LOG {
    uuid id
    uuid actor_user_id
    string action
    string status
    timestamp occurred_at
  }
```

## API Surface by Consumer

```mermaid
flowchart TB
  swagger["Swagger UI<br/>Manual API testing"] --> public["Public endpoints<br/>POST /users, POST /auth/login, POST /auth/refresh"]
  swagger --> userapi["Authenticated user endpoints<br/>GET /dashboard/me, GET /accounts, POST /accounts, POST /transfers"]
  swagger --> adminapi["Admin endpoints<br/>GET /users, GET /audit-logs, cross-user account visibility"]

  public --> auth["Identity and token flow"]
  userapi --> banking["Banking operations<br/>accounts, transfers, balances"]
  userapi --> risk["Risk and loan flows<br/>fraud alerts, loan applications"]
  adminapi --> governance["Governance and observability<br/>audit trail, user management"]

  banking --> paged["Paged list responses<br/>content, page, size, totalElements, totalPages"]
  risk --> paged
  governance --> paged
```

## Evolution Map

```mermaid
flowchart LR
  current["Current modular monolith"] --> hardening["Near term<br/>examples, error catalog, more audit coverage"]
  hardening --> reactive["Mid term<br/>Kafka consumers, fraud workflow, scoring workflow"]
  reactive --> platform["Long term<br/>API gateway, bounded-context extraction, distributed workflows"]

  current --> c1["Identity"]
  current --> c2["Accounts"]
  current --> c3["Transfers"]
  current --> c4["Risk and Fraud"]
  current --> c5["Loans"]
  current --> c6["Audit"]

  platform --> p1["Notifications service"]
  platform --> p2["Reporting service"]
  platform --> p3["Scoring service"]
  platform --> p4["Fraud analysis service"]
  platform --> p5["Observability stack"]
```

## Reading the Diagrams

The first diagram is the current backend map. The second shows the same platform from a more explicitly hexagonal perspective. The third presents the target microservice-oriented shape. The remaining diagrams explain runtime flow, data relationships, API surface and the evolution path without losing the current modular boundaries.
