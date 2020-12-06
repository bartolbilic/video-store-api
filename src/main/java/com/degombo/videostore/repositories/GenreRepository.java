package com.degombo.videostore.repositories;

import com.degombo.videostore.models.entities.Genre;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GenreRepository extends CrudRepository<Genre, Long> {
    List<Genre> findAllByNameIgnoringCase(String name);
}
