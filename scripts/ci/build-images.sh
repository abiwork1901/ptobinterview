#!/usr/bin/env bash
set -euo pipefail

echo "[ci] Building core images"
docker compose build app web-react

echo "[ci] Building optional app images"
docker compose --profile flutter --profile clients build flutter-web kotlin-client

echo "[ci] Building target microservice images"
docker compose --profile algo-microservices build \
  trading-service \
  ledger-service \
  omnibus-service \
  settlement-service \
  cost-basis-service \
  reconciliation-service \
  audit-event-service

echo "[ci] Image build completed"
