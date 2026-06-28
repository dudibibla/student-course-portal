# Project Plan & Documentation

## 1. General Explanation
This project is an E-Commerce Website (Store) built with Spring Boot MVC. It allows users to browse products, add items to a shopping cart, and place orders. It includes user authentication and an admin backend to manage products and users.

## 2. Skills & Technologies (Skills)
* **Backend:** Java, Spring Boot, Spring MVC, Spring Data JPA, Spring Security
* **Frontend:** HTML5, CSS3, Thymeleaf (Server-side rendering), Javascript (limited, for validation)
* **Database:** MySQL (Database name: `ex4`)
* **Architecture:** MVC (Model-View-Controller), Dependency Injection (Beans), Session Management

## 3. Main Pages & Their Goals (At least 5 pages)
1. **Landing / Home Page:** Displays featured products and navigation.
2. **Login / Registration Page:** User authentication using Spring Security.
3. **Product Catalog & Search Page:** Browse all products, advanced search, and filtering.
4. **Shopping Cart Page:** Displays current items in the cart (stored in Session), allowing checkout.
5. **User Profile / Order History Page:** Shows user details and past orders.
6. **Admin Dashboard (Backend):** Manage products, view all orders, and manage users.

## 4. Database Beans & Relations (JPA - at least 4 tables)
1. **User (Entity):** Stores user credentials and details.
   * *Relations:* One-to-Many with `Order`.
2. **Product (Entity):** Stores product details (name, price, stock).
   * *Relations:* One-to-Many with `OrderItem`.
3. **Order (Entity):** Stores order metadata (date, total price, status).
   * *Relations:* Many-to-One with `User`, One-to-Many with `OrderItem`.
4. **OrderItem (Entity):** Represents a specific product in an order.
   * *Relations:* Many-to-One with `Order`, Many-to-One with `Product`.

## 5. Division of Tasks for 2 Students (חלוקת עבודה ל-2 סטודנטים)

### Student 1 (e.g., David)
* **Infrastructure & DB:** Setup Spring Boot project, configure MySQL (`ex4`), define JPA Entities (`User`, `Product`, `Order`, `OrderItem`) and their relations.
* **Security:** Implement Spring Security (Login/Logout/Registration).
* **Pages:** Home Page, Login/Registration Pages, User Profile Page.
* **Logic:** User service, basic authentication logic.

### Student 2 (e.g., Partner)
* **Core Business Logic:** Implement Shopping Cart using HTTP Sessions.
* **Admin Backend:** Create the Admin dashboard to manage products and view orders.
* **Pages:** Product Catalog & Search Page, Shopping Cart Page, Admin Dashboard.
* **Logic:** Product service, order processing logic, advanced search functionality.

*(Note: Both students will participate in the final 12-minute demo recording, presenting their respective parts).*
