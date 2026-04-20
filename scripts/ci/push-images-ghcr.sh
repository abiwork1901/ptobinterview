#!/usr/bin/env bash
set -euo pipefail

REGISTRY="${1:-ghcr.io}"
IMAGE_PREFIX="${2:?image prefix is required (example: owner/ptob)}"
TAG="${3:-latest}"

declare -A IMAGES=(
  ["ptobinterview-app:latest"]="app"
  ["ptobinterview-web-react:latest"]="web-react"
  ["ptobinterview-flutter-web:latest"]="flutter-web"
  ["ptobinterview-kotlin-client:latest"]="kotlin-client"
  ["ptob/trading-service:local"]="trading-service"
  ["ptob/ledger-service:local"]="ledger-service"
  ["ptob/omnibus-service:local"]="omnibus-service"
  ["ptob/settlement-service:local"]="settlement-service"
  ["ptob/cost-basis-service:local"]="cost-basis-service"
  ["ptob/reconciliation-service:local"]="reconciliation-service"
  ["ptob/audit-event-service:local"]="audit-event-service"
)

for source_image in "${!IMAGES[@]}"; do
  target_name="${IMAGES[${source_image}]}"
  target_image="${REGISTRY}/${IMAGE_PREFIX}/${target_name}:${TAG}"
  echo "[ci] Tagging ${source_image} -> ${target_image}"
  docker tag "${source_image}" "${target_image}"
  echo "[ci] Pushing ${target_image}"
  docker push "${target_image}"
done

echo "[ci] GHCR push completed"
