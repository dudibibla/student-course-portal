# Project Specification: Student Management & Chat Portal

## 1. Project Overview
The proposed project is a **Student Management & Chat Portal**. It functions as an educational platform where students can browse available courses, add them to their curriculum (similar to an e-commerce "shopping cart" experience), and communicate with teachers and peers via an integrated chat system. 

This application translates the classic "e-commerce" requirements into an educational domain, maintaining the required technical complexity (shopping cart flow using sessions, multiple related database tables, and security) while providing a meaningful, real-world utility.

## 2. Technology Stack
* **Backend:** Java, Spring Boot, Spring MVC, Spring Data JPA, Spring Security
* **Frontend:** HTML5, CSS3, Thymeleaf (with minimal JavaScript for chat auto-refreshing)
* **Database:** MySQL
* **Architecture:** MVC, Dependency Injection, Session Management

## 3. Database Entities & Relationships
The project consists of 5 core entities with complex relationships, fulfilling the requirement for relational data modeling:

1. **User (Student / Teacher / Admin):**
   * Stores user details, credentials, and roles.
   * *Relationships:* One-to-Many with `Registration`, One-to-Many with `ChatMessage`.
2. **Course (The "Product"):**
   * Represents a course available for enrollment (Title, description, capacity, assigned teacher).
   * *Relationships:* One-to-Many with `RegistrationItem`.
3. **Registration (The "Order"):**
   * Represents an official enrollment submission for a semester.
   * *Relationships:* Many-to-One with `User`, One-to-Many with `RegistrationItem`.
4. **RegistrationItem (The "Order Item"):**
   * A specific course within a registration request.
   * *Relationships:* Many-to-One with `Registration`, Many-to-One with `Course`.
5. **ChatMessage:**
   * Stores chat messages between users.
   * *Relationships:* Many-to-One with `User` (Sender) and `User` (Recipient) / `Course` (for group chats).

## 4. Session Management ("The Cart")
The project fulfills the "ShoppingCart" requirement using a **Course Cart** pattern:
When a student browses the course catalog and selects "Add to my schedule", the selected courses are stored in the user's **Session** (Course Cart). The data is only persisted to the database (creating a `Registration` record) when the student finalizes and confirms their enrollment, mirroring an e-commerce checkout process.

## 5. Key Views and Pages
The application includes the following main views:
1. **Landing / Home Page:** Introduction and platform overview.
2. **Login & Registration Page:** Secured access using Spring Security.
3. **Course Catalog:** A browsable list of available courses with "Add to schedule" functionality.
4. **Registration Cart:** A view of the user's session-stored courses prior to final confirmation.
5. **Student Dashboard & Chat:** The student's personal area showing their enrolled courses alongside an interactive chat interface.
6. **Admin / Teacher Dashboard:** A management interface for creating courses, viewing enrolled students, and interacting via chat.

## 6. Scope & Requirements Checklist
This project is designed to meet the following expected academic requirements:
- [x] Minimum of 4 inter-related database tables (Has 5).
- [x] Implementation of Session management (Course cart).
- [x] Implementation of Spring Security (Authentication and Role-based authorization).
- [x] CRUD operations on core entities.
- [x] Minimum of 5 distinct web pages/views.
