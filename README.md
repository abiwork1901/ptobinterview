# PTOB Interview Project

Reference implementation for trading, omnibus allocation, settlement, ledger, cost basis, and reconciliation workflows.

## Current vs Target

- Current: one Spring Boot backend deployable with domain service modules.
- Target: split modules into independently deployable Spring Boot microservices.

## Documentation Map

- Primary runbook + architecture diagram: `ALL_COMBINED_README.MD`
- Docker image build/run and cloud push path: `RUN_DOCKER_IMAGES_README.md`
- Core algorithm details and module mapping: `PTO_CORE_ALGO_README.md`

## Script Shortcuts

- Health check (host + in-container fallback): `./scripts/health-check.sh 180 3`
- Restart and verify stack: `./scripts/restart-and-verify.sh 180 3`
- Build all images: `./scripts/ci/build-images.sh`
- Push images to GHCR: `./scripts/ci/push-images-ghcr.sh ghcr.io <owner>/ptob <tag>`

## Quick Start

```bash
docker compose up -d
mvn test
```

Optional clients:

```bash
docker compose --profile flutter --profile clients up -d flutter-web kotlin-client
```

Target microservice image build:

```bash
docker compose --profile algo-microservices build trading-service ledger-service omnibus-service settlement-service cost-basis-service reconciliation-service audit-event-service
```

Health check helper:

```bash
./scripts/health-check.sh 180 3
```

Restart + verify helper:

```bash
./scripts/restart-and-verify.sh 180 3
```

## Tests

Current automated tests include:

- API flow test: `src/test/java/com/ptob/demo/controller/TradingControllerTest.java`
- Unit tests:
  - `src/test/java/com/ptob/demo/service/RiskServiceTest.java`
  - `src/test/java/com/ptob/demo/service/OmnibusServiceTest.java`

