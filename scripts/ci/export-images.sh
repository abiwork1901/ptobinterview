#!/usr/bin/env bash
set -euo pipefail

OUTPUT_DIR="${1:-docker-images/microservices}"
mkdir -p "${OUTPUT_DIR}"

declare -A EXPORT_MAP=(
  ["ptob/trading-service:local"]="trading-service-local.tar"
  ["ptob/ledger-service:local"]="ledger-service-local.tar"
  ["ptob/omnibus-service:local"]="omnibus-service-local.tar"
  ["ptob/settlement-service:local"]="settlement-service-local.tar"
  ["ptob/cost-basis-service:local"]="cost-basis-service-local.tar"
  ["ptob/reconciliation-service:local"]="reconciliation-service-local.tar"
  ["ptob/audit-event-service:local"]="audit-event-service-local.tar"
)

for image in "${!EXPORT_MAP[@]}"; do
  archive="${OUTPUT_DIR}/${EXPORT_MAP[${image}]}"
  echo "[ci] Exporting ${image} -> ${archive}"
  docker save -o "${archive}" "${image}"
done

echo "[ci] Export completed"
