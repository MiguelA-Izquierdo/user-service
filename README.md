# User Service

This is a microservice for user management that includes authentication (Auth) features. The service allows for user registration, authentication, and management, following **Domain-Driven Design (DDD)** principles to organize the code into distinct layers, each with clear and separate responsibilities.

## Project Structure

The project is organized according to DDD principles and is divided into the following main modules:

- **auth**: Contains the logic for authentication and authorization (JWT, credential validation).
- **user**: Manages the logic related to users (registration, updating, deletion).
- **_shared**: Common components and services, such as events and exceptions.
- **infrastructure**: Implementation of API, repositories, and other external services.

## Design

The project follows the **Domain-Driven Design (DDD)** pattern, allowing each module to align with the business domain and facilitating the system's maintainability and scalability. The layers are separated into:

- **Application Layer**: Defines the use cases and interactions with services.
- **Domain Layer**: Models the entities, domain services, and business rules.
- **Infrastructure Layer**: Implements API controllers, repositories, and other external dependencies.


