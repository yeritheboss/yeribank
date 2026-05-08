# API Overview

## API Style

YeriBank currently exposes a REST API over HTTP with JSON payloads.

Authentication is based on:

- JWT access tokens
- Refresh token rotation
- Role-based access control with `ADMIN` and `USER`

OpenAPI documentation is available locally through Swagger UI:

- `http://localhost:8080/swagger-ui.html`

## Pagination

List endpoints return a common paginated response:

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0
}
```

Rules:

- `page` starts at `0`
- `size` defaults to the endpoint value and is capped at `100`
- `content` contains the current page items
- `totalElements` and `totalPages` describe the full filtered result set

## Security Model

### Roles

- `USER`
- `ADMIN`

### Access rules

- Public authentication endpoints are available for login and token refresh
- User creation supports bootstrap behavior for the first admin user
- Sensitive banking operations require a valid JWT
- Account access is restricted to the owner or an admin user
- Transfer execution is validated against caller permissions

## Current Endpoints

### Users

#### `POST /users`

Creates a new user.

Notes:

- The first user can bootstrap the system as `ADMIN`
- After bootstrap, only an authenticated `ADMIN` can create additional admin users

#### `GET /users`

Lists users.

Rules:

- Requires JWT authentication
- Only `ADMIN` users can access it
- Supports `page` and `size`

### Auth

#### `POST /auth/login`

Authenticates a user with email and password.

Returns:

- Access token
- Refresh token
- Token type
- Access token expiration metadata

#### `POST /auth/refresh`

Rotates the refresh token and returns a new token pair.

### Accounts

#### `GET /accounts`

Lists visible accounts.

Rules:

- Requires JWT authentication
- A regular user sees their own accounts
- An admin can list all accounts or filter by `userId`
- Responses include owner profile data for UI use
- Supports `page` and `size`

#### `POST /accounts`

Creates an account.

Rules:

- Requires JWT authentication
- A regular user creates an account for themselves
- An admin can create an account for another user by providing `userId`

#### `GET /accounts/{id}/balance`

Returns the balance for a given account.

Rules:

- Requires JWT authentication
- Accessible by the account owner
- Accessible by an admin user

#### `GET /accounts/{accountNumber}/transfers`

Lists recent incoming and outgoing transfers for an account.

Rules:

- Requires JWT authentication
- Uses the account number / IBAN-style identifier, not the internal ID
- A regular user can only read movements for their own accounts
- An admin can read movements for any account
- Supports `direction`, `page`, and `size`

Filters:

- `direction=INCOMING`
- `direction=OUTGOING`

Response entries include:

- Source and destination account numbers
- Amount
- Status
- Risk score when available
- Direction from the requested account perspective: `INCOMING` or `OUTGOING`

### Transfers

#### `POST /transfers`

Executes a transfer between two accounts.

Behavior:

- Requires JWT authentication
- Validates the source and destination accounts
- Prevents invalid transfer scenarios such as self-transfer
- Supports transfers by account number through `fromAccountNumber` and `toAccountNumber`
- Executes within a transactional boundary
- Publishes a Kafka event after successful completion

### Dashboard

#### `GET /dashboard/me`

Returns a UI-ready summary for the authenticated user.

Current response includes:

- User profile data
- Visible accounts
- Recent transfers across the user's accounts

### Audit Logs

#### `GET /audit-logs`

Returns recent auditable system events.

Rules:

- Requires JWT authentication
- Only `ADMIN` users can access it
- Supports `action`, `status`, `actorUserId`, `page`, and `size`

Current audited actions include:

- User creation
- Login success and failure
- Refresh token rotation
- Account creation
- Transfer execution

### Fraud

#### `GET /fraud/alerts`

Lists fraud alerts.

Rules:

- Requires JWT authentication
- Supports `status`, `page`, and `size`

#### `PATCH /fraud/alerts/{alertId}/status`

Reviews a fraud alert and updates its status.

Rules:

- Requires JWT authentication
- Used for alert review workflows

### Financial Profile

#### `POST /financial-profile`

Creates or updates the financial profile used by risk and loan flows.

Current profile data includes:

- Monthly income
- Monthly expenses
- Current debt

#### `GET /financial-profile/me`

Returns the authenticated user's financial profile.

#### `GET /financial-profile/{userId}`

Returns another user's financial profile when the caller is allowed to access it.

### Risk

#### `GET /risk/profile/me`

Returns the authenticated user's risk profile.

Current response includes:

- Score
- Last transfer assessment score
- Risk level
- Alert count over the last 90 days

#### `GET /risk/profile/{userId}`

Returns another user's risk profile when the caller is allowed to access it.

#### `GET /risk/assessments/transfers/{transferId}`

Returns the risk assessment associated with a transfer.

Current response includes:

- Transfer ID
- User ID
- Score
- Decision
- Risk level
- Reasons
- Creation timestamp

### Loans

#### `POST /loans/simulations`

Simulates a loan offer from financial profile and risk information.

#### `POST /loans/applications`

Creates a loan application using the current risk and financial profile state.

#### `GET /loans/applications`

Lists loan applications.

Rules:

- Requires JWT authentication
- A regular user sees their own loan applications
- An admin can filter by `userId`
- Supports `page` and `size`

## Event Publication

The current implementation publishes:

- `TransferCreatedEvent`

Configured topic:

- `transfer-created`

## Documentation Guidance

The public API documentation should always reflect the implemented controllers first. Planned endpoints or future modules should be documented separately as roadmap items, not mixed into the live API contract.
