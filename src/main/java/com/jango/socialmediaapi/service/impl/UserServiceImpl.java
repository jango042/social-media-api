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

    @Override
    public User getUserById(Long userId) throws ServiceException {
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
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            throw new ServiceException("User does not exist.");
        }
        User user = createUserFromDto(userRequest);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) throws ServiceException {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new ServiceException("User does not exist.");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public User findUserByUsername(String username) throws ServiceException {
        User user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null) {
            throw new ServiceException("User does not exist.");
        }
        return user;
    }

    private User createUserFromDto(UserDto userRequest) {
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setProfilePicture(userRequest.getProfilePicture());
        return user;
    }
}
