package com.degombo.videostore.models.dtos;

import lombok.Data;

import java.util.Set;

@Data
public class MovieDTO {
    private String title;
    private String description;
    private Set<GenreDTO> genres;
}
