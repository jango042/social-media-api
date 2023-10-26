package com.jango.socialmediaapi.dto.response;

import com.jango.socialmediaapi.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private String username;
    private String email;
    private String profilePicture;

    public UserResponseDto(User user) {
        this.email = user.getEmail();
        this.profilePicture = user.getProfilePicture();
        this.username = user.getUsername();
    }
}
