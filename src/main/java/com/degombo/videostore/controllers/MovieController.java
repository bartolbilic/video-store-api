package com.degombo.videostore.controllers;

import com.degombo.videostore.models.dtos.MovieDTO;
import com.degombo.videostore.models.entities.Movie;
import com.degombo.videostore.services.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<Movie> findAll(@RequestParam("title") Optional<String> title) {
        return movieService.findAll(title);
    }

    @GetMapping("/{id}")
    public Movie findById(@PathVariable("id") Long id) {
        return movieService.findById(id);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void save(@RequestBody MovieDTO movie) {
        movieService.save(movie);
    }

    @DeleteMapping
    public void deleteAll() {
        movieService.deleteAll();
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        movieService.deleteById(id);
    }
}
