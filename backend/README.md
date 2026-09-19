🚀 PrepTrack - Interview Intelligence Platform

PrepTrack is a full-stack Interview Intelligence Platform designed to help students prepare effectively for placements and internships by providing company-specific interview experiences, frequently asked questions, preparation tracking, and analytics.

---
 📌 Problem Statement

Students often struggle to find authentic interview experiences, company-specific interview patterns, and structured preparation resources. Information available on various platforms is scattered and often outdated.

PrepTrack solves this problem by providing a centralized platform where students can:

* Share interview experiences.
* Explore company-specific interview processes.
* Access frequently asked interview questions.
* Track preparation progress.
* Analyze interview trends.

---

 ✨ Features

🔐 Authentication & Authorization

* User Registration
* User Login
* JWT Authentication
* Refresh Token Support
* Email Verification
* Password Reset
* Role-Based Access Control (RBAC)

---

### 👥 User Management

* Student Profiles
* Profile Updates
* Resume Links
* LinkedIn/GitHub Integration

---

### 🏢 Company Module

* Add Companies
* Update Company Information
* Search Companies
* Company Overview

---

### 📝 Interview Experience Module

* Submit Interview Experiences
* View Experiences
* Update/Delete Experiences
* Company-wise Experiences

---

### 📚 Question Bank

* Topic-wise Questions
* Company-wise Questions
* Bookmark Questions
* Frequently Asked Questions

---

### 📊 Analytics Dashboard

* Most Asked Topics
* Most Asked Companies
* Interview Trends
* Difficulty Analysis

---

### 🔑 Role & Permission Management

Roles:

* ROLE_STUDENT
* ROLE_ADMIN

Permissions:

* USER_READ
* USER_WRITE
* INTERVIEW_CREATE
* INTERVIEW_DELETE
* ADMIN_ACCESS

---

## 🏗️ System Architecture

```text
Client (React)
      |
REST APIs
      |
Spring Boot Backend
      |
Service Layer
      |
Repository Layer
      |
MySQL Database
```

---

## 🛠️ Tech Stack

### Backend

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* JWT
* Maven

### Database

* MySQL

### Frontend

* React.js
* Tailwind CSS
* Axios
* React Router

### Tools

* Git
* GitHub
* Postman
* IntelliJ IDEA

---

## 📂 Project Structure

```text
src/main/java/com/preptrack

├── config
├── controller
├── dto
├── entity
├── exception
├── mapper
├── repository
├── security
├── service
│   └── impl
├── util
└── FullstackProjectApplication.java
```

---

## 🗄️ Database Tables

### Core Tables

* users
* roles
* permissions
* role_permissions

### Authentication Tables

* refresh_tokens
* revoked_tokens
* email_verification_tokens
* password_reset_tokens

---

## 🔐 Security Features

* BCrypt Password Encryption
* JWT Authentication
* Stateless Security
* Custom JWT Filter
* Role-Based Access Control
* Permission-Based Authorization

---

## 🚀 Getting Started

### Clone Repository

```bash
git clone https://github.com/yourusername/PrepTrack.git
```

### Navigate to Project

```bash
cd PrepTrack
```

### Configure Database

Update `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/preptrack_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### Build Project

```bash
mvn clean install
```

### Run Project

```bash
mvn spring-boot:run
```

Application will start on:

```text
http://localhost:8080
```

---

## 🔌 API Endpoints

### Authentication APIs

| Method | Endpoint                  |
| ------ | ------------------------- |
| POST   | /api/auth/register        |
| POST   | /api/auth/login           |
| POST   | /api/auth/refresh-token   |
| POST   | /api/auth/forgot-password |
| POST   | /api/auth/reset-password  |

---

## 🌟 Future Enhancements

* AI-Based Resume Analyzer
* Personalized Preparation Roadmap
* Mock Interview System
* Company Comparison Dashboard
* Real-Time Notifications
* Recommendation Engine
* Discussion Forum

---
 👨‍💻 Contributors

* **Sarthak Kumar**
* **Piyush Raghav**

---

📄 License

This project is developed for educational and learning purposes.

---

 ⭐ Support

If you like this project, please consider giving it a ⭐ on GitHub.
