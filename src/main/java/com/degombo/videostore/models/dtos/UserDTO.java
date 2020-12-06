package com.degombo.videostore.models.dtos;

import lombok.Data;

@Data
public class UserDTO {
    private final String username;
    private final String password;
    private final String firstName;
    private final String lastName;
}
