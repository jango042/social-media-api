package com.jango.socialmediaapi.service.impl;

import com.jango.socialmediaapi.dto.response.UserResponseDto;
import com.jango.socialmediaapi.entity.User;
import com.jango.socialmediaapi.dto.UserDto;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.repository.UserRepository;
import com.jango.socialmediaapi.service.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }



    public User getUser(Long userId) throws ServiceException {
        try {
            User user = getUserById(userId);
            userRepository.save(user);
            return user;
        } catch (ConstraintViolationException ex) {
            Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
            String errorMessage = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining("; "));
            throw new ServiceException(errorMessage);
        }
    }

    @Override
    public User createUser(UserDto userRequest) throws ServiceException {
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new ServiceException("Username already exists.");
        }
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new ServiceException("Email already exists.");
        }

        User user = createUserFromDto(userRequest);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(UserDto userRequest, Long id) throws ServiceException {
        try {
            User existingUser = getUserById(id);
            if (existingUser == null) {
                throw new ServiceException("User does not exist.");
            }
            existingUser.setProfilePicture(userRequest.getProfilePicture());
            existingUser.setUsername(userRequest.getUsername());
            existingUser.setEmail(userRequest.getEmail());
            return userRepository.save(existingUser);
        } catch (ConstraintViolationException ex) {
            Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
            String errorMessage = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining("; "));
            throw new ServiceException(errorMessage);
        }

    }

    @Override
    public void deleteUser(Long userId) throws ServiceException {
        try {
            User user = getUserById(userId);
            userRepository.delete(user);
        } catch (ServiceException e) {
            throw new ServiceException("User deletion failed");
        }

    }

    @Override
    public User findUserByUsername(String username) throws ServiceException {
        User user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null) {
            throw new ServiceException("User does not exist.");
        }
        return user;
    }

    @Override
    public User followUser(Long userId, Long followedUserId) throws ServiceException {
        User user = getUserById(userId);
        User followedUser = getUserById(followedUserId);

        // Check if the user is already following the followedUser
        if (user.getFollowing().contains(followedUser)) {
            throw new ServiceException("You are already following this user.");
        }

        user.getFollowing().add(followedUser);
        userRepository.save(user);

        return user;
    }

    @Override
    public User unfollowUser(Long userId, Long unfollowedUserId) throws ServiceException {
        User user = getUserById(userId);
        User unfollowedUser = getUserById(unfollowedUserId);

        // Check if the user is already following the unfollowedUser
        if (!user.getFollowing().contains(unfollowedUser)) {
            throw new ServiceException("You are not following this user, so you cannot unfollow them.");
        }

        user.getFollowing().remove(unfollowedUser);
        userRepository.save(user);

        return user;
    }

    @Override
    public List<UserResponseDto> searchAndFilterUsers(String keyword, String sortOrder) {
        List<User> filteredUsers;

        if ("asc".equalsIgnoreCase(sortOrder)) {
            filteredUsers = userRepository.searchAndFilterUsers(keyword);
        } else if ("desc".equalsIgnoreCase(sortOrder)) {
            // No need to change the order as the native query handles descending order
            filteredUsers = userRepository.searchAndFilterUsers(keyword);
        } else {
            filteredUsers = userRepository.searchAndFilterUsers(keyword);
        }

        List<UserResponseDto> userResponseDtos = new ArrayList<>();
        for (User user : filteredUsers) {
            userResponseDtos.add(mapUserToUserResponseDto(user));
        }

        return userResponseDtos;
    }

    private User createUserFromDto(UserDto userRequest) {
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setProfilePicture(userRequest.getProfilePicture());
        return user;
    }
    private User getUserById(Long userId) throws ServiceException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("User not found with id: " + userId));
    }

    private UserResponseDto mapUserToUserResponseDto(User user) {
        return new UserResponseDto(user);
    }
}
