# OpenShop

OpenShop is a RESTful e-commerce API built with Spring Boot. It provides endpoints for managing products, categories, shopping carts, orders, and user authentication.

## Features

- User authentication with JWT
- Product and category management
- Shopping cart functionality
- Order processing
- Admin-specific endpoints

## Getting Started

### Prerequisites

- Java 21
- Maven
- MySQL

### Installation

1. Clone the repository
2. Configure your MySQL database in `application.properties`
3. Run the application:

```bash
mvn spring-boot:run
```

## API Documentation

This project uses Swagger/OpenAPI for API documentation. Once the application is running, you can access the Swagger UI at:

```
http://localhost:8080/swagger-ui/index.html
```

For detailed information about the API documentation, please see the [Swagger Documentation Guide](docs/swagger-documentation.md).

## Technologies Used

- Spring Boot 3.3.8
- Spring Security
- Spring Data JPA
- JWT Authentication
- MySQL
- Lombok
- MapStruct
- Swagger/OpenAPI

## Project Structure

- `controller`: REST API endpoints
- `service`: Business logic
- `repository`: Data access layer
- `entity`: Database models
- `dto`: Data transfer objects
- `config`: Application configuration
- `security`: Authentication and authorization

## License

This project is licensed under the MIT License - see the LICENSE file for details.