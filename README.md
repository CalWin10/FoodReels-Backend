# 🍔 FoodReels Backend

<p align="center">
  <strong>A short-form food discovery and ordering backend built with Spring Boot</strong>
</p>

<p align="center">
  Watch → Discover → Decide → Order
</p>

<p align="center">

![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-336791)
![Maven](https://img.shields.io/badge/Maven-Build-red)
![Status](https://img.shields.io/badge/Status-In%20Development-yellow)

</p>

---

## 🚀 Overview

**FoodReels** is a food discovery platform inspired by short-form video apps. Instead of starting with a restaurant search, users discover food by scrolling through **food reels**, then view the dish, check the restaurant, and order through a supported platform.

```text
Watch a reel → Discover a dish → View restaurant → View food → Order
```

This repository contains the backend: APIs, persistence, business logic, authentication, recommendations, and ordering integration.

---

## 🎯 Problem Statement

Traditional food apps are built around search: *search → browse → order*. That works when you know what you want, but many users start with **"I don't know what I want to eat."**

FoodReels flips the model to reduce friction between **discovery and ordering**:

```text
Traditional:  Search → Browse → Decide
FoodReels:    Watch → Discover → Desire → Decide → Order
```

---

## ✨ Key Features

| Area | Features |
| --- | --- |
| 👤 Users | Registration, login, profile, roles |
| 🏪 Restaurants | Details, location, contact info, owned food items |
| 🍔 Food | Name, price, category, availability, media |
| 🎬 Reels | Feed, food/restaurant/creator linking, views |
| ❤️ Engagement | Likes, saves, comments, watch history |
| 🔍 Search | Food, restaurant, cuisine, price and location filters |
| 🧠 Recommendations | Personalized feed from watch, like, save and location signals |
| 🛒 Orders | Cart, order creation, history, status |
| 🔗 External Ordering | Redirect/integration with providers such as Zomato or Swiggy |

---

## 🛠️ Technology Stack

| Layer | Technology |
| --- | --- |
| Language | Java 25 |
| Framework | Spring Boot, Spring Web |
| Persistence | Spring Data JPA, Hibernate |
| Database | PostgreSQL |
| Build | Maven |
| Tools | VS Code, Git, GitHub, Postman, pgAdmin |

**Planned:** Spring Security, JWT, BCrypt, Redis, Elasticsearch/OpenSearch, Docker, CI/CD, cloud deployment.

---

## 🏛️ Architecture

The project is a **modular monolith** with a layered structure:

```text
HTTP Request → Controller → DTO → Service → Repository → JPA/Hibernate → PostgreSQL
```

| Layer | Responsibility |
| --- | --- |
| Controller | HTTP requests, responses, routing |
| Service | Business rules, workflows, transactions |
| Repository | Database access and queries |
| Entity | Persistent domain objects |
| DTO | Controls data exposed through the API |
| Exception | Centralized, consistent error handling |
| Config | Security, CORS, beans, external services |

---

## 🗄️ Domain Model

```text
User ──creates/views──▶ Reel ──represents──▶ Food ──belongs to──▶ Restaurant
```

**Core tables:** `users`, `restaurants`, `foods`, `reels`

**Planned tables:** `likes`, `saves`, `comments`, `carts`, `cart_items`, `orders`, `order_items`

---

## 🌐 API Design

Planned REST endpoint groups:

```text
/api/auth          /api/users        /api/restaurants
/api/foods         /api/reels        /api/comments
/api/cart          /api/orders
```

Example (Reels & Engagement):

```http
POST   /api/reels
GET    /api/reels
GET    /api/reels/{id}
POST   /api/reels/{id}/like
DELETE /api/reels/{id}/like
POST   /api/reels/{id}/comments
```

---

## 🔐 Authentication & Roles

Authentication will use **Spring Security + JWT** with password hashing.

| Role | Responsibilities |
| --- | --- |
| `USER` | Browse reels, like, save, comment, order |
| `RESTAURANT_OWNER` | Manage restaurant and food content |
| `ADMIN` | Manage users, restaurants, and moderation |

---

## 🧠 Recommendation System

The first version is a rule-based scoring model, before any machine learning:

```text
Reel Score = User Preference + Popularity + Freshness + Location Relevance + Engagement
```

---

## 🛣️ Roadmap

- [x] **Phase 0** — Planning
- [ ] **Phase 1** — Backend foundation *(in progress)*
- [ ] **Phase 2** — Authentication & security
- [ ] **Phase 3** — Restaurant & food management
- [ ] **Phase 4** — Food reels
- [ ] **Phase 5** — Personalized feed
- [ ] **Phase 6** — Redis & performance
- [ ] **Phase 7** — Search & location
- [ ] **Phase 8** — Cart & orders
- [ ] **Phase 9** — External ordering
- [ ] **Phase 10** — Production engineering

---

## 📌 Current Status

**🟡 Active Development — Phase 1: Backend Foundation**

```text
✅ Spring Boot application
✅ PostgreSQL connection
✅ JPA/Hibernate configuration
✅ User entity, UserRole enum, UserRepository

➡️ Next: UserService → UserController → User CRUD → DTOs → Validation
```

---

## 💻 Local Setup

**Requirements:** Java 25, PostgreSQL, Git, Postman

```bash
git clone https://github.com/CalWin10/FoodReels-Backend.git
cd FoodReels-Backend
git switch dev
```

**Database:** create a PostgreSQL database named `foodreels`, then set the connection properties in `src/main/resources/application.properties`.

> Never commit passwords or secrets. Use environment variables (`DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `JWT_SECRET`) as the project matures.

**Run the app** (`http://localhost:8080`):

```bash
./mvnw spring-boot:run        # Linux/macOS
.\mvnw.cmd spring-boot:run    # Windows
```

**Build and test:**

```bash
./mvnw clean package
./mvnw test
```

---

## 🔀 Git Workflow

```text
main  ← stable milestones
 └── dev  ← active development
      └── feature/*  ← individual features
```

Commit style: `feat:`, `fix:`, `test:`, `refactor:`, `docs:`

---

## 🧱 Engineering Principles

1. Build incrementally
2. Avoid premature complexity
3. Separate responsibilities
4. Protect sensitive data
5. Design before implementation
6. Test continuously
7. Keep Git history meaningful

---

## 🔮 Future Improvements

AI-based recommendations · creator and restaurant analytics · real-time notifications · social following · geospatial search · event-driven architecture · horizontal scaling

---

## 📄 License

Personal learning and portfolio project. See the repository license for terms.

---

## 👨‍💻 Developer

**Calwin Samuel V** — Computer Science & Engineering Student

GitHub: [CalWin10](https://github.com/CalWin10) · Repo: [FoodReels-Backend](https://github.com/CalWin10/FoodReels-Backend)

---

<p align="center">
  Built with ☕ Java + Spring Boot + PostgreSQL
</p>

<p align="center">
  <strong>Discover food. Watch. Decide. Order.</strong>
</p>