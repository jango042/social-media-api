package com.jango.socialmediaapi.service;

import com.jango.socialmediaapi.dto.response.UserResponseDto;
import com.jango.socialmediaapi.entity.User;
import com.jango.socialmediaapi.dto.UserDto;
import com.jango.socialmediaapi.exceptions.ServiceException;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUser(Long userId) throws ServiceException;

    User createUser(UserDto user) throws ServiceException;

    User updateUser(UserDto user, Long id) throws ServiceException;

    void deleteUser(Long userId) throws ServiceException;

    User findUserByUsername(String username) throws ServiceException;
    User followUser(Long userId, Long followedUserId) throws ServiceException;
    User unfollowUser(Long userId, Long unfollowedUserId) throws ServiceException;
    List<UserResponseDto> searchAndFilterUsers(String keyword, String sortOrder);

    }
