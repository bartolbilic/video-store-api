package com.degombo.videostore.controllers;

import com.degombo.videostore.models.dtos.*;
import com.degombo.videostore.models.entities.Movie;
import com.degombo.videostore.models.entities.User;
import com.degombo.videostore.models.projections.UserProjection;
import com.degombo.videostore.services.MovieService;
import com.degombo.videostore.services.UserService;
import com.degombo.videostore.utils.JwtTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final MovieService movieService;

    public UserController(UserService userService, AuthenticationManager authenticationManager,
                          JwtTokenUtil jwtTokenUtil, PasswordEncoder passwordEncoder,
                          MovieService movieService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
        this.movieService = movieService;
    }

    @GetMapping
    public List<UserProjection> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserProjection findById(@PathVariable("id") Long id) {
        return userService.findByIdProjected(id);
    }

    @PostMapping("/login")
    public JwtDTO login(@RequestBody AuthRequest authRequest) {
        authenticate(authRequest.getUsername(), authRequest.getPassword());

        final UserDetails userDetails = userService
                .loadUserByUsername(authRequest.getUsername());

        return new JwtDTO(jwtTokenUtil.generateToken(userDetails));
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void save(@RequestBody UserDTO userDTO) {
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userService.save(userDTO);
    }

    @DeleteMapping
    public void deleteAll() {
        userService.deleteAll();
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        userService.deleteById(id);
    }

    @GetMapping("/{id}/movies")
    public List<Movie> getMovies(@PathVariable("id") Long id) {
        User user = userService.findById(id);
        return movieService.findAllByUsersContaining(user);
    }

    @PostMapping("/{id}/movies")
    public void addMovie(@PathVariable("id") Long userId, @RequestBody IdDTO movieDTO) {
        userService.addMovie(userId, movieService.findById(movieDTO.getId()));
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
