#!/usr/bin/env bash
set -euo pipefail

TIMEOUT_SECONDS="${1:-180}"
START_TS="$(date +%s)"

declare -a SERVICES=(
  "trading-service"
  "ledger-service"
  "omnibus-service"
  "settlement-service"
  "cost-basis-service"
  "reconciliation-service"
  "audit-event-service"
)

for service in "${SERVICES[@]}"; do
  container="ptobinterview-${service}-1"
  echo "[wait] Waiting for ${service} container health endpoint"
  while true; do
    if docker exec "${container}" sh -c "curl -fsS -m 5 http://localhost:8080/actuator/health >/dev/null" 2>/dev/null; then
      echo "[wait] ${service} is healthy"
      break
    fi
    now="$(date +%s)"
    elapsed=$((now - START_TS))
    if [ "${elapsed}" -ge "${TIMEOUT_SECONDS}" ]; then
      echo "[wait] Timed out waiting for ${service} (>${TIMEOUT_SECONDS}s)"
      exit 1
    fi
    sleep 5
  done
done

echo "[wait] All algorithm microservices are healthy"
