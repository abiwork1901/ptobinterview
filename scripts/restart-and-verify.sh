#!/usr/bin/env bash
set -euo pipefail

TIMEOUT_SECONDS="${1:-180}"
INTERVAL_SECONDS="${2:-3}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

echo "[restart] Restarting compose stack"
cd "${PROJECT_ROOT}"
docker compose down
docker compose up -d

echo "[restart] Running health verification"
"${SCRIPT_DIR}/health-check.sh" "${TIMEOUT_SECONDS}" "${INTERVAL_SECONDS}"

echo "[restart] Completed"
