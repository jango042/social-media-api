package com.jango.socialmediaapi.repository;

import com.jango.socialmediaapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
