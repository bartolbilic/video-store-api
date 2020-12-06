package com.degombo.videostore.repositories;

import com.degombo.videostore.models.entities.Movie;
import com.degombo.videostore.models.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MovieRepository extends CrudRepository<Movie, Long> {
    List<Movie> findAllByTitle(String title);
    List<Movie> findAllByUsersIsContaining(User user);
}
