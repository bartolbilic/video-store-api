package com.degombo.videostore.controllers;

import com.degombo.videostore.models.dtos.AuthRequest;
import com.degombo.videostore.models.dtos.IdDTO;
import com.degombo.videostore.models.dtos.JwtDTO;
import com.degombo.videostore.models.dtos.UserDTO;
import com.degombo.videostore.models.entities.Movie;
import com.degombo.videostore.models.entities.User;
import com.degombo.videostore.models.projections.UserProjection;
import com.degombo.videostore.services.MovieService;
import com.degombo.videostore.services.UserService;
import com.degombo.videostore.utils.JwtTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateById(@PathVariable("id") Long id, @RequestBody UserDTO userDTO) {
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return userService.updateById(id, userDTO);
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

    @DeleteMapping("/{id}/movies")
    public void deleteAllMovies(@PathVariable("id") Long id) {
        userService.deleteAllMovies(userService.findById(id));
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
