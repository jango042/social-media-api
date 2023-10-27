package com.jango.socialmediaapi.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Post extends BaseModel{

    @Column(nullable = false, length = 2000)
    @NotBlank(message = "content is required")
    private String content;
    private Long likesCount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "post")
    @JsonBackReference
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany(mappedBy = "likedPosts")
    private Set<User> likes = new HashSet<>();

    public Set<User> getLikes() {
        return likes;
    }
}
