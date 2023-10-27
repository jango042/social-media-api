package com.jango.socialmediaapi.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

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
}
