package com.example.apigateway.client;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ThirdPartyServiceClient {

    public void submitUserData(Map<String, String> userData) {
        // Mock implementation
        System.out.println("Sending data to third-party service: " + userData);
        // Simulate a delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Data sent successfully to third-party service");
    }
}