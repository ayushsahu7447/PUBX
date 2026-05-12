# PUBX

# 🎤 PUBX — Public Talent × Private Events

**Connect talented individuals with event organizers. Seamlessly.**

PUBX is a microservices-based platform where **People** (stand-up comedians, musicians, speakers, hackers, dancers, anchors) showcase their talent and get booked by **Projects** (colleges, companies, hotels, NGOs, startups) for real-world events.

> Think of it as **LinkedIn meets BookMyShow** — but for live talent booking.

---

## 🧠 The Problem

- **Talented people** struggle to find gigs. They rely on word-of-mouth or social media DMs.
- **Event organizers** waste hours searching for the right speaker, performer, or host.
- There's no single platform where talent discovery, booking, and payment happen in one flow.

---

## 💡 The Solution — PUBX

| For People (Talent) | For Projects (Organizers) |
|---------------------|---------------------------|
| Create rich profile with topics, bio, media | Discover talent by topic, city, rating |
| Upload portfolio (images + reels) | Post event requirements publicly |
| Set availability calendar | Send direct booking requests |
| Accept/reject bookings | Track booking status end-to-end |
| Get rated after events | Rate talent after events |
| Get discovered by organizers | Get notified on new matching talent |

---

## 🏗️ Architecture
┌──────────────┐ ┌──────────────┐ ┌──────────────┐ │ Frontend │────▶│ API Gateway │────▶│ Auth Service │ │ (React/Next)│ │ (port 8080) │ │ (port 8081) │ └──────────────┘ └──────┬───────┘ └──────────────┘ │ ┌─────────────┼─────────────┐ │ │ │ ┌────────▼──┐ ┌──────▼───┐ ┌─────▼────────┐ │User Service│ │Booking │ │Requirement │ │(port 8082) │ │Service │ │Service │ └────────────┘ │(port 8083)│ │(port 8084) │ └───────────┘ └──────────────┘ │ │ │ ┌────────▼──┐ ┌──────▼───┐ ┌─────▼────────┐ │Notification│ │Rating │ │Search/Feed │ │Service │ │Service │ │Service │ │(port 8085)│ │(port 8086)│ │(port 8087) │ └───────────┘ └───────────┘ └──────────────┘




**Each service has its own database** — true microservice isolation.

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.x |
| **Security** | Spring Security + JWT (Access + Refresh tokens) |
| **Database** | PostgreSQL (separate DB per service) |
| **Migrations** | Flyway |
| **Caching** | Redis (token blacklist, session) |
| **Messaging** | Apache Kafka (async events between services) |
| **Search** | Elasticsearch (talent discovery) |
| **Storage** | MinIO / AWS S3 (images, reels) |
| **API Docs** | Swagger / OpenAPI 3.0 |
| **Gateway** | Spring Cloud Gateway |
| **Containerization** | Docker + Docker Compose |
| **Build Tool** | Maven (multi-module) |

---

## 📦 Microservices

| Service | Port | Database | Responsibility |
|---------|------|----------|---------------|
| `auth-service` | 8081 | `pubx_auth` | Register, Login, JWT, Logout, Refresh |
| `user-service` | 8082 | `pubx_user` | Profiles, Topics, Media, Availability |
| `booking-service` | 8083 | `pubx_booking` | Booking requests, Accept/Reject, Status |
| `requirement-service` | 8084 | `pubx_requirement` | Public event postings by organizers |
| `notification-service` | 8085 | `pubx_notification` | Email, Push, In-app notifications |
| `rating-service` | 8086 | `pubx_rating` | Post-event ratings & reviews |
| `search-service` | 8087 | Elasticsearch | Talent search, filters, feed |
| `api-gateway` | 8080 | — | Routing, rate limiting, auth check |

---

## 🔐 Security Design
Registration → BCrypt password hash → stored in DB Login → JWT Access Token (15 min) + Refresh Token (7 days) Refresh Token → SHA-256 hashed → stored in DB (rotation on use) Logout → Access token blacklisted (in-memory / Redis) Inter-service → Shared JWT secret (local validation, zero network calls)




- UUIDs for all entity IDs (non-guessable, distributed-safe)
- Role-based access: `PERSON`, `PROJECT`, `ADMIN`
- Refresh token rotation (old token deleted on each refresh)
- ADMIN role cannot be created via API

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- PostgreSQL 16+
- (Later) Redis, Kafka, Elasticsearch, Docker

### Setup

```bash
# Clone
git clone https://github.com/YOUR_USERNAME/pubx.git
cd pubx

# Create databases
psql -U postgres
CREATE DATABASE pubx_auth;
CREATE DATABASE pubx_user;
CREATE DATABASE pubx_booking;
CREATE DATABASE pubx_requirement;
CREATE DATABASE pubx_notification;
CREATE DATABASE pubx_rating;
\q

# Run auth-service
cd auth-service
mvn spring-boot:run

# Run user-service (separate terminal)
cd user-service
mvn spring-boot:run
Test
bash


# Swagger UI
http://localhost:8081/swagger-ui.html   # Auth APIs
http://localhost:8082/swagger-ui.html   # User APIs

# Quick test
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@gmail.com","password":"pass1234","role":"PERSON"}'
📂 Project Structure


pubx/
├── pom.xml                    (parent POM — manages all modules)
├── auth-service/
│   ├── pom.xml
│   └── src/main/java/com/pubx/auth/
│       ├── config/            (JWT, Security, Filters)
│       ├── controller/        (REST endpoints)
│       ├── dto/               (Request/Response objects)
│       ├── entity/            (JPA entities → DB tables)
│       ├── enums/             (Role, etc.)
│       ├── exception/         (Global error handling)
│       ├── repository/        (Database queries)
│       ├── service/           (Business logic)
│       └── util/              (Hashing, helpers)
├── user-service/
│   ├── pom.xml
│   └── src/main/java/com/pubx/user/
│       ├── config/
│       ├── controller/
│       ├── dto/
│       ├── entity/
│       ├── enums/
│       ├── exception/
│       ├── repository/
│       └── service/
├── booking-service/           (coming soon)
├── requirement-service/       (coming soon)
├── notification-service/      (coming soon)
├── rating-service/            (coming soon)
├── search-service/            (coming soon)
└── api-gateway/               (coming soon)
🗓️ Roadmap
 Auth Service (Register, Login, Logout, Refresh, JWT)
 User Service (Profiles, Topics, Media, Availability)
 Booking Service (Request, Accept, Reject, Status)
 Requirement Service (Public event postings)
 Notification Service (Email + Push)
 Rating Service (Post-event reviews)
 Search Service (Elasticsearch-powered discovery)
 API Gateway (Routing + Rate limiting)
 Docker Compose (One-command deployment)
 CI/CD Pipeline
 Frontend (React/Next.js)
🧪 API Endpoints (so far)
Auth Service — localhost:8081
Method	Endpoint	Auth	Description
POST	/api/v1/auth/register	❌	Create account
POST	/api/v1/auth/login	❌	Get JWT tokens
POST	/api/v1/auth/refresh	❌	Refresh access token
POST	/api/v1/auth/logout	✅	Blacklist token
GET	/api/v1/auth/me	✅	Get current user
GET	/api/v1/auth/health	❌	Health check
User Service — localhost:8082
Method	Endpoint	Auth	Description
GET	/api/v1/topics	❌	List all topics
POST	/api/v1/persons/profile	✅	Create person profile
GET	/api/v1/persons/profile/me	✅	Get my profile
PUT	/api/v1/persons/profile	✅	Update profile
GET	/api/v1/persons/{username}	❌	Public profile
POST	/api/v1/projects/profile	✅	Create project profile
GET	/api/v1/projects/profile/me	✅	Get my project profile
PUT	/api/v1/projects/profile	✅	Update project profile
GET	/api/v1/projects/{username}	❌	Public project profile
👨‍💻 Author
Ayush Sahu

Building PUBX from scratch — full-stack microservices
