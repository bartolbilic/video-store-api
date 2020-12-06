package com.degombo.videostore.services;

import com.degombo.videostore.models.dtos.GenreDTO;
import com.degombo.videostore.models.entities.Genre;
import com.degombo.videostore.repositories.GenreRepository;
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

@Service
public class GenreService {
    private final GenreRepository genreRepository;
    private final ModelMapper modelMapper;
    private final MovieRepository movieRepository;

    public GenreService(GenreRepository genreRepository, MovieRepository movieRepository,
                        ModelMapper modelMapper) {
        this.genreRepository = genreRepository;
        this.movieRepository = movieRepository;
        this.modelMapper = modelMapper;
    }

    public List<Genre> findAll(Optional<String> name) {
        return name.map(this::findAllByName)
                .orElseGet(() -> Lists.newArrayList(genreRepository.findAll()));
    }

    public Genre findById(Long id) {
        return genreRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found"));
    }

    public List<Genre> findAllByName(String name) {
        return Lists.newArrayList(genreRepository.findAllByNameIgnoringCase(name));
    }

    public ResponseEntity<Void> save(GenreDTO genreDTO) {
        Genre genre = convert(genreDTO);
        if (genre.getId() != null) {
            return ResponseEntity.ok().build();
        }
        genreRepository.save(convert(genreDTO));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public void deleteAll() {
        genreRepository.findAll().forEach(t -> deleteById(t.getId()));
    }

    public void deleteById(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        genre.getMovies().forEach(t -> {
            Set<Genre> genres = t.getGenres();
            genres.remove(genre);
            movieRepository.save(t);
        });
        genreRepository.deleteById(id);
    }

    public Genre convert(GenreDTO genreDTO) {
        List<Genre> genres = findAllByName(genreDTO.getName());

        if (genres.size() == 0) {
            return modelMapper.map(genreDTO, Genre.class);
        }

        return genres.get(0);
    }

    public ResponseEntity<Void> updateById(Long id, GenreDTO genreDTO) {
        Genre genre = convert(genreDTO);
        genre.setId(id);
        if (genreRepository.existsById(id)) {
            genreRepository.save(genre);
            return ResponseEntity.status(200).build();
        }

        genreRepository.save(genre);
        return ResponseEntity.status(201).build();
    }
}
