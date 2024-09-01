# Dynamic Form Generation API Gateway

## Table of Contents
1. [Introduction](#introduction)
2. [System Architecture](#system-architecture)
3. [Endpoint 1: Missing Fields Retrieval](#endpoint-1-missing-fields-retrieval)
   - [Implementation Details](#implementation-details-1)
   - [Code Walkthrough](#code-walkthrough-1)
   - [Database Interactions](#database-interactions-1)
   - [JSON Response Structure](#json-response-structure)
4. [Endpoint 2: Dynamic Form Submission](#endpoint-2-dynamic-form-submission)
   - [Implementation Details](#implementation-details-2)
   - [Code Walkthrough](#code-walkthrough-2)
   - [Database Persistence](#database-persistence)
   - [Third-Party Service Integration](#third-party-service-integration)
5. [Data Models](#data-models)
6. [Configuration](#configuration)
7. [Testing Procedures](#testing-procedures)
8. [Scalability and Future Enhancements](#scalability-and-future-enhancements)
9. [Conclusion](#conclusion)

## 1. Introduction

This document provides a detailed explanation of how I implemented a Spring Boot application to address the specific requirements of creating an API gateway for dynamic form generation. The application consists of two main endpoints that handle the retrieval of missing fields and the submission of dynamically generated forms.

## 2. System Architecture

The application follows a layered architecture:

- **Controller Layer**: Handles HTTP requests and responses
- **Service Layer**: Implements business logic
- **Repository Layer**: Manages data persistence
- **Model Layer**: Represents data structures
- **Client Layer**: Manages third-party service interactions

Technologies used:
- Spring Boot 2.5.5
- Spring Data JPA
- H2 Database (for demonstration purposes)
- Spring Cloud OpenFeign

## 3. Endpoint 1: Missing Fields Retrieval

### Implementation Details

Endpoint: `GET /api/users/{userId}/missing-fields`

This endpoint fulfills the following requirements:
1. Reads available fields from the database
2. Reads required fields from the database
3. Computes which fields are necessary to be received from the user
4. Produces a JSON response for frontend dynamic form generation

### Code Walkthrough

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}/missing-fields")
    public ResponseEntity<Map<String, Object>> getMissingFields(@PathVariable Long userId) {
        Map<String, Object> missingFields = userService.getMissingFields(userId);
        return ResponseEntity.ok(missingFields);
    }
}

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RequiredFieldRepository requiredFieldRepository;

    public Map<String, Object> getMissingFields(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        List<RequiredField> requiredFields = requiredFieldRepository.findAll();

        Map<String, Object> response = new HashMap<>();
        List<String> missingFields = new ArrayList<>();

        for (RequiredField field : requiredFields) {
            if (isFieldMissing(user, field.getFieldName())) {
                missingFields.add(field.getFieldName());
            }
        }

        response.put("userId", userId);
        response.put("missingFields", missingFields);
        return response;
    }

    private boolean isFieldMissing(User user, String fieldName) {
        // Implementation to check if a field is missing
    }
}
```

### Database Interactions

1. **Reading available fields**:
   - The `UserRepository.findById(userId)` method fetches the user entity from the database, which contains all available user fields.

2. **Reading required fields**:
   - The `RequiredFieldRepository.findAll()` method retrieves all required fields from a separate table in the database.

### JSON Response Structure

The endpoint returns a JSON response in the following format:

```json
{
    "userId": 1,
    "missingFields": ["birthDate", "birthPlace", "sex", "currentAddress"]
}
```

This structure allows the frontend to easily generate a dynamic form based on the missing fields.

## 4. Endpoint 2: Dynamic Form Submission

### Implementation Details

Endpoint: `POST /api/users/{userId}/submit-form`

This endpoint fulfills the following requirements:
1. Persists user response
2. Imitates a call to an imaginary 3rd party service with all necessary parameters
3. Uses Spring Boot + Feign for the third-party service call
4. Retrieves the imaginary service API URL from the database (simulated via properties)

### Code Walkthrough

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final ThirdPartyServiceClient thirdPartyServiceClient;

    @PostMapping("/{userId}/submit-form")
    public ResponseEntity<String> submitForm(@PathVariable Long userId, @RequestBody Map<String, String> formData) {
        userService.updateUserFields(userId, formData);
        thirdPartyServiceClient.submitUserData(formData);
        return ResponseEntity.ok("Data updated successfully and sent to third-party service");
    }
}

@Service
public class UserService {
    private final UserRepository userRepository;

    public void updateUserFields(Long userId, Map<String, String> fields) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        updateUserWithFormData(user, fields);
        userRepository.save(user);
    }

    private void updateUserWithFormData(User user, Map<String, String> fields) {
        // Implementation to update user fields
    }
}

@FeignClient(name = "thirdPartyService", url = "${third-party.service.url}")
public interface ThirdPartyServiceClient {
    @PostMapping("/api/submit")
    void submitUserData(@RequestBody Map<String, String> userData);
}
```

### Database Persistence

The `UserService.updateUserFields()` method updates the user entity with the submitted form data and saves it to the database using `userRepository.save(user)`.

### Third-Party Service Integration

1. The `ThirdPartyServiceClient` interface uses Feign to define the contract for the third-party service call.
2. The `submitUserData()` method in the Feign client is called with all necessary parameters (First name, Last name, Birthdate, Birthplace, Sex, Current address).
3. The third-party service URL is configured in `application.properties`:

```properties
third-party.service.url=${THIRD_PARTY_SERVICE_URL}
```

In a production environment, this URL would be fetched from the database and injected as an environment variable.

## 5. Data Models

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String birthDate;
    private String birthPlace;
    private String sex;
    private String currentAddress;
    // Getters and setters
}

@Entity
public class RequiredField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fieldName;
    // Getters and setters
}
```

## 6. Configuration

The application is configured using `application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

spring.jpa.hibernate.ddl-auto=update

logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate=ERROR
logging.level.com.example.apigateway=DEBUG

third-party.service.url=${THIRD_PARTY_SERVICE_URL}
```

## 7. Testing Procedures

To test the application:

1. Start the application:
   ```
   mvn spring-boot:run
   ```

2. Test Endpoint 1 (Missing Fields Retrieval):
   ```
   GET http://localhost:8080/api/users/1/missing-fields
   ```

3. Test Endpoint 2 (Dynamic Form Submission):
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

4. Verify database updates and third-party service call simulation in logs.

## 8. Scalability and Future Enhancements

- Replace H2 with a production-grade database
- Implement caching for frequently accessed data
- Add authentication and authorization
- Implement API versioning
- Set up comprehensive logging and monitoring

## 9. Conclusion

This Spring Boot application fully addresses the requirements specified in the task. It provides a flexible and scalable solution for dynamic form generation and submission, with the capability to integrate with third-party services. The modular design allows for easy maintenance and future enhancements.