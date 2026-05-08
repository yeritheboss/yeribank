# Backend Diagrams

This page explains the current YeriBank backend visually and shows the intended evolution path.

## Current Backend Modules

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

The first diagram is the current backend map. The second shows how one request moves through security, controllers, use cases, domain logic and adapters. The third focuses on persistence relationships. The fourth and fifth diagrams show how the API is consumed today and how the project can grow without losing the existing modular boundaries.
