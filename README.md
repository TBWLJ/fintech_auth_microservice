# Auth Microservice

The **Auth Microservice** is a core component of the fintech application responsible for handling user authentication, registration, KYC, and secure account identity features. It is built using **Spring Boot** and connected to a **PostgreSQL** database. This service issues JWT tokens for authentication and supports OTP-based verification.

---

## ✨ Features

- ✅ User Registration (with unique 10-digit NUBAN-style account number)
- ✅ Secure Login with JWT Token Issuance
- ✅ OTP Email Verification
- ✅ KYC Handling (Basic)
- ✅ Token-Based Authentication Middleware
- ✅ BCrypt Password Hashing
- ✅ Redis (optional) for token/session caching

---

## 🧱 Tech Stack<>

- **Java 21 / JDK 23**
- **Spring Boot**
- **Spring Security**
- **Spring Data JPA**
- **PostgreSQL**
- **Redis** (optional)
- **Flyway** (DB migration)
- **Docker** (Containerized deployment)
- **Maven** (Build tool)

---

## 🚀 Getting Started

### 📦 Prerequisites

Ensure you have the following installed:

- Java 21 or higher
- Maven
- Docker (optional, for containerized setup)
- PostgreSQL database (e.g., Render, Supabase, or local)
- Redis (optional, for OTP/session caching)

---

### 🔧 Configuration

Edit `src/main/resources/application.properties` or `application.yml` with your own credentials:

```properties
# Server
server.port=8080

# PostgreSQL Database
spring.datasource.url=jdbc:postgresql://<YOUR_HOST>:5432/<YOUR_DATABASE>
spring.datasource.username=<YOUR_USERNAME>
spring.datasource.password=<YOUR_PASSWORD>
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
app.jwt.secret=your_jwt_secret
app.jwt.expirationMs=86400000

# Mail (for OTP)
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=<YOUR_SENDGRID_API_KEY>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true


📮 API Endpoints
🔐 Auth Routes
Method	Endpoint	Description
POST	/api/auth/register	Register a new user
POST	/api/auth/login	Authenticate user & return JWT
POST	/api/auth/send-otp	Send verification OTP to email
POST	/api/auth/verify-otp	Verify OTP

👤 User Routes
Method	Endpoint	Description
GET	/api/users/profile	Get current user profile
PUT	/api/users/kyc	Submit KYC information

🔐 Security
BCrypt is used to hash user passwords.

JWT is used for secure authentication.

Tokens are stored in cookies (or headers) with HTTPOnly and Secure flags.

OTP verification is time-limited and sent over email.

🛠️ Development Scripts
bash
Copy
Edit
# Run the app
./mvnw spring-boot:run

# Run tests
./mvnw test

# Format code (if using Spotless/Checkstyle)
./mvnw spotless:apply
🧪 Environment Variables
Key	Description
JWT_SECRET	Secret used to sign JWTs
SPRING_DATASOURCE_*	DB connection variables
SENDGRID_API_KEY	For email delivery (OTP)

📁 Folder Structure
bash
Copy
Edit
src/
├── main/
│   ├── java/
│   │   └── com/yourcompany/auth/
│   │       ├── controller/
│   │       ├── model/
│   │       ├── service/
│   │       ├── repository/
│   │       ├── security/
│   │       └── utils/
│   └── resources/
│       └── application.properties
└── test/

🧳 Future Improvements
Add refresh tokens for better security

Rate-limit OTP requests

Add 2FA with Authenticator App

Integrate user roles and permissions

Full KYC integration with third-party APIs

✍️ Author
Taiwo Ayomide — 
Fintech Auth Microservice — Panelly / Exobank / [your app name]

📝 License
This project is licensed under the MIT License.