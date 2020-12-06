package com.degombo.videostore.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;

    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<Genre> genres;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public String getDescription() {
        return description;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
