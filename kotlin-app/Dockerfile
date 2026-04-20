FROM gradle:8.10.2-jdk21 AS builder
WORKDIR /workspace

COPY . .
RUN gradle --no-daemon clean installDist

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /workspace/build/install/kotlin-app /app/kotlin-app

ENV OMNIBUS_BASE_URL=http://app:8080

ENTRYPOINT ["/app/kotlin-app/bin/kotlin-app"]
