package com.jango.socialmediaapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseWrapper {
    private String username;
    private String email;
    private String profilePicture;
    private List<UserResponseDto> followers;
    private List<UserResponseDto> following;
}
