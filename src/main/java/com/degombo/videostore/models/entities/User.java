package com.degombo.videostore.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String password;

    private String firstName;
    private String lastName;
}
