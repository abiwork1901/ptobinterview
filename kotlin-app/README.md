# Kotlin App

This is a lightweight Kotlin JVM client app that calls the backend API health endpoint.

## Prerequisites

- Java 21+
- Gradle 8+ (or use your local Gradle wrapper if you add one)

## Run

From this folder:

```bash
gradle run
```

Optional environment variable:

```bash
export OMNIBUS_BASE_URL=http://localhost:8080
```

The app calls:

- `GET /actuator/health`

## Container Run

Build and run as container:

```bash
docker build -t ptob/kotlin-client:local .
docker run --rm -e OMNIBUS_BASE_URL=http://host.docker.internal:8080 ptob/kotlin-client:local
```

## Generated Docker Image (Already Created)

The image has been generated and exported in the project root:

- Image tag: `ptob/kotlin-client:local`
- Tar file: `../docker-images/kotlin-client-local.tar`

Load image from tar on another machine:

```bash
docker load -i ../docker-images/kotlin-client-local.tar
```

Run via root compose profile:

```bash
docker compose --profile clients up kotlin-client
```

## AWS and GCP Readiness

This app can be used as:

- a smoke-check sidecar/job in EKS/GKE,
- a scheduled health job in ECS/Cloud Run Jobs.

Recommended cloud flow:

1. Push image to ECR (AWS) or Artifact Registry (GCP).
2. Run as a short-lived job/container with `OMNIBUS_BASE_URL` pointed to backend ingress URL.

## Folder Layout

- `build.gradle.kts` - build configuration
- `settings.gradle.kts` - project name
- `src/main/kotlin/.../App.kt` - entrypoint
- `Dockerfile` - cloud/container execution
