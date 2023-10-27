package com.jango.socialmediaapi.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

  @NotBlank(message = "username cannot be empty")
  private String username;

  @NotBlank(message = "password cannot be empty")
  private String password;

}
