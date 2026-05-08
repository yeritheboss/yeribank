# Product Overview

## Executive Summary

YeriBank is a modular, event-driven digital banking core created as a professional showcase project for enterprise-style backend engineering.

The project focuses on demonstrating:

- Hexagonal Architecture
- Domain-driven modeling
- Secure API design
- Transactional integrity in financial flows
- Event-driven communication
- Concurrency control strategies
- Evolvability toward microservices

YeriBank uses banking-inspired domains such as users, accounts, and transfers to model real architectural concerns without pretending to be a production banking platform.

## Product Vision

Traditional banking systems focus primarily on balances, transactions, and access control.

YeriBank extends that baseline with a broader product vision centered on **behavior-aware financial systems**. The long-term direction includes:

- Behavioral financial scoring
- Event-based state evolution
- Gamified financial progression
- AI-assisted financial insights

These ideas make the domain richer and help demonstrate how modern fintech products often combine transactional systems with analytics, scoring, and reactive workflows.

## Problem Statement

Modern financial systems must address challenges that go far beyond CRUD:

- Safe concurrent operations
- Strong transactional consistency
- Fraud-aware workflows
- Secure access boundaries
- Reliable event publication
- Clear evolution paths toward distributed systems

YeriBank exists to model those concerns in a compact but technically credible way.

## Core Functional Domains

YeriBank is organized around several conceptual domains.

### Implemented domains

#### User Domain

- Registration
- Authentication
- Authorization
- Role management

#### Account Domain

- Account creation
- Balance management
- Ownership rules
- Financial precision with `BigDecimal`

#### Transfer Domain

- Transactional money movement
- Transfer status lifecycle
- Account-number based transfer execution
- Account movement history
- Kafka event publication after successful transfer execution

#### Dashboard Read Model

- Authenticated user summary
- Account list for client applications
- Recent account movements across the user's accounts

### Planned domains

The following domains are part of the product direction, but are not yet fully implemented in the current codebase:

#### Fraud Domain

- Rule-based fraud detection
- Alert generation
- Suspicious transfer analysis

#### Scoring Domain

- Discipline scoring
- Risk scoring
- Trust scoring
- Level progression

#### Reporting Domain

- Monthly financial summaries
- AI-assisted behavioral insights

## Guiding Engineering Principles

### Domain First

Business rules should remain independent from frameworks and delivery mechanisms.

### Explicit Architecture

Layer boundaries should be visible in the codebase and easy to explain.

### Event-Driven Thinking

Important business actions should emit events that can be consumed by other parts of the system.

### Evolvability

The monolith should be structured so bounded contexts can be extracted later with minimal redesign.

### Demonstrability

Architectural decisions should be observable through code, API behavior, tests, and documentation.

## Why a Gamified Banking Direction

The gamified layer is not meant to trivialize banking. It exists to enrich the domain and create more interesting system behavior.

It enables future scenarios such as:

- Tracking financial discipline over time
- Modeling score changes after transfers and account activity
- Introducing reactive workflows driven by user behavior
- Simulating fintech-style trust and risk systems

This makes YeriBank a stronger architecture portfolio project because it combines transactional rigor with richer domain evolution.

## Non-Goals

YeriBank is not intended to:

- Operate as a real banking platform
- Implement full regulatory compliance
- Connect to real payment rails
- Provide production-grade AI decisions

Its role is to simulate realistic architecture and engineering concerns in a clean, inspectable project.

## Target Audience

YeriBank is designed for:

- Technical interviewers
- Senior backend engineers
- Software architects
- Engineering managers
- Fintech-oriented teams

The project aims to communicate engineering maturity, not just feature completeness.
