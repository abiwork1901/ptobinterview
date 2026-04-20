# RUN DOCKER IMAGES README

This document explains how to run Docker images for this project locally, on AWS, and on GCP.

## 1) Images Covered

- Spring Boot backend image (current single deployable): `ptobinterview-app:latest`
- Algorithm domain microservice images (target-state split, generated from current codebase):
  - `ptob/trading-service:local`
  - `ptob/ledger-service:local`
  - `ptob/omnibus-service:local`
  - `ptob/settlement-service:local`
  - `ptob/cost-basis-service:local`
  - `ptob/reconciliation-service:local`
  - `ptob/audit-event-service:local`
- React + TypeScript web app: `ptobinterview-web-react:latest`
- Flutter web app (optional): `ptobinterview-flutter-web:latest`
- Kotlin client app (optional): `ptobinterview-kotlin-client:latest`

Note: prebuilt tar files are available under `docker-images/`.

## 2) Local Run (From Prebuilt Tar Files)

Load image tar files:

```bash
docker load -i docker-images/react-web-local.tar
docker load -i docker-images/flutter-web-local.tar
docker load -i docker-images/kotlin-client-local.tar
```

If loaded names differ from compose defaults, retag to project-local names:

```bash
docker tag <loaded-react-image>:local ptobinterview-web-react:latest
docker tag <loaded-flutter-image>:local ptobinterview-flutter-web:latest
docker tag <loaded-kotlin-image>:local ptobinterview-kotlin-client:latest
```

Run core stack:

```bash
docker compose up -d
```

Run optional apps:

```bash
docker compose --profile flutter --profile clients up -d flutter-web kotlin-client --no-build
```

Verify:

- Backend health (inside compose network): `http://app:8080/actuator/health`
- React web: `http://localhost:5173`
- Flutter web: `http://localhost:8088`
- Kafka UI: `http://localhost:8081`
- Combined health helper: `./scripts/health-check.sh 180 3`
- Restart and verify helper: `./scripts/restart-and-verify.sh 180 3`

## 3) Build Images Locally (Optional)

```bash
docker compose build app web-react
docker compose --profile flutter --profile clients build flutter-web kotlin-client
docker compose --profile algo-microservices build trading-service ledger-service omnibus-service settlement-service cost-basis-service reconciliation-service audit-event-service
```

Equivalent helper script:

```bash
./scripts/ci/build-images.sh
```

If local memory is constrained and Kotlin build fails, use the prebuilt Kotlin tar image path above.

Run generated algorithm microservice images:

```bash
docker compose --profile algo-microservices up -d trading-service ledger-service omnibus-service settlement-service cost-basis-service reconciliation-service audit-event-service
```

Export target microservice images to tar files:

```bash
./scripts/ci/export-images.sh
```

## 4) AWS Run Path (ECR + EKS/ECS)

### 4.1 Push images to ECR

```bash
# Example names
docker tag ptobinterview-app:latest <account>.dkr.ecr.<region>.amazonaws.com/ptob/app:latest
docker tag ptobinterview-web-react:latest <account>.dkr.ecr.<region>.amazonaws.com/ptob/web-react:latest
docker tag ptobinterview-flutter-web:latest <account>.dkr.ecr.<region>.amazonaws.com/ptob/flutter-web:latest
docker tag ptobinterview-kotlin-client:latest <account>.dkr.ecr.<region>.amazonaws.com/ptob/kotlin-client:latest
docker tag ptob/trading-service:local <account>.dkr.ecr.<region>.amazonaws.com/ptob/trading-service:latest
docker tag ptob/ledger-service:local <account>.dkr.ecr.<region>.amazonaws.com/ptob/ledger-service:latest
docker tag ptob/omnibus-service:local <account>.dkr.ecr.<region>.amazonaws.com/ptob/omnibus-service:latest
docker tag ptob/settlement-service:local <account>.dkr.ecr.<region>.amazonaws.com/ptob/settlement-service:latest
docker tag ptob/cost-basis-service:local <account>.dkr.ecr.<region>.amazonaws.com/ptob/cost-basis-service:latest
docker tag ptob/reconciliation-service:local <account>.dkr.ecr.<region>.amazonaws.com/ptob/reconciliation-service:latest
docker tag ptob/audit-event-service:local <account>.dkr.ecr.<region>.amazonaws.com/ptob/audit-event-service:latest

docker push <account>.dkr.ecr.<region>.amazonaws.com/ptob/app:latest
docker push <account>.dkr.ecr.<region>.amazonaws.com/ptob/web-react:latest
docker push <account>.dkr.ecr.<region>.amazonaws.com/ptob/flutter-web:latest
docker push <account>.dkr.ecr.<region>.amazonaws.com/ptob/kotlin-client:latest
docker push <account>.dkr.ecr.<region>.amazonaws.com/ptob/trading-service:latest
docker push <account>.dkr.ecr.<region>.amazonaws.com/ptob/ledger-service:latest
docker push <account>.dkr.ecr.<region>.amazonaws.com/ptob/omnibus-service:latest
docker push <account>.dkr.ecr.<region>.amazonaws.com/ptob/settlement-service:latest
docker push <account>.dkr.ecr.<region>.amazonaws.com/ptob/cost-basis-service:latest
docker push <account>.dkr.ecr.<region>.amazonaws.com/ptob/reconciliation-service:latest
docker push <account>.dkr.ecr.<region>.amazonaws.com/ptob/audit-event-service:latest
```

