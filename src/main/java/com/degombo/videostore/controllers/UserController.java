package com.degombo.videostore.controllers;

import com.degombo.videostore.models.dtos.UserDTO;
import com.degombo.videostore.models.entities.User;
import com.degombo.videostore.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void save(@RequestBody UserDTO userDTO) {
        userService.save(userDTO);
    }
}
