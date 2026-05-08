# Messaging Infrastructure

## Overview

YeriBank currently uses Kafka as its event backbone for asynchronous integration between banking flows and future downstream consumers.

In the local Docker Compose environment, Kafka runs together with Zookeeper.

This setup is intentionally simple and focused on development and demonstration.

## Current Role of Kafka

Kafka is currently used to publish transfer-related events after a successful transactional flow.

Current event:

- `TransferCreatedEvent`

Current topic:

- `transfer-created`

This gives the project an event-driven integration point without requiring downstream services to exist yet.

## Why Zookeeper Exists in This Project

Zookeeper is present because the local Kafka container setup uses the classic Kafka + Zookeeper model.

Its role here is infrastructure coordination for the Kafka broker in local development.

At this stage, Zookeeper is **not** part of the business architecture. It is only part of the local runtime stack used to support Kafka.

## Docker Compose Setup

The current `docker-compose.yml` defines:

- `zookeeper` on port `2181`
- `kafka` on port `9092`
- internal Kafka broker communication through `kafka:29092`

Relevant configuration:

- `KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181`
- `KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092`
- `SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:29092`

## Local Service Flow

```text
Spring Boot app -> Kafka broker -> topic: transfer-created
                      |
                      -> Zookeeper coordination
```

## Development Notes

- The application connects to Kafka through the internal Docker hostname `kafka:29092`
- Local tools outside Docker can connect through `localhost:9092`
- Zookeeper is exposed on `localhost:2181` mainly for local debugging or infrastructure inspection

## Scope and Limitations

Current messaging support is limited to event publication.

What exists now:

- Kafka broker in local environment
- Zookeeper-backed Kafka setup
- Transfer event publishing from the backend

What is not implemented yet:

- Internal Kafka consumers
- Fraud processing consumers
- Scoring consumers
- Notification consumers
- Production-grade messaging topology

## Public Documentation Guidance

In public-facing documentation, Kafka should be described as an implemented architectural capability.

Zookeeper should be described as a local infrastructure dependency for the current Docker-based Kafka setup, not as a product feature.
