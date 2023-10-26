package com.jango.socialmediaapi.controller;

import com.jango.socialmediaapi.entity.User;
import com.jango.socialmediaapi.dto.UserDto;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) throws ServiceException {
        return userService.getUserById(userId);
    }

    @PostMapping
    public User createUser(@RequestBody UserDto user) throws ServiceException {
        return userService.createUser(user);
    }

    @PutMapping("/{userId}")
    public User updateUser(@PathVariable Long userId, @RequestBody UserDto user) throws ServiceException {
        return userService.updateUser(user, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) throws ServiceException {
        userService.deleteUser(userId);
    }

    @GetMapping("/username/{username}")
    public User findUserByUsername(@PathVariable String username) throws ServiceException {
        return userService.findUserByUsername(username);
    }

}