### 4.2 Deploy

- Current image can run as one backend workload on EKS or ECS Fargate.
- Target architecture can split into multiple backend microservices over time.
- Web containers on EKS or ECS Fargate behind ALB.
- Managed data/event services:
  - RDS PostgreSQL
  - MSK Kafka
- Set backend env vars (`DB_*`, `KAFKA_BOOTSTRAP_SERVERS`, `APP_BEARER_TOKEN`) from Secrets Manager/Parameter Store.

## 5) GCP Run Path (Artifact Registry + GKE/Cloud Run)

### 5.1 Push images to Artifact Registry

```bash
docker tag ptobinterview-app:latest <region>-docker.pkg.dev/<project>/<repo>/ptob-app:latest
docker tag ptobinterview-web-react:latest <region>-docker.pkg.dev/<project>/<repo>/ptob-web-react:latest
docker tag ptobinterview-flutter-web:latest <region>-docker.pkg.dev/<project>/<repo>/ptob-flutter-web:latest
docker tag ptobinterview-kotlin-client:latest <region>-docker.pkg.dev/<project>/<repo>/ptob-kotlin-client:latest
docker tag ptob/trading-service:local <region>-docker.pkg.dev/<project>/<repo>/ptob-trading-service:latest
docker tag ptob/ledger-service:local <region>-docker.pkg.dev/<project>/<repo>/ptob-ledger-service:latest
docker tag ptob/omnibus-service:local <region>-docker.pkg.dev/<project>/<repo>/ptob-omnibus-service:latest
docker tag ptob/settlement-service:local <region>-docker.pkg.dev/<project>/<repo>/ptob-settlement-service:latest
docker tag ptob/cost-basis-service:local <region>-docker.pkg.dev/<project>/<repo>/ptob-cost-basis-service:latest
docker tag ptob/reconciliation-service:local <region>-docker.pkg.dev/<project>/<repo>/ptob-reconciliation-service:latest
docker tag ptob/audit-event-service:local <region>-docker.pkg.dev/<project>/<repo>/ptob-audit-event-service:latest

docker push <region>-docker.pkg.dev/<project>/<repo>/ptob-app:latest
docker push <region>-docker.pkg.dev/<project>/<repo>/ptob-web-react:latest
docker push <region>-docker.pkg.dev/<project>/<repo>/ptob-flutter-web:latest
docker push <region>-docker.pkg.dev/<project>/<repo>/ptob-kotlin-client:latest
docker push <region>-docker.pkg.dev/<project>/<repo>/ptob-trading-service:latest
docker push <region>-docker.pkg.dev/<project>/<repo>/ptob-ledger-service:latest
docker push <region>-docker.pkg.dev/<project>/<repo>/ptob-omnibus-service:latest
docker push <region>-docker.pkg.dev/<project>/<repo>/ptob-settlement-service:latest
docker push <region>-docker.pkg.dev/<project>/<repo>/ptob-cost-basis-service:latest
docker push <region>-docker.pkg.dev/<project>/<repo>/ptob-reconciliation-service:latest
docker push <region>-docker.pkg.dev/<project>/<repo>/ptob-audit-event-service:latest
```

### 5.2 Deploy

- Current image can run as one backend workload on GKE (or Cloud Run for simple backend-only setup).
- Target architecture can split into multiple backend microservices over time.
- Web containers on GKE or Cloud Run.
- Managed data/event services:
  - Cloud SQL PostgreSQL
  - Managed Kafka provider (for example Confluent Cloud) or self-managed Kafka on GKE
- Inject environment variables using Secret Manager and workload configs.

## 6) GitHub Actions CI/CD Integration

- CI workflow: `.github/workflows/ci.yml` (runs `mvn -B test` with required infra services).
- Docker workflow: `.github/workflows/docker-images.yml` (builds all images and optionally pushes to GHCR).
- GHCR push helper:

```bash
./scripts/ci/push-images-ghcr.sh ghcr.io <owner>/ptob <tag>
```

## 7) Local Ops Scripts

- Health check with retry and fallback:

```bash
./scripts/health-check.sh 180 3
```

- Restart full stack and verify:

```bash
./scripts/restart-and-verify.sh 180 3
```

