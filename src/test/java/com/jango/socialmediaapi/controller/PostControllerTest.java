package com.jango.socialmediaapi.controller;

import com.jango.socialmediaapi.dto.CommentDTO;
import com.jango.socialmediaapi.dto.PostDTO;
import com.jango.socialmediaapi.dto.response.ApiResponse;
import com.jango.socialmediaapi.dto.response.CommentResponseDto;
import com.jango.socialmediaapi.dto.response.PostResponseDTO;
import com.jango.socialmediaapi.dto.response.UserResponseDto;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.service.impl.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

    @InjectMocks
    private PostController postController;

    @Mock
    private PostServiceImpl postService;


    @Test
    public void testCreatePost() throws ServiceException {

        PostDTO postDto = new PostDTO();
        postDto.setContent("Test content");
        postDto.setUserId(1L);

        PostResponseDTO postResponse = new PostResponseDTO();
        postResponse.setContent(postDto.getContent());
        postResponse.setLikesCount(0L);
        postResponse.setUser(createSampleUserResponseDto());
        postResponse.setComments(createSampleCommentResponseDtos());
        when(postService.createPost(postDto)).thenReturn(postResponse);

        ResponseEntity<ApiResponse<PostResponseDTO>> response = postController.createPost(postDto);

        verify(postService, times(1)).createPost(postDto);
        ApiResponse<PostResponseDTO> responseBody = response.getBody();
        assert responseBody != null;
        PostResponseDTO returnedPostResponse = responseBody.getData();
        assert returnedPostResponse != null;

        assert response.getStatusCode() == HttpStatus.CREATED;
        assert responseBody.getStatus() == 201;
        assert responseBody.getMessage().equals("Post created successfully");
        assert returnedPostResponse.getContent().equals("Test content");
    }

    @Test
    public void testGetAllPosts() {

        Page<PostResponseDTO> samplePostsPage = createSamplePostsPage();

        when(postService.getAllPosts(0, 10, "id", "asc")).thenReturn(samplePostsPage);

        ResponseEntity<ApiResponse<Page<PostResponseDTO>>> responseEntity = postController.getAllPosts(0, 10, "id", "asc");
        ApiResponse<Page<PostResponseDTO>> response = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assert response != null;
        assertEquals("Posts retrieved successfully", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(samplePostsPage, response.getData());
    }

    @Test
    public void testGetPost_Success() throws ServiceException {

        Long postId = 1L;

        PostResponseDTO samplePost = new PostResponseDTO();
        samplePost.setContent("Sample Post");
        samplePost.setLikesCount(10L);

        when(postService.getPost(postId)).thenReturn(samplePost);

        ResponseEntity<ApiResponse<PostResponseDTO>> responseEntity = postController.getPost(postId);

        assertEquals(200, responseEntity.getStatusCodeValue());
        ApiResponse<PostResponseDTO> response = responseEntity.getBody();
        assert response != null;
        assertEquals("Post retrieved successfully", response.getMessage());

        PostResponseDTO retrievedPost = response.getData();
        assertEquals("Sample Post", retrievedPost.getContent());
        assertEquals(10, retrievedPost.getLikesCount());

        verify(postService, times(1)).getPost(postId);
    }

    @Test
    public void testGetPost_NotFound() throws ServiceException {

        Long postId = 1L;

        when(postService.getPost(postId)).thenThrow(new ServiceException("Post not found"));

        ResponseEntity<ApiResponse<PostResponseDTO>> responseEntity = postController.getPost(postId);

        assertEquals(404, responseEntity.getStatusCodeValue());
        ApiResponse<PostResponseDTO> response = responseEntity.getBody();
        assert response != null;
        assertEquals("Post not found", response.getMessage());
        assertNull(response.getData());

        verify(postService, times(1)).getPost(postId);
    }

    @Test
    public void testDeletePost_Success() throws ServiceException {

        Long postId = 1L;
        Long userId = 2L;

        doNothing().when(postService).deletePost(postId, userId);

        ResponseEntity<ApiResponse<String>> responseEntity = postController.deletePost(postId, userId);

        assertEquals(204, responseEntity.getStatusCodeValue());
        ApiResponse<String> response = responseEntity.getBody();
        assert response != null;
        assertEquals("Post deleted successfully", response.getMessage());

        verify(postService, times(1)).deletePost(postId, userId);
    }

    @Test
    public void testDeletePost_NotFound() throws ServiceException {

        Long postId = 1L;
        Long userId = 2L;

        doThrow(new ServiceException("Post not found")).when(postService).deletePost(postId, userId);

        ResponseEntity<ApiResponse<String>> responseEntity = postController.deletePost(postId, userId);

        assertEquals(404, responseEntity.getStatusCodeValue());
        ApiResponse<String> response = responseEntity.getBody();
        assert response != null;
        assertEquals("Post not found", response.getMessage());
        assertNull(response.getData());

        verify(postService, times(1)).deletePost(postId, userId);
    }

    @Test
    public void testUpdatePost_Success() throws ServiceException {

        Long postId = 1L;
        PostDTO postDto = new PostDTO();
        postDto.setContent("Test content");
        postDto.setUserId(1L);

        PostResponseDTO updatedPost = createSamplePostResponseDTO();
        when(postService.updatePost(postId, postDto)).thenReturn(updatedPost);

        ResponseEntity<ApiResponse<PostResponseDTO>> responseEntity = postController.updatePost(postId, postDto);

        assertEquals(200, responseEntity.getStatusCodeValue());
        ApiResponse<PostResponseDTO> response = responseEntity.getBody();
        assert response != null;
        assertEquals("Post updated successfully", response.getMessage());
        assertNotNull(response.getData());

        verify(postService, times(1)).updatePost(postId, postDto);
    }

    @Test
    public void testUpdatePost_NotFound() throws ServiceException {

        Long postId = 1L;
        PostDTO postDto = new PostDTO();
        postDto.setContent("Test content");
        postDto.setUserId(1L);

        when(postService.updatePost(postId, postDto)).thenThrow(new ServiceException("Post not found"));

        ResponseEntity<ApiResponse<PostResponseDTO>> responseEntity = postController.updatePost(postId, postDto);

        assertEquals(404, responseEntity.getStatusCodeValue());
        ApiResponse<PostResponseDTO> response = responseEntity.getBody();
        assert response != null;
        assertEquals("Post not found", response.getMessage());
        assertNull(response.getData());

        verify(postService, times(1)).updatePost(postId, postDto);
    }

    @Test
    public void testLikePost_Success() throws ServiceException {
        // Arrange
        Long postId = 1L;
        Long userId = 2L;
        PostResponseDTO samplePostResponseDTO = createSamplePostResponseDTO();

        when(postService.likePost(postId, userId)).thenReturn(samplePostResponseDTO);

        ResponseEntity<ApiResponse<PostResponseDTO>> responseEntity = postController.likePost(postId, userId);

        assertEquals(responseEntity.getStatusCode(),HttpStatus.OK);
        assertNotNull(responseEntity.getBody());
        assertEquals(responseEntity.getBody().getMessage(), "Post liked successfully");
        assertEquals(responseEntity.getBody().getData(), samplePostResponseDTO);
    }

    @Test
    public void testCommentOnPost() throws ServiceException {

        Long postId = 1L;
        Long userId = 2L;

        CommentDTO commentDto = new CommentDTO();
        commentDto.setContent("Sample comment content");

        CommentResponseDto commentResponse = createSampleCommentResponseDto();

        when(postService.commentOnPost(postId, userId, commentDto)).thenReturn(commentResponse);

        ResponseEntity<ApiResponse<CommentResponseDto>> responseEntity = postController.commentOnPost(postId, userId, commentDto);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse<CommentResponseDto> apiResponse = responseEntity.getBody();
        assert apiResponse != null;
        assertEquals(HttpStatus.OK.value(), apiResponse.getStatus());
        assertEquals("Comment added successfully", apiResponse.getMessage());
        assertEquals(commentResponse, apiResponse.getData());
    }

    @Test
    public void testSearchAndFilterPosts() {

        String keyword = "sampleKeyword";
        Long userId = 1L;
        String sortOrder = "asc";

        List<PostResponseDTO> filteredPosts = createSamplePostResponseDTOs();

        when(postService.searchAndFilterPosts(keyword, userId, sortOrder)).thenReturn(filteredPosts);

        ResponseEntity<ApiResponse<List<PostResponseDTO>>> responseEntity = postController.searchAndFilterPosts(keyword, userId, sortOrder);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse<List<PostResponseDTO>> apiResponse = responseEntity.getBody();
        assert apiResponse != null;
        assertEquals(HttpStatus.OK.value(), apiResponse.getStatus());
        assertEquals("Posts retrieved successfully", apiResponse.getMessage());
        assertEquals(filteredPosts, apiResponse.getData());
    }

    private List<PostResponseDTO> createSamplePostResponseDTOs() {
        List<PostResponseDTO> postResponseDTOs = new ArrayList<>();

        PostResponseDTO post1 = new PostResponseDTO();
        post1.setContent("Sample post content 1");
        post1.setLikesCount(10L);
        post1.setUser(createSampleUserResponseDto());
        post1.setComments(createSampleCommentResponseDtos());

        PostResponseDTO post2 = new PostResponseDTO();
        post2.setContent("Sample post content 2");
        post2.setLikesCount(5L);
        post2.setUser(createSampleUserResponseDto());
        post2.setComments(createSampleCommentResponseDtos());

        postResponseDTOs.add(post1);
        postResponseDTOs.add(post2);

        return postResponseDTOs;
    }

    private CommentResponseDto createSampleCommentResponseDto() {
        CommentResponseDto commentResponseDto = new CommentResponseDto();
        commentResponseDto.setContent("Sample comment content");
        commentResponseDto.setId(1L);
        return commentResponseDto;
    }
    private PostResponseDTO createSamplePostResponseDTO() {
        PostResponseDTO postResponseDTO = new PostResponseDTO();
        postResponseDTO.setContent("Sample post content");
        postResponseDTO.setLikesCount(10L);

        return postResponseDTO;
    }


    private Page<PostResponseDTO> createSamplePostsPage() {
        List<PostResponseDTO> samplePosts = new ArrayList<>();

        PostResponseDTO post1 = new PostResponseDTO();
        post1.setContent("Post content one");
        post1.setLikesCount(10L);

        PostResponseDTO post2 = new PostResponseDTO();
        post2.setContent("Post Content two");
        post2.setLikesCount(5L);

        samplePosts.add(post1);
        samplePosts.add(post2);

        return new PageImpl<>(samplePosts, PageRequest.of(0, samplePosts.size()), samplePosts.size());
    }

    private UserResponseDto createSampleUserResponseDto() {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername("ejike");
        userResponseDto.setEmail("ejike@example.com");
        userResponseDto.setProfilePicture("profile.jpg");

        return userResponseDto;
    }

    private Set<CommentResponseDto> createSampleCommentResponseDtos() {
        Set<CommentResponseDto> comments = new HashSet<>();

        CommentResponseDto firstComment = new CommentResponseDto();
        firstComment.setContent("This is the first comment");
        firstComment.setId(1L);

        CommentResponseDto secondComment = new CommentResponseDto();
        secondComment.setContent("This is the second comment");
        secondComment.setId(2L);

        CommentResponseDto thirdComment = new CommentResponseDto();
        thirdComment.setContent("This is the third comment");
        thirdComment.setId(3L);

        comments.add(firstComment);
        comments.add(secondComment);
        comments.add(thirdComment);

        return comments;
    }
}
