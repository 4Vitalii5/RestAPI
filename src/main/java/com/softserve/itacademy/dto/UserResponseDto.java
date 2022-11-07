package com.softserve.itacademy.dto;

//import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.softserve.itacademy.model.User;
import lombok.Value;

@Value
//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserResponseDto {

    Long id;
    String firstName;
    String lastName;
    String email;
    String role;

    public UserResponseDto(User user) {
        id = user.getId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        email = user.getEmail();
        role = user.getRole().getName();
    }
}