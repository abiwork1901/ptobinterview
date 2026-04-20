# PTO Core Algo README

This is the single combined core algorithm document for the PTOB project.

## 1)  Shared dependencies:

  - PostgreSQL for persistent state and audit records
  - Kafka for event publication and consumption

###(architecture direction

- Independently deployable microservices:
  - `trading-service`
  - `ledger-service`
  - `omnibus-service`
  - `settlement-service`
  - `cost-basis-service`
  - `reconciliation-service`
  - `audit-event-service`
- Docker image generation for these  services is available through the `algo-microservices` compose profile.

## 2) Algorithm-to-Service Mapping

- Price-time matching -> `TradingService` (target: `trading-service`)
- Pre-trade reservation -> `LedgerService` (target: `ledger-service`)
- Double-entry settlement -> `LedgerService` (target: `ledger-service`)
- Omnibus direct allocation -> `OmnibusService` (target: `omnibus-service`)
- Omnibus proportional allocation -> `OmnibusService` (target: `omnibus-service`)
- Weighted-average cost basis -> `CostBasisService` (target: `cost-basis-service`)
- Reconciliation -> `ReconciliationService` (target: `reconciliation-service`)
- Event publication/consumption -> `KafkaEventPublisher`/`AuditEventConsumer` (target: `audit-event-service`)

## 3) Implemented Core Algorithms

### 3.1 Price-Time Priority Matching

- Order book per symbol:
  - bids sorted descending
  - asks sorted ascending
- FIFO queue per price level.
- Matching executes deterministically when prices cross.

### 3.2 Pre-Trade Reservation

- BUY reserve = `price * quantity` in quote asset.
- SELL reserve = `quantity` in base asset.
- Prevents overspending and overselling.

### 3.3 Double-Entry Settlement

- Quote leg: buyer quote -> seller quote.
- Base leg: seller base -> buyer base.
- Immutable ledger entries are persisted for each leg.

### 3.4 Omnibus Direct Allocation

- Validates idempotency key.
- Debits omnibus and credits beneficiary per instruction.
- Persists ledger and idempotency record.

### 3.5 Omnibus Proportional Allocation

- Splits total amount by beneficiary weights.
- Rounds down intermediate shares.
- Assigns residual to the final beneficiary to preserve exact totals.

### 3.6 Weighted-Average Cost Basis

- BUY increases quantity and total cost.
- SELL reduces quantity and total cost using current average cost.

### 3.7 Reconciliation

- For each asset:
  - `difference = walletBalance - (internalOmnibusBalance + totalClientAllocated)`
- Any non-zero result is a reconciliation break.

### 3.8 Kafka Eventing

- Publishes events for orders, trades, ledger entries, allocations, and transfers.
- Consumer persists raw audit events for traceability and replay analysis.

## 4) Scope and Notes

- Algorithms are implemented and exposed via REST APIs.
- Current runtime is one Spring Boot deployable with clear domain boundaries.
- Architecture direction is multi-microservice by domain responsibility.
- Current implementation remains a practical reference/demo and not a full distributed matching cluster.

## 5) Run and Validate

```bash
docker compose up -d postgres zookeeper kafka
mvn test
mvn spring-boot:run
curl http://localhost:8080/actuator/health
```

If host health checks are intermittent, use:

```bash
./scripts/health-check.sh 180 3
```

Generate target-state service images:

```bash
docker compose --profile algo-microservices build trading-service ledger-service omnibus-service settlement-service cost-basis-service reconciliation-service audit-event-service
```

