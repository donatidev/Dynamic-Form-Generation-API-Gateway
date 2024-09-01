package com.example.apigateway.controller;

import com.example.apigateway.service.UserService;
import com.example.apigateway.client.ThirdPartyServiceClient;

import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final ThirdPartyServiceClient thirdPartyServiceClient;

    public UserController(UserService userService, ThirdPartyServiceClient thirdPartyServiceClient) {
        this.userService = userService;
        this.thirdPartyServiceClient = thirdPartyServiceClient;
    }

    @GetMapping("/{userId}/missing-fields")
    public Map<String, Object> getMissingFields(@PathVariable Long userId) {
        return userService.getMissingFields(userId);
    }

    @PostMapping(value = "/{userId}/submit-form", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> submitForm(@PathVariable Long userId, @RequestBody Map<String, String> formData) {
        try {
            System.out.println("Received form data for user " + userId + ": " + formData);
            userService.updateUserFields(userId, formData);
            thirdPartyServiceClient.submitUserData(formData);
            System.out.println("User data updated successfully");
            return ResponseEntity.ok("Data updated successfully and sent to third-party service");
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            System.err.println("Error updating user data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error occurred: " + e.getMessage());
        }
    }
}