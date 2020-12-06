package com.degombo.videostore.services;

import com.degombo.videostore.models.dtos.UserDTO;
import com.degombo.videostore.models.entities.Movie;
import com.degombo.videostore.models.entities.User;
import com.degombo.videostore.models.projections.UserProjection;
import com.degombo.videostore.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public List<UserProjection> findAll() {
        return userRepository.findAllProjected();
    }

    public UserProjection findByIdProjected(Long id) {
        return userRepository.findByIdProjected(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public void save(UserDTO userDTO) {
        userRepository.save(convert(userDTO));
    }

    public User convert(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByUsername(s)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + s + " not found"));
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }

    public void deleteById(Long id) {
        userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));

        userRepository.deleteById(id);
    }

    public void addMovie(Long userId, Movie movie) {
        User user = findById(userId);
        user.getMovies().add(movie);
        userRepository.save(user);
    }
}
