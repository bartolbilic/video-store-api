package com.degombo.videostore.repositories;

import com.degombo.videostore.models.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
