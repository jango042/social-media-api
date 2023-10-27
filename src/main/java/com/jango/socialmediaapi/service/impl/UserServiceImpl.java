package com.jango.socialmediaapi.service.impl;

import com.jango.socialmediaapi.dto.LoginRequest;
import com.jango.socialmediaapi.dto.response.JwtResponse;
import com.jango.socialmediaapi.dto.response.UserResponseDto;
import com.jango.socialmediaapi.entity.Role;
import com.jango.socialmediaapi.entity.User;
import com.jango.socialmediaapi.dto.UserDto;
import com.jango.socialmediaapi.enums.RoleType;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.repository.RoleRepository;
import com.jango.socialmediaapi.repository.UserRepository;
import com.jango.socialmediaapi.security.UserDetailsImpl;
import com.jango.socialmediaapi.service.UserService;
import com.jango.socialmediaapi.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.security.core.GrantedAuthority;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapUserToUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getUserById(Long userId) throws ServiceException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("User not found with id: " + userId));
        return mapUserToUserResponseDto(user);
    }


    @Override
    public UserResponseDto createUser(UserDto userRequest) throws ServiceException {
        try {
            if (userRepository.existsByUsername(userRequest.getUsername())) {
                throw new ServiceException("Username already exists.");
            }
            if (userRepository.existsByEmail(userRequest.getEmail())) {
                throw new ServiceException("Email already exists.");
            }

            User user = createUserFromDto(userRequest);
            User savedUser = userRepository.save(user);

            return mapUserToUserResponseDto(savedUser);
        }catch (ConstraintViolationException ex) {
            Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
            String errorMessage = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining("; "));
            throw new ServiceException(errorMessage);
        }

    }

    @Override
    public UserResponseDto updateUser(UserDto userRequest, Long id) throws ServiceException {
        try {
            User existingUser = getUser(id);
            if (existingUser == null) {
                throw new ServiceException("User does not exist.");
            }
            existingUser.setProfilePicture(userRequest.getProfilePicture());
            existingUser.setUsername(userRequest.getUsername());
            existingUser.setEmail(userRequest.getEmail());
            User updatedUser = userRepository.save(existingUser);
            return mapUserToUserResponseDto(updatedUser);
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
            User user = getUser(userId);
            userRepository.delete(user);
        } catch (ServiceException e) {
            throw new ServiceException("User deletion failed");
        }

    }

    @Override
    public UserResponseDto findUserByUsername(String username) throws ServiceException {
        User user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null) {
            throw new ServiceException("User does not exist.");
        }
        return mapUserToUserResponseDto(user);
    }

    @Override
    public UserResponseDto followUser(Long userId, Long followedUserId) throws ServiceException {
        User user = getUser(userId);
        User followedUser = getUser(followedUserId);

        // Check if the user is already following the followedUser
        if (user.getFollowing().contains(followedUser)) {
            throw new ServiceException("You are already following this user.");
        }

        user.getFollowing().add(followedUser);
        userRepository.save(user);

        return mapUserToUserResponseDto(user);
    }

    @Override
    public UserResponseDto unfollowUser(Long userId, Long unfollowedUserId) throws ServiceException {
        User user = getUser(userId);
        User unfollowedUser = getUser(unfollowedUserId);

        // Check if the user is already following the unfollowedUser
        if (!user.getFollowing().contains(unfollowedUser)) {
            throw new ServiceException("You are not following this user, so you cannot unfollow them.");
        }

        user.getFollowing().remove(unfollowedUser);
        userRepository.save(user);

        return mapUserToUserResponseDto(user);
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

    public JwtResponse authenticateUser(LoginRequest loginRequest) throws ServiceException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            return new JwtResponse(jwt, roles);
        } catch (AuthenticationException e) {
            log.error("Authentication failed: {}", e.getMessage());
            throw new ServiceException("Invalid username or password");
        }
    }

    private User createUserFromDto(UserDto userRequest) {
        Optional<Role> role = roleRepository.findByName(RoleType.USER);

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        if (role.isPresent()) {
            Set<Role> roleSet = new HashSet<>();
            roleSet.add(role.get());
            user.setRoles(roleSet);
        }
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setProfilePicture(userRequest.getProfilePicture());
        return user;
    }
    private User getUser(Long userId) throws ServiceException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("User not found with id: " + userId));
    }

    private UserResponseDto mapUserToUserResponseDto(User user) {
        return new UserResponseDto(user);
    }


}
