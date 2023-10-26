package com.jango.socialmediaapi.repository;

import com.jango.socialmediaapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    @Query(value = "SELECT * FROM users u WHERE LOWER(u.username) LIKE %:keyword% ORDER BY u.username DESC", nativeQuery = true)
    List<User> searchAndFilterUsers(@Param("keyword") String keyword);
}
