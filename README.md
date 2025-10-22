# SkillMatch Auth Service

Authentication and authorization microservice for the SkillMatch platform.

### 🧰 Tech Stack
- Java 21
- Spring Boot 3.4.10
- Spring Security + JWT
- PostgreSQL (JPA)
- Maven

### 🚀 Features
- User registration and login
- JWT token generation and validation
- BCrypt password hashing
- Role-based access (ADMIN, MENTOR, LEARNER, RECRUITER)

### 🛠️ Run Locally
```bash
export JWT_SECRET="your_secret_here"
mvn spring-boot:run
