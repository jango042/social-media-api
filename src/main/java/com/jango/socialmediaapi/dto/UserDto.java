package com.jango.socialmediaapi.dto;

import com.jango.socialmediaapi.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username should contain only alphabets and numbers")
    private String username;
    @Email(message = "Invalid email format")
    private String email;
    private String profilePicture;
    private Set<UserDto> followers;
    private Set<UserDto> following;
}
