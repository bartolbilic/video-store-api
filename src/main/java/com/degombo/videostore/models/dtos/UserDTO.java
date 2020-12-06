package com.degombo.videostore.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
    private final String username;
    private String password;
    private final String firstName;
    private final String lastName;
}
