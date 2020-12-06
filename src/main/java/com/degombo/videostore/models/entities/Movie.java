package com.degombo.videostore.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Setter
@Getter
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String title;
    private String description;

    @ManyToMany(mappedBy = "movies")
    private Set<User> users;

    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<Genre> genres;
}
