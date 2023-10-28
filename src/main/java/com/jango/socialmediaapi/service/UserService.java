package com.jango.socialmediaapi.service;

import com.jango.socialmediaapi.dto.LoginRequest;
import com.jango.socialmediaapi.dto.response.JwtResponse;
import com.jango.socialmediaapi.dto.response.UserResponseDto;
import com.jango.socialmediaapi.dto.response.UserResponseWrapper;
import com.jango.socialmediaapi.dto.UserDto;
import com.jango.socialmediaapi.exceptions.ServiceException;

import java.util.List;

public interface UserService {
    List<UserResponseWrapper> getAllUsers();

    UserResponseWrapper getUserById(Long userId) throws ServiceException;

    UserResponseWrapper createUser(UserDto userRequest) throws ServiceException;

    UserResponseWrapper updateUser(UserDto userRequest, Long id) throws ServiceException;

    void deleteUser(Long userId) throws ServiceException;

    UserResponseDto findUserByUsername(String username) throws ServiceException;
    UserResponseDto followUser(Long userId, Long followedUserId) throws ServiceException;
    UserResponseDto unfollowUser(Long userId, Long unfollowedUserId) throws ServiceException;
    List<UserResponseDto> searchAndFilterUsers(String keyword, String sortOrder);
    JwtResponse authenticateUser(LoginRequest loginRequest) throws ServiceException;

    }
