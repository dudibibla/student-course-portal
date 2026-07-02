# Student Management & Chat Portal

[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/GA17uJkR)

## General Functionality
A student portal built with Spring Boot MVC and Thymeleaf. It supports three roles:

* **Students** — browse the course catalog, add courses to a session-based registration cart, check out (with capacity and duplicate-registration checks), view their grades and grade average, write course reviews, and chat in the global consultation channel and per-course chat rooms.
* **Teachers** — claim unassigned courses, manage their courses (post assignments, enter grades for enrolled students), and view recent registrations.
* **Admin** — everything teachers can do, plus creating new courses. Only the admin can create courses; self-registration always creates a **student** account (teacher accounts are assigned by the admin).

Authentication and authorization are handled by Spring Security (BCrypt password hashing, role-based URL rules, and `@PreAuthorize` method security). Chat updates via lightweight polling every 5 seconds.

## Compile and Run Instructions
Requires Java 17+ and Docker (for the MySQL container).

```bash
# 1. Start the database (or let spring-boot-docker-compose start it for you)
docker compose up -d

# 2. Build and run
mvnw clean package
mvnw spring-boot:run
```

The app runs at `http://localhost:8080`. On first run the database is seeded automatically with the users and courses listed below.

To run the unit tests:
```bash
mvnw test
```

## Important Information & Credentials
* **Database:** MySQL, database name `ex4`, exposed on port **3307** (see `compose.yaml`)
  * Username: `myuser`
  * Password: `secret`
  * Root password: `verysecret`
  * Connection settings can be overridden with the `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` environment variables.
* **Admin Login Credentials:**
  * Email: `admin@portal.com`
  * Password: `admin123`
* **Teacher Login Credentials (example):**
  * Email: `turing@portal.com`
  * Password: `password`
* **Student Login Credentials (example):**
  * Email: `david@student.com`
  * Password: `password`

All seeded non-admin users share the password `password`.

## Demo Video
[Link to Demo Video](#) (To be added before submission)
