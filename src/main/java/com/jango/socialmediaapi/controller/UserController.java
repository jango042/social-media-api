package com.jango.socialmediaapi.controller;

import com.jango.socialmediaapi.dto.response.ApiResponse;
import com.jango.socialmediaapi.dto.response.UserResponseDto;
import com.jango.socialmediaapi.entity.User;
import com.jango.socialmediaapi.dto.UserDto;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        ApiResponse<List<User>> response = new ApiResponse<>(HttpStatus.OK.value(), "Users retrieved successfully", users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long userId) throws ServiceException {
        try {
            User user = userService.getUser(userId);
            ApiResponse<User> response = new ApiResponse<>(HttpStatus.OK.value(), "User retrieved successfully", user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ServiceException e) {
            ApiResponse<User> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@Validated @RequestBody UserDto user) throws ServiceException {
        User createdUser = userService.createUser(user);
        ApiResponse<User> response = new ApiResponse<>(200, "User created successfully", createdUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long userId, @RequestBody @Valid UserDto userDto) throws ServiceException {
        User updatedUser = userService.updateUser(userDto, userId);
        ApiResponse<User> response = new ApiResponse<>(HttpStatus.OK.value(), "User updated successfully", updatedUser);
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
    public ResponseEntity<ApiResponse<User>> findUserByUsername(@PathVariable String username) throws ServiceException {
        try {
            User user = userService.findUserByUsername(username);
            ApiResponse<User> response = new ApiResponse<>(HttpStatus.OK.value(), "User retrieved successfully", user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ServiceException e) {
            ApiResponse<User> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{userId}/follow/{followedUserId}")
    public ResponseEntity<ApiResponse<User>> followUser(@PathVariable Long userId, @PathVariable Long followedUserId) throws ServiceException {
        User user = userService.followUser(userId, followedUserId);
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), "User followed successfully", user), HttpStatus.OK);
    }

    @PostMapping("/{userId}/unfollow/{unfollowedUserId}")
    public ResponseEntity<ApiResponse<User>> unfollowUser(@PathVariable Long userId, @PathVariable Long unfollowedUserId) throws ServiceException {
        User user = userService.unfollowUser(userId, unfollowedUserId);
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

}
