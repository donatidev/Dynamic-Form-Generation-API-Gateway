package com.example.apigateway;

import com.example.apigateway.model.User;
import com.example.apigateway.model.RequiredField;
import com.example.apigateway.repository.UserRepository;
import com.example.apigateway.repository.RequiredFieldRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, RequiredFieldRepository requiredFieldRepository) {
        return args -> {
            // Create and save users
            List<User> users = Arrays.asList(
                createUser("Nicolas", "Donati", null, null, null, null),
                createUser("Nick", "Johnson", "1990-07-22", "Los Angeles", "Male", null),
                createUser("Nico", "Williams", "1988-11-30", "Chicago", "Male", "456 Oak Ave, Chicago"),
                createUser("Nikolas", "Brown", "1992-05-18", null, "Male", "789 Pine Rd, Houston"),
                createUser("Cole", "Davis", "1987-09-03", "Miami", "Male", null),
                createUser("Nicholas", "Miller", "1993-01-25", "Seattle", null, "321 Cedar Ln, Seattle"),
                createUser("Nicky", "Wilson", "1991-06-11", null, "Male", "654 Birch Dr, Boston"),
                createUser("Nicolás", "Garcia", "1986-12-07", "San Francisco", "Male", "987 Elm St, San Francisco"),
                createUser("Nik", "Martinez", "1994-04-20", "Denver", "Male", null),
                createUser("Nikolai", "Anderson", "1989-08-14", "Phoenix", "Male", "147 Maple Ave, Phoenix")
                );

            userRepository.saveAll(users);

            // Create and save required fields
            String[] fieldNames = {"firstName", "lastName", "birthDate", "birthPlace", "sex", "currentAddress"};
            for (String fieldName : fieldNames) {
                RequiredField requiredField = new RequiredField();
                requiredField.setFieldName(fieldName);
                requiredFieldRepository.save(requiredField);
            }

            System.out.println("Sample data has been loaded with 10 Nicolas variations.");
        };
    }

    private User createUser(String firstName, String lastName, String birthDate, String birthPlace, String sex, String currentAddress) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setBirthDate(birthDate);
        user.setBirthPlace(birthPlace);
        user.setSex(sex);
        user.setCurrentAddress(currentAddress);
        return user;
    }
}