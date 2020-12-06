package com.degombo.videostore.controllers;

import com.degombo.videostore.models.dtos.JwtDTO;
import com.degombo.videostore.models.dtos.UserDTO;
import com.degombo.videostore.models.entities.User;
import com.degombo.videostore.services.UserService;
import com.degombo.videostore.utils.JwtTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    public UserController(UserService userService, AuthenticationManager authenticationManager,
                          JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @PostMapping("/authenticate")
    @ResponseStatus(code = HttpStatus.CREATED)
    public JwtDTO save(@RequestBody UserDTO userDTO) {
        authenticate(userDTO.getUsername(), userDTO.getPassword());

        final UserDetails userDetails = userService
                .loadUserByUsername(userDTO.getUsername());

        return new JwtDTO(jwtTokenUtil.generateToken(userDetails));
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
