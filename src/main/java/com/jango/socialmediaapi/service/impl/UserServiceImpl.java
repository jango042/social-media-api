package com.jango.socialmediaapi.service.impl;

import com.jango.socialmediaapi.entity.User;
import com.jango.socialmediaapi.dto.UserDto;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.repository.UserRepository;
import com.jango.socialmediaapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("User not found"));
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
        User existingUser = getUserById(id);
        if (existingUser == null) {
            throw new ServiceException("User does not exist.");
        }
        User user = createUserFromDto(userRequest);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) throws ServiceException {
        User user = getUserById(userId);

        userRepository.delete(user);
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

        user.getFollowing().add(followedUser);
        userRepository.save(user);

        return user;
    }

    @Override
    public User unfollowUser(Long userId, Long unfollowedUserId) throws ServiceException {
        User user = getUserById(userId);
        User unfollowedUser = getUserById(unfollowedUserId);

        user.getFollowing().remove(unfollowedUser);
        userRepository.save(user);

        return user;
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
}
