package com.jango.socialmediaapi.controller;

import com.jango.socialmediaapi.dto.LoginRequest;
import com.jango.socialmediaapi.dto.UserDto;
import com.jango.socialmediaapi.dto.response.ApiResponse;
import com.jango.socialmediaapi.dto.response.JwtResponse;
import com.jango.socialmediaapi.dto.response.UserResponseDto;
import com.jango.socialmediaapi.dto.response.UserResponseWrapper;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserServiceImpl userService;

    @Test
    void testGetAllUsers() {

        List<UserResponseWrapper> userResponseList = Collections.singletonList(new UserResponseWrapper());

        when(userService.getAllUsers()).thenReturn(userResponseList);

        ResponseEntity<ApiResponse<List<UserResponseWrapper>>> responseEntity = userController.getAllUsers();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ApiResponse<List<UserResponseWrapper>> response = responseEntity.getBody();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("Users retrieved successfully", response.getMessage());
        assertEquals(userResponseList, response.getData());
    }

    @Test
    void testGetUserById_UserExists() throws ServiceException {
        Long userId = 1L;

        UserResponseWrapper userResponse = new UserResponseWrapper();

        when(userService.getUserById(userId)).thenReturn(userResponse);

        ResponseEntity<ApiResponse<UserResponseWrapper>> responseEntity = userController.getUserById(userId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ApiResponse<UserResponseWrapper> response = responseEntity.getBody();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("User retrieved successfully", response.getMessage());
        assertEquals(userResponse, response.getData());
    }

    @Test
    void testGetUserById_UserNotFound() throws ServiceException {
        Long userId = 1L;

        when(userService.getUserById(userId)).thenThrow(new ServiceException("User not found"));

        ResponseEntity<ApiResponse<UserResponseWrapper>> responseEntity = userController.getUserById(userId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        ApiResponse<UserResponseWrapper> response = responseEntity.getBody();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals("User not found", response.getMessage());
        assertEquals(null, response.getData());
    }

    @Test
    void testCreateUser_UserCreatedSuccessfully() throws ServiceException {

        UserDto userDto = new UserDto();
        userDto.setUsername("james");
        userDto.setEmail("james@example.com");

        UserResponseWrapper createdUser = new UserResponseWrapper();
        createdUser.setUsername("james");
        createdUser.setEmail("james@example.com");

        when(userService.createUser(userDto)).thenReturn(createdUser);

        ResponseEntity<ApiResponse<UserResponseWrapper>> responseEntity = userController.createUser(userDto);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ApiResponse<UserResponseWrapper> response = responseEntity.getBody();
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals("User created successfully", response.getMessage());
        assertEquals(createdUser, response.getData());
    }

    @Test
    public void testUpdateUser() throws ServiceException {

        Long userId = 1L;
        UserDto userDto = new UserDto();
        UserResponseWrapper updatedUserResponse = new UserResponseWrapper();
        updatedUserResponse.setUsername("ejike");
        updatedUserResponse.setEmail("ejike@example.com");
        updatedUserResponse.setProfilePicture("updated-profile.jpg");

        List<UserResponseDto> followers = new ArrayList<>();
        followers.add(new UserResponseDto("micheal", "micheal@example.com", "micheal-profile.jpg"));
        followers.add(new UserResponseDto("daniel", "daniel@example.com", "daniel-profile.jpg"));
        updatedUserResponse.setFollowers(followers);

        List<UserResponseDto> following = new ArrayList<>();
        following.add(new UserResponseDto("jane", "jane@example.com", "jane-profile.jpg"));
        following.add(new UserResponseDto("ngozi", "ngozi@example.com", "ngozi-profile.jpg"));
        updatedUserResponse.setFollowing(following);

        when(userService.updateUser(userDto, userId)).thenReturn(updatedUserResponse);

        ResponseEntity<ApiResponse<UserResponseWrapper>> responseEntity = userController.updateUser(userId, userDto);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ApiResponse<UserResponseWrapper> responseBody = responseEntity.getBody();
        assertEquals(HttpStatus.OK.value(), responseBody.getStatus());
        assertEquals("User updated successfully", responseBody.getMessage());
        assertEquals(updatedUserResponse, responseBody.getData());

        verify(userService).updateUser(userDto, userId);
    }

    @Test
    public void testDeleteUser_Success() throws ServiceException {
        Long userId = 1L;

        doNothing().when(userService).deleteUser(userId);

        ResponseEntity<ApiResponse<String>> response = userController.deleteUser(userId);

        verify(userService, times(1)).deleteUser(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User deleted successfully", response.getBody().getMessage());
    }

    @Test
    public void testFindUserByUsername_Success() throws ServiceException {

        String username = "ejike";
        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setUsername("ngozi");
        userResponse.setEmail("ngozi@example.com");
        userResponse.setProfilePicture("ngozi.jpg");

        when(userService.findUserByUsername(username)).thenReturn(userResponse);

        ResponseEntity<ApiResponse<UserResponseDto>> response = userController.findUserByUsername(username);

        verify(userService, times(1)).findUserByUsername(username);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User retrieved successfully", response.getBody().getMessage());
        assertEquals(userResponse, response.getBody().getData());
    }

    @Test
    public void testFindUserByUsername_UserNotFound() throws ServiceException {

        String username = "nonexistentuser";
        String errorMessage = "User not found";

        when(userService.findUserByUsername(username)).thenThrow(new ServiceException(errorMessage));

        ResponseEntity<ApiResponse<UserResponseDto>> response = userController.findUserByUsername(username);

        verify(userService, times(1)).findUserByUsername(username);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void testFollowUser() throws ServiceException {

        Long userId = 1L;
        Long followedUserId = 2L;

        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setUsername("ejike");

        when(userService.followUser(userId, followedUserId)).thenReturn(userResponse);

        ResponseEntity<ApiResponse<UserResponseDto>> response = userController.followUser(userId, followedUserId);

        verify(userService, times(1)).followUser(userId, followedUserId);
        ApiResponse<UserResponseDto> responseBody = response.getBody();
        assert responseBody != null;
        UserResponseDto returnedUser = responseBody.getData();
        assert returnedUser != null;

        assert response.getStatusCode() == HttpStatus.OK;
        assert returnedUser.getUsername().equals("ejike");
    }

    @Test
    public void testUnfollowUser() throws ServiceException {

        Long userId = 1L;
        Long unfollowedUserId = 2L;

        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setUsername("ejike");

        when(userService.unfollowUser(userId, unfollowedUserId)).thenReturn(userResponse);

        ResponseEntity<ApiResponse<UserResponseDto>> response = userController.unfollowUser(userId, unfollowedUserId);

        verify(userService, times(1)).unfollowUser(userId, unfollowedUserId);
        ApiResponse<UserResponseDto> responseBody = response.getBody();
        assert responseBody != null;
        UserResponseDto returnedUser = responseBody.getData();
        assert returnedUser != null;

        assert response.getStatusCode() == HttpStatus.OK;
        assert returnedUser.getUsername().equals("ejike");
    }

    @Test
    public void testSearchAndFilterUsers() {

        String keyword = "john";
        String sortOrder = "asc";

        List<UserResponseDto> userResponses = new ArrayList<>();
        UserResponseDto user1 = new UserResponseDto();
        user1.setUsername("john_ike");
        userResponses.add(user1);
        UserResponseDto user2 = new UserResponseDto();
        user2.setUsername("jane_smith");
        userResponses.add(user2);

        when(userService.searchAndFilterUsers(keyword, sortOrder)).thenReturn(userResponses);

        ResponseEntity<ApiResponse<List<UserResponseDto>>> response = userController.searchAndFilterUsers(keyword, sortOrder);

        verify(userService, times(1)).searchAndFilterUsers(keyword, sortOrder);
        ApiResponse<List<UserResponseDto>> responseBody = response.getBody();
        assert responseBody != null;
        List<UserResponseDto> returnedUsers = responseBody.getData();
        assert returnedUsers != null;

        assert response.getStatusCode() == HttpStatus.OK;
        assert returnedUsers.size() == 2;
        assert returnedUsers.get(0).getUsername().equals("john_ike");
        assert returnedUsers.get(1).getUsername().equals("jane_smith");
    }

    @Test
    public void testLogin() throws ServiceException {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("ejike");
        loginRequest.setPassword("testpassword");


        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWVrYSIsImlhdCI6MTY5ODQxOTcxMywiZXhwIjoxNjk4NTA2MTEzfQ.ZszGfvI268GhpKSXlzJgnusUmUdUZIoPHnjb4j-WioM";

        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setToken(token);
        jwtResponse.setRoles(List.of("USER"));
        when(userService.authenticateUser(loginRequest)).thenReturn(jwtResponse);

        ResponseEntity<ApiResponse<JwtResponse>> response = userController.login(loginRequest);

        verify(userService, times(1)).authenticateUser(loginRequest);
        ApiResponse<JwtResponse> responseBody = response.getBody();
        assert responseBody != null;
        JwtResponse returnedJwtResponse = responseBody.getData();
        assert returnedJwtResponse != null;

        assert response.getStatusCode() == HttpStatus.OK;
        assert responseBody.getStatus() == 200;
        assert responseBody.getMessage().equals("Authentication successful");
        assert returnedJwtResponse.getToken().equals(token);
        assert returnedJwtResponse.getRoles().size() == 1;
        assert returnedJwtResponse.getRoles().get(0).equals("USER");
    }

}
