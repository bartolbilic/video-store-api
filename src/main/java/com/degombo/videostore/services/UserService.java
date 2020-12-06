package com.degombo.videostore.services;

import com.degombo.videostore.models.dtos.UserDTO;
import com.degombo.videostore.models.entities.User;
import com.degombo.videostore.repositories.UserRepository;
import com.google.common.collect.Lists;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMappper;

    public UserService(UserRepository userRepository, ModelMapper modelMappper) {
        this.userRepository = userRepository;
        this.modelMappper = modelMappper;
    }

    public List<User> findAll() {
        return Lists.newArrayList(userRepository.findAll());
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public void save(UserDTO userDTO) {
        userRepository.save(convert(userDTO));
    }

    public User convert(UserDTO userDTO) {
        return modelMappper.map(userDTO, User.class);
    }
}
