package com.example.apigateway.service;

import com.example.apigateway.model.User;
import com.example.apigateway.model.RequiredField;
import com.example.apigateway.repository.UserRepository;
import com.example.apigateway.repository.RequiredFieldRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RequiredFieldRepository requiredFieldRepository;

    public UserService(UserRepository userRepository, RequiredFieldRepository requiredFieldRepository) {
        this.userRepository = userRepository;
        this.requiredFieldRepository = requiredFieldRepository;
    }

    public Map<String, Object> getMissingFields(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<RequiredField> requiredFields = requiredFieldRepository.findAll();

        Map<String, Object> response = new HashMap<>();
        List<String> missingFields = new ArrayList<>();

        for (RequiredField field : requiredFields) {
            String fieldName = field.getFieldName();
            if (getFieldValue(user, fieldName) == null) {
                missingFields.add(fieldName);
            }
        }

        response.put("userId", userId);
        response.put("missingFields", missingFields);
        return response;
    }

    private String getFieldValue(User user, String fieldName) {
        switch (fieldName) {
            case "firstName": return user.getFirstName();
            case "lastName": return user.getLastName();
            case "birthDate": return user.getBirthDate();
            case "birthPlace": return user.getBirthPlace();
            case "sex": return user.getSex();
            case "currentAddress": return user.getCurrentAddress();
            default: return null;
        }
    }

    public void updateUserFields(Long userId, Map<String, String> fields) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        
        fields.forEach((key, value) -> {
            switch (key) {
                case "birthDate": user.setBirthDate(value); break;
                case "birthPlace": user.setBirthPlace(value); break;
                case "sex": user.setSex(value); break;
                case "currentAddress": user.setCurrentAddress(value); break;
            }
        });

        userRepository.save(user);
    }
}