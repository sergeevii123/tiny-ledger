# Tiny Ledger

A simple multi-account ledger application that provides basic banking operations through REST APIs.

## Requirements

For local development:
- Java 21 or higher
- Gradle

For Docker deployment:
- Docker
- Docker Compose

## Running the Application

### Using Docker (Recommended)

Build and run using Docker Compose:
```bash
docker-compose up --build
```

To stop the application:
```bash
docker-compose down
```

### Local Development

Run using Gradle:
```bash
gradle bootRun
```

## API 
open in browser: http://localhost:8080/swagger-ui/index.html

```
POST /api/accounts/create                - Create account
GET  /api/accounts/list                 - List all accounts
GET  /api/accounts/{accountId}          - Get specific account name and balance

POST /api/transactions/{accountId}/deposit   - Make deposit
POST /api/transactions/{accountId}/withdraw  - Make withdrawal
POST /api/transactions/transfer             - Transfer between accounts
GET  /api/transactions/{accountId}          - Get transaction history
```

## Design Decisions

1. Multi-account Support: The service supports multiple accounts with separate transaction histories and balances
2. In-memory Storage: Uses thread-safe concurrent collections for simplicity
3. Immutable Objects: Uses Java records for immutable data structures
4. BigDecimal: Used for monetary calculations to avoid floating-point precision issues
5. Docker Support: Multi-stage build for smaller image size and better security
6. RESTful Organization: Resources (accounts, transactions) are properly separated with clear responsibilities
7. OpenAPI/Swagger: Interactive API documentation with try-it-out functionality

## Limitations

1. No persistence - data is lost when the container restarts
2. No authentication/authorization
3. Basic transaction isolation (only synchronized transfers)