package com.degombo.videostore.controllers;

import com.degombo.videostore.models.dtos.AuthRequest;
import com.degombo.videostore.models.dtos.JwtDTO;
import com.degombo.videostore.models.dtos.UserDTO;
import com.degombo.videostore.models.entities.User;
import com.degombo.videostore.services.UserService;
import com.degombo.videostore.utils.JwtTokenUtil;
import org.springframework.http.HttpStatus;
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

    public UserController(UserService userService, AuthenticationManager authenticationManager,
                          JwtTokenUtil jwtTokenUtil, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable("id") Long id) {
        return userService.findById(id);
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

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
