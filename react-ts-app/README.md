# React + TypeScript Web App

This is a web client starter built with **React.js + TypeScript + Vite**.

It includes a simple UI to call:

- `GET /actuator/health`

## Prerequisites

- Node.js 20+
- npm 10+

## Setup

From this folder:

```bash
npm install
```

## Run

```bash
npm run dev
```

Open the Vite URL shown in terminal (usually `http://localhost:5173`).

## Build

```bash
npm run build
npm run preview
```

## Containerized Deployment

Build and run production container:

```bash
docker build -t ptob/react-web:local .
docker run --rm -p 5173:80 ptob/react-web:local
```

## Generated Docker Image (Already Created)

The image has been generated and exported in the project root:

- Image tag: `ptob/react-web:local`
- Tar file: `../docker-images/react-web-local.tar`

Load image tar:

```bash
docker load -i ../docker-images/react-web-local.tar
```

Or run from root compose:

```bash
docker compose up -d web-react
```

## AWS and GCP Deployment Guidance

### AWS

- Push image to ECR.
- Deploy container on EKS (recommended for multi-service) or ECS Fargate.
- Put ALB in front with TLS and autoscaling.

### GCP

- Push image to Artifact Registry.
- Deploy on GKE (recommended for multi-service) or Cloud Run.
- Use HTTPS load balancer and autoscaling.

## Project Structure

- `src/App.tsx` - health check UI
- `src/main.tsx` - app entry
- `package.json` - scripts and dependencies
- `vite.config.ts` - Vite config
- `Dockerfile` - production image build
- `nginx.conf` - SPA routing config
