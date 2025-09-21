# 🎯 Quizzy - Microservices Quiz Platform

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![GitHub stars](https://img.shields.io/github/stars/Devansh-sys/quizzy?style=social)](https://github.com/Devansh-sys/quizzy/stargazers)

A distributed quiz platform built with Spring Boot microservices, featuring user authentication, quiz management, and AI-powered question generation.

## 🌟 Features

- **User Management**
  - JWT-based authentication
  - Role-based access control
  - User registration and profile management

- **Quiz System**
  - Create and manage quizzes
  - Submit answers and view results
  - Track quiz history

- **Question Bank**
  - Categorized questions
  - Multiple question types
  - AI-powered question generation

- **Rate Limiting**
  - Redis-based rate limiting
  - Protects AI endpoints
  - Configurable limits

## 🏗️ System Architecture

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   API Gateway   │◄───►│  Quiz Service   │◄───►|   PostgreSQL   |
└─────────────────┘     └─────────────────┘     └─────────────────┘
        ▲                       ▲
        │                       │
        ▼                       ▼
┌─────────────────┐     ┌─────────────────┐
│  User Service   |     | Question Service |
└─────────────────┘     └─────────────────┘
        ▲                       ▲
        └───────────┬───────────┘
                    │
                    ▼
           ┌─────────────────┐
           |  Service Registry  |
           └─────────────────┘
```

## 🛠️ Tech Stack

- **Backend**: Java 17, Spring Boot 3.1.1
- **Database**: PostgreSQL
- **Authentication**: JWT, Spring Security
- **Service Discovery**: Eureka
- **API Gateway**: Spring Cloud Gateway
- **AI**: Google Gemini Pro
- **Caching**: Redis
- **Build**: Maven

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6.3+
- PostgreSQL 13+
- Redis 6.2+

### Setup

1. **Clone the project**
   ```bash
   git clone https://github.com/Devansh-sys/quizzy.git
   cd quizzy
   ```

2. **Set up databases**
   ```sql
   CREATE DATABASE quiz_db;
   CREATE DATABASE user_db;
   CREATE DATABASE question_db;
   ```

3. **Configure services**
   Update `application.properties` in each service with your database and Redis credentials.

4. **Run services**
   ```bash
   # Start services in order
   cd service-registry && mvn spring-boot:run
   cd user-service && mvn spring-boot:run
   cd question-service && mvn spring-boot:run
   cd quiz-service && mvn spring-boot:run
   cd api-gateway && mvn spring-boot:run
   ```

## 📚 API Reference

### Authentication
Add JWT token to header:
```
Authorization: Bearer <token>
```

### Endpoints

#### User Service
- `POST /api/v1/auth/register` - Register user
- `POST /api/v1/auth/authenticate` - Login
- `GET /api/v1/users/me` - User profile

#### Quiz Service
- `POST /quiz/create` - New quiz
- `GET /quiz/{id}` - Get quiz
- `POST /quiz/submit/{id}` - Submit answers
- `GET /quiz/user/{userId}` - User history

#### Question Service
- `GET /question/all` - All questions
- `POST /question/add` - Add question (Admin)
- `GET /question/category/{category}` - By category
- `GET /question/generate` - AI questions

## 🔒 Rate Limiting
- 5 requests/minute per endpoint
- Uses Redis

## 🤝 Contributing
1. Fork the repo
2. Create feature branch
3. Commit changes
4. Push and open PR

---

<div align="center">
  <h3>✨ Happy Quizzing! ✨</h3>
  <p>If you find this project useful, please consider giving it a ⭐️</p>
</div>

