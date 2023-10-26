package com.jango.socialmediaapi.dto;

import com.jango.socialmediaapi.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String username;
    private String email;
    private String profilePicture;
    private Set<User> followers;
    private Set<User> following;
}
