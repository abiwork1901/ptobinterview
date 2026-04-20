# Flutter App

This is a Flutter client starter app for your backend API service.  
It provides a simple UI to call `GET /actuator/health`.

## Prerequisites

- Flutter SDK 3.22+ (Dart 3.3+)

## Setup

From this folder:

```bash
flutter pub get
```

If this is your first time using this folder as a Flutter app, generate platform folders:

```bash
flutter create .
```

Then restore dependencies again:

```bash
flutter pub get
```

## Run

```bash
flutter run
```

In the app:

- Set backend URL (default is `http://localhost:8080`)
- Tap **Check Health**

## Containerized Web Deployment

Build and run Flutter web container:

```bash
docker build -f Dockerfile.web -t ptob/flutter-web:local .
docker run --rm -p 8088:80 ptob/flutter-web:local
```

Open `http://localhost:8088`.

## Generated Docker Image (Already Created)

The web image has been generated and exported in the project root:

- Image tag: `ptob/flutter-web:local`
- Tar file: `../docker-images/flutter-web-local.tar`

Load image tar:

```bash
docker load -i ../docker-images/flutter-web-local.tar
```

Or run from root compose:

```bash
docker compose --profile flutter up -d flutter-web
```

## Mobile Artifact Build in Cloud CI

Use `Dockerfile.mobile-ci` in AWS CodeBuild or GCP Cloud Build to produce Android artifacts:

```bash
docker build -f Dockerfile.mobile-ci -t ptob/flutter-mobile-ci:local .
docker run --rm -v "$PWD:/app" ptob/flutter-mobile-ci:local
```

This produces:

- release APK
- release AAB

## AWS and GCP Deployment Guidance

- **AWS:** use CodeBuild/CodePipeline to build APK/AAB and distribute through internal channels or Play Console workflows.
- **GCP:** use Cloud Build to generate APK/AAB and publish through your release pipeline.
- For browser access, deploy the web container on EKS/GKE or ECS/Cloud Run behind HTTPS ingress.

## Folder Layout

- `pubspec.yaml` - Flutter dependencies
- `analysis_options.yaml` - lint configuration
- `lib/main.dart` - starter UI and health-call flow
- `Dockerfile.web` - Flutter web container image
- `Dockerfile.mobile-ci` - CI image for mobile artifacts
