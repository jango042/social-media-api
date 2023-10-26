package com.jango.socialmediaapi.controller;

import com.jango.socialmediaapi.dto.response.ApiResponse;
import com.jango.socialmediaapi.entity.User;
import com.jango.socialmediaapi.dto.UserDto;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody UserDto userDto) throws ServiceException {
        User user = userService.createUser(userDto);
        ApiResponse<User> response = new ApiResponse<>(HttpStatus.CREATED.value(), "User created successfully", user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        try {
            User updatedUser = userService.updateUser(userDto, userId);
            ApiResponse<User> response = new ApiResponse<>(HttpStatus.OK.value(), "User updated successfully", updatedUser);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ServiceException e) {
            ApiResponse<User> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            ApiResponse<Void> response = new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "User deleted successfully", null);
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        } catch (ServiceException e) {
            ApiResponse<Void> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
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

}
