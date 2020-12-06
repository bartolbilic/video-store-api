package com.degombo.videostore.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "genres")
    @JsonIgnore
    private Set<Movie> movies;

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public Set<Movie> getMovies() {
        return movies;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMovies(Set<Movie> movies) {
        this.movies = movies;
    }
}
