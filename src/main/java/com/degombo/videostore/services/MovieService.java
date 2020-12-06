package com.degombo.videostore.services;

import com.degombo.videostore.models.dtos.MovieDTO;
import com.degombo.videostore.models.entities.Genre;
import com.degombo.videostore.models.entities.Movie;
import com.degombo.videostore.repositories.MovieRepository;
import com.google.common.collect.Lists;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final ModelMapper modelMapper;
    private final GenreService genreService;

    public MovieService(MovieRepository movieRepository, GenreService genreService,
                        ModelMapper modelMapper) {
        this.movieRepository = movieRepository;
        this.modelMapper = modelMapper;
        this.genreService = genreService;
    }

    public List<Movie> findAll(Optional<String> title) {
        return title.map(s -> Lists.newArrayList(movieRepository.findAllByTitle(s)))
                .orElseGet(() -> Lists.newArrayList(movieRepository.findAll()));
    }

    public Movie findById(Long id) {
        return movieRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
    }

    public void save(MovieDTO movieDTO) {
        movieRepository.save(convert(movieDTO));
    }

    public void deleteAll() {
        movieRepository.deleteAll();
    }

    public void deleteById(Long id) {
        Movie movie = movieRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
        movieRepository.deleteById(movie.getId());
    }

    public Movie convert(MovieDTO movieDTO) {
        Set<Genre> genres = movieDTO.getGenres().stream()
                .map(genreService::convert)
                .collect(Collectors.toSet());

        Movie movie = modelMapper.map(movieDTO, Movie.class);
        movie.setGenres(genres);
        return movie;
    }

    public ResponseEntity<Void> updateById(Long id, MovieDTO movieDTO) {
        Movie movie = convert(movieDTO);
        movie.setId(id);
        if (movieRepository.existsById(id)) {
            movieRepository.save(movie);
            return ResponseEntity.status(200).build();
        }

        movieRepository.save(movie);
        return ResponseEntity.status(201).build();
    }
}
