package com.degombo.videostore.services;

import com.degombo.videostore.models.entities.User;
import com.degombo.videostore.repositories.UserRepository;
import com.google.common.collect.Lists;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return Lists.newArrayList(userRepository.findAll());
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
