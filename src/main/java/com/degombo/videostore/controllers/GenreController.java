package com.degombo.videostore.controllers;

import com.degombo.videostore.models.dtos.GenreDTO;
import com.degombo.videostore.models.entities.Genre;
import com.degombo.videostore.services.GenreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<Genre> findAll(@RequestParam("name") Optional<String> name) {
        return genreService.findAll(name);
    }

    @GetMapping("/{id}")
    public Genre findById(@PathVariable("id") Long id) {
        return genreService.findById(id);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<Void> save(@RequestBody GenreDTO genreDTO) {
        return genreService.save(genreDTO);
    }

    @DeleteMapping
    public void deleteAll() {
        genreService.deleteAll();
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        genreService.deleteById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateById(@PathVariable("id") Long id,
                                           @RequestBody GenreDTO genreDTO) {
        return genreService.updateById(id, genreDTO);
    }
}
