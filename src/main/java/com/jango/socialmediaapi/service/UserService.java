package com.jango.socialmediaapi.service;

import com.jango.socialmediaapi.entity.User;
import com.jango.socialmediaapi.entity.UserDto;
import com.jango.socialmediaapi.exceptions.ServiceException;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(Long userId) throws ServiceException;

    User createUser(UserDto user) throws ServiceException;

    User updateUser(UserDto user, Long id) throws ServiceException;

    void deleteUser(Long userId) throws ServiceException;

    User findUserByUsername(String username) throws ServiceException;
}
