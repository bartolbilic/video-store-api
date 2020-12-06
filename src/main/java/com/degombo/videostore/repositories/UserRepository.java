package com.degombo.videostore.repositories;

import com.degombo.videostore.models.entities.User;
import com.degombo.videostore.models.projections.UserProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
    @Query(value = "SELECT * FROM user", nativeQuery = true)
    List<UserProjection> findAllProjected();
}
