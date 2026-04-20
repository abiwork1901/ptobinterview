#!/usr/bin/env bash
set -euo pipefail

TIMEOUT_SECONDS="${1:-180}"
INTERVAL_SECONDS="${2:-3}"
HOST_HEALTH_URL="${HOST_HEALTH_URL:-http://localhost:8080/actuator/health}"
APP_CONTAINER="${APP_CONTAINER:-ptobinterview-app-1}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

start_ts="$(date +%s)"

echo "[health] timeout=${TIMEOUT_SECONDS}s interval=${INTERVAL_SECONDS}s"
echo "[health] host_url=${HOST_HEALTH_URL}"
echo "[health] container=${APP_CONTAINER}"

while true; do
  if curl -fsS -m 5 "${HOST_HEALTH_URL}" >/dev/null 2>&1; then
    elapsed=$(( $(date +%s) - start_ts ))
    echo "[health] Host endpoint is healthy after ${elapsed}s"
    exit 0
  fi

  if docker exec "${APP_CONTAINER}" sh -c "curl -fsS -m 5 http://localhost:8080/actuator/health >/dev/null" >/dev/null 2>&1; then
    elapsed=$(( $(date +%s) - start_ts ))
    echo "[health] App is healthy inside container after ${elapsed}s"
    echo "[health] Host endpoint is still unreachable; this is a local host-network reachability issue."
    exit 0
  fi

  elapsed=$(( $(date +%s) - start_ts ))
  if [ "${elapsed}" -ge "${TIMEOUT_SECONDS}" ]; then
    echo "[health] Timed out after ${elapsed}s"
    echo "[health] Compose status:"
    (cd "${PROJECT_ROOT}" && docker compose ps) || true
    exit 1
  fi

  sleep "${INTERVAL_SECONDS}"
done
