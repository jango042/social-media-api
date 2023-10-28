package com.jango.socialmediaapi.controller;

import com.jango.socialmediaapi.dto.LoginRequest;
import com.jango.socialmediaapi.dto.response.ApiResponse;
import com.jango.socialmediaapi.dto.response.JwtResponse;
import com.jango.socialmediaapi.dto.response.UserResponseDto;
import com.jango.socialmediaapi.dto.UserDto;
import com.jango.socialmediaapi.dto.response.UserResponseWrapper;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.repository.RoleRepository;
import com.jango.socialmediaapi.repository.UserRepository;
import com.jango.socialmediaapi.service.UserService;
import com.jango.socialmediaapi.utils.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private  final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    private final JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseWrapper>>> getAllUsers() {
        List<UserResponseWrapper> users = userService.getAllUsers();
        ApiResponse<List<UserResponseWrapper>> response = new ApiResponse<>(HttpStatus.OK.value(), "Users retrieved successfully", users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseWrapper>> getUserById(@PathVariable Long userId) throws ServiceException {
        try {
            UserResponseWrapper user = userService.getUserById(userId);
            ApiResponse<UserResponseWrapper> response = new ApiResponse<>(HttpStatus.OK.value(), "User retrieved successfully", user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ServiceException e) {
            ApiResponse<UserResponseWrapper> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseWrapper>> createUser(@Validated @RequestBody UserDto user) throws ServiceException {
        UserResponseWrapper createdUser = userService.createUser(user);
        ApiResponse<UserResponseWrapper> response = new ApiResponse<>(201, "User created successfully", createdUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseWrapper>> updateUser(@PathVariable Long userId, @RequestBody @Valid UserDto userDto) throws ServiceException {
        UserResponseWrapper updatedUser = userService.updateUser(userDto, userId);
        ApiResponse<UserResponseWrapper> response = new ApiResponse<>(HttpStatus.OK.value(), "User updated successfully", updatedUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            ApiResponse<String> response = new ApiResponse<>(200, "User deleted successfully", null);
            return ResponseEntity.ok(response);
        } catch (ServiceException ex) {
            ApiResponse<String> response = new ApiResponse<>(400, ex.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponseDto>> findUserByUsername(@PathVariable String username) throws ServiceException {
        try {
            UserResponseDto user = userService.findUserByUsername(username);
            ApiResponse<UserResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(), "User retrieved successfully", user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ServiceException e) {
            ApiResponse<UserResponseDto> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{userId}/follow/{followedUserId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> followUser(@PathVariable Long userId, @PathVariable Long followedUserId) throws ServiceException {
        UserResponseDto user = userService.followUser(userId, followedUserId);
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), "User followed successfully", user), HttpStatus.OK);
    }

    @PostMapping("/{userId}/unfollow/{unfollowedUserId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> unfollowUser(@PathVariable Long userId, @PathVariable Long unfollowedUserId) throws ServiceException {
        UserResponseDto user = userService.unfollowUser(userId, unfollowedUserId);
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), "User unfollowed successfully", user), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> searchAndFilterUsers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sortOrder", required = false, defaultValue = "asc") String sortOrder
    ) {
        List<UserResponseDto> userResponseDtos = userService.searchAndFilterUsers(keyword, sortOrder);
        ApiResponse<List<UserResponseDto>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "Users found successfully", userResponseDtos
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@RequestBody LoginRequest loginRequest) throws ServiceException {
        JwtResponse jwtResponse = userService.authenticateUser(loginRequest);
        return ResponseEntity.ok(new ApiResponse<>(200, "Authentication successful", jwtResponse));
    }



}
