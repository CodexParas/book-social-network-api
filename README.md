# Book Social Network - API

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Learning Objectives](#learning-objectives)
- [Getting Started](#getting-started)
- [Contributors](#contributors)
- [Acknowledgments](#acknowledgments)

## Overview

The backend of the Book Social Network is built with Spring Boot 3 and provides RESTful APIs for managing user accounts,
book collections, and community interactions. It handles user registration, secure email validation, book management,
and book borrowing/return functionality with an approval system for returns. The application ensures security using JWT
tokens and adheres to best practices in REST API design.

## Features

- **User Registration**: Create a new user account.
- **Email Validation**: Secure account activation via email.
- **User Authentication**: Secure login using JWT tokens.
- **Book Management**: Create, update, share, and archive books.
- **Book Borrowing**: Verify availability and borrow books.
- **Book Returning**: Return borrowed books.
- **Book Return Approval**: Approve book returns.

## Technologies Used

- Spring Boot 3
- Spring Security 6
- JWT Token Authentication
- Spring Data JPA
- JSR-303 and Spring Validation
- OpenAPI and Swagger UI Documentation
- Docker
- GitHub Actions
- Keycloak

## Learning Objectives

By working on this backend project, you will learn:

- Designing class diagrams from business requirements.
- Implementing JWT-based authentication with Spring Security.
- User registration and account validation via email.
- Utilizing inheritance with Spring Data JPA.
- Implementing service layers and handling application exceptions.
- Object validation using JSR-303 and Spring Validation.
- Handling custom exceptions in a Spring Boot application.
- Implementing pagination and adhering to REST API best practices.
- Configuring Spring Profiles for different environments.
- Documenting APIs with OpenAPI and Swagger UI.
- Dockerizing the backend service.
- Setting up a CI/CD pipeline with GitHub Actions.

## Getting Started

### Prerequisites

- JDK 17 or higher
- Maven 3.6+
- Docker (optional for containerization)
- PostgreSQL (or your preferred database)
- Keycloak (for authentication)

### Installation

1. **Clone the repository:**

   ```bash
   git clone https://github.com/CodexParas/book-social-network-api.git
   cd book-social-network-api
   ```

2. **Set up the database:**

   Configure your PostgreSQL database and update the `application.properties` or `application.yml` file with your
   database credentials.

3. **Run the application:**

   ```bash
   mvn spring-boot:run
   ```

4. **Access the API documentation:**

   Once the application is running, you can access the API documentation at
   `http://localhost:8080/api/v1/swagger-ui/index.html`.

### Dockerization (Optional)

To run the application in a Docker container:

1. **Build the Docker image:**

   ```bash
   docker build -t book-social-network-api .
   ```

2. **Run the Docker container:**

   ```bash
   docker run -p 8080:8080 book-social-network-api
   ```

## Contributors

- [Paras Gupta](https://github.com/CodexParas)

## Acknowledgments

Special thanks to the developers and maintainers of the technologies used in this project.