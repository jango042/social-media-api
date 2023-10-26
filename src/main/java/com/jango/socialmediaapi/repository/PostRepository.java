package com.jango.socialmediaapi.repository;

import com.jango.socialmediaapi.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p " +
            "WHERE (:keyword IS NULL OR LOWER(p.content) LIKE %:keyword%) " +
            "AND (:userId IS NULL OR p.user.id = :userId) " +
            "ORDER BY CASE WHEN :sortOrder = 'asc' THEN p.id END ASC, " +
            "CASE WHEN :sortOrder = 'desc' THEN p.id END DESC")
    List<Post> searchAndFilter(@Param("keyword") String keyword,
                               @Param("userId") Long userId,
                               @Param("sortOrder") String sortOrder);
}
