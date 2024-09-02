# Dynamic Form Generation API Gateway

## Executive Summary

This project demonstrates a sophisticated approach to dynamic form generation and submission using Spring Boot. It showcases a flexible, scalable, and maintainable solution that addresses complex business requirements while adhering to best practices in software architecture and design.

## Table of Contents
1. [Introduction and Problem Statement](#1-introduction-and-problem-statement)
2. [Architectural Decisions and Rationale](#2-architectural-decisions-and-rationale)
3. [System Architecture](#3-system-architecture)
4. [Core Components](#4-core-components)
   - [Endpoint 1: Missing Fields Retrieval](#endpoint-1-missing-fields-retrieval)
   - [Endpoint 2: Dynamic Form Submission](#endpoint-2-dynamic-form-submission)
5. [Data Modeling and Persistence](#5-data-modeling-and-persistence)
6. [Configuration and Environment Management](#6-configuration-and-environment-management)
7. [Frontend Implementation](#7-frontend-implementation)
8. [Testing Procedures](#8-testing-procedures)
9. [Scalability Considerations and Future Enhancements](#9-scalability-considerations-and-future-enhancements)
10. [Conclusion](#10-conclusion)

## 1. Introduction and Problem Statement

The challenge was to create an API gateway capable of dynamically generating forms based on missing user information and submitting this data to a third-party service. This required a solution that could:

1. Determine missing fields for a given user
2. Generate a dynamic form based on these missing fields
3. Submit the form data and persist it
4. Integrate with an external service

> [!IMPORTANT]
> The complexity lay in creating a flexible system that could adapt to varying user data requirements without frontend changes.

> For more details on the tasks, please see [**`task.txt`**](./task.txt).

## 2. Architectural Decisions and Rationale

### Layered Architecture

- **Controller Layer**: Handles HTTP requests, promoting clean API design.
- **Service Layer**: Encapsulates business logic, allowing for easier unit testing and potential reuse.
- **Repository Layer**: Abstracts data access, enabling flexibility in data source changes.
- **Model Layer**: Represents domain entities, ensuring a clear data structure.
- **Client Layer**: Manages external service interactions, isolating third-party dependencies.

> This approach enhances maintainability and allows for independent scaling of components.

### Choice of H2 Database
For this demonstration, I used H2:

- **Simplicity**: Enables easy setup and testing without external dependencies.
- **In-memory Capability**: Facilitates rapid testing and development cycles.

> [!WARNING]
> In a production environment, this should be replaced with a more robust database solution.

### Frontend Approach
I implemented a simple HTML/JavaScript frontend to demonstrate full-stack integration:

## 3. System Architecture

![System Architecture Diagram](https://github.com/user-attachments/assets/2e417cfb-c689-488c-8f6a-13fc8f929a54)

The system follows a typical Spring Boot application structure with clear separation of concerns:

- **Web Layer**: Handles incoming HTTP requests and response formatting.
- **Service Layer**: Implements core business logic and orchestrates operations.
- **Data Access Layer**: Manages database interactions using Spring Data JPA.
- **External Service Layer**: Manages third-party service interactions using OpenFeign.

## 4. Core Components

### Endpoint 1: Missing Fields Retrieval

This endpoint (`GET /api/users/{userId}/missing-fields`) showcases:

- **Dynamic Query Construction**: Efficiently determines missing fields by comparing available user data against required fields.
- **Flexible Response Structure**: Returns a JSON structure that can easily be consumed by any frontend technology.

Key implementation details:

```java
@GetMapping("/{userId}/missing-fields")
public ResponseEntity<Map<String, Object>> getMissingFields(@PathVariable Long userId) {
    Map<String, Object> missingFields = userService.getMissingFields(userId);
    return ResponseEntity.ok(missingFields);
}
```

> This design allows for easy extension to support different user types or changing field requirements.

### Endpoint 2: Dynamic Form Submission

This endpoint (`POST /api/users/{userId}/submit-form`) demonstrates:

- **Flexible Data Acceptance**: Uses a Map to accept varying form fields without changing the API contract.
- **Transactional Processing**: Ensures data consistency across user updates and third-party submissions.
- **Error Handling**: Implements robust error handling for database and external service failures.

Key implementation:

```java
@PostMapping("/{userId}/submit-form")
public ResponseEntity<String> submitForm(@PathVariable Long userId, @RequestBody Map<String, String> formData) {
    userService.updateUserFields(userId, formData);
    thirdPartyServiceClient.submitUserData(formData);
    return ResponseEntity.ok("Data updated successfully and sent to third-party service");
}
```

> This approach provides flexibility in handling varying form structures and seamless integration with external services.

## 5. Data Modeling and Persistence

The data model is designed for flexibility:

- **User Entity**: Represents core user data with nullable fields to accommodate varying completeness of user profiles.
- **RequiredField Entity**: Allows for dynamic definition of required fields, enabling easy adjustment of form requirements without code changes.

JPA annotations are used for ORM, providing a clean separation between domain models and database schema.

## 6. Configuration and Environment Management

Configuration is externalized using `application.properties`, allowing for easy environment-specific setups:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=update
third-party.service.url=${THIRD_PARTY_SERVICE_URL}
```

> [!IMPORTANT]
> This approach facilitates DevOps practices and simplifies deployment across different environments.

## 7. Frontend Implementation

The frontend demonstrates:

- **Dynamic Form Generation**: Adapts to server-provided missing fields.
- **Asynchronous Communication**: Uses fetch API for smooth user experience.
- **Error Handling**: Provides user feedback for both successful and failed operations.

This implementation serves as a proof-of-concept for integrating the API with any frontend framework.

## 8. Testing Procedures

To fully test the application, including the frontend:

1. Start the Spring Boot application:
   ```
   ./mvnw spring-boot:run
   ```

2. Open a web browser and navigate to `http://localhost:8080` to access the frontend.
   - The form should load automatically, displaying inputs for any missing fields for User ID 1.
   - Fill in the form and submit.
   - Check the browser console for detailed information about the requests and responses.

3. Use Postman or curl to test the backend API directly:

   a. Test Endpoint 1 (Missing Fields Retrieval):
      ```
      GET http://localhost:8080/api/users/1/missing-fields
      ```

   b. Test Endpoint 2 (Dynamic Form Submission):
      ```
      POST http://localhost:8080/api/users/1/submit-form
      Content-Type: application/json

      {
          "birthDate": "1990-01-01",
          "birthPlace": "New York",
          "sex": "Male",
          "currentAddress": "123 Main St, Anytown, USA"
      }
      ```
      
4. Verify database updates and third-party service call simulation in the application logs.

5. After submitting the form via the frontend or direct API call, refresh the frontend page or re-test the missing fields endpoint to confirm that the fields have been updated.

## 9. Scalability Considerations and Future Enhancements

The current design lays a foundation for scalability:

- **Database Scalability**: Easy transition to a distributed database system.
- **API Versioning**: Can be implemented to manage evolving client requirements.
- **Caching Layer**: Introduction of Redis or similar caching solutions for frequently accessed data.
- **Authentication and Authorization**: Implementation of OAuth2 or JWT for secure access.
- **Comprehensive Monitoring**: Integration with tools like Prometheus and Grafana for real-time system insights.

## 10. Conclusion

This project demonstrates a thoughtful approach to solving the dynamic form generation problem. By leveraging Spring Boot's capabilities and adhering to solid architectural principles, I've created a solution that is not only functional but also maintainable and extensible.

Key strengths of this approach include:
- Flexibility in handling varying user data requirements
- Clear separation of concerns for easier maintenance and testing
- Scalable architecture that can grow with business needs

> [!NOTE]
> Areas for potential improvement in a production environment would include more robust error handling, comprehensive logging, and enhanced security measures.
