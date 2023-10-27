package com.jango.socialmediaapi.controller;

import com.jango.socialmediaapi.dto.CommentDTO;
import com.jango.socialmediaapi.dto.PostDTO;
import com.jango.socialmediaapi.dto.response.ApiResponse;
import com.jango.socialmediaapi.dto.response.CommentResponseDto;
import com.jango.socialmediaapi.dto.response.PostResponseDTO;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;


    @PostMapping
    public ResponseEntity<ApiResponse<PostResponseDTO>> createPost(@RequestBody PostDTO postDto) throws ServiceException {
        PostResponseDTO post = postService.createPost(postDto);
        ApiResponse<PostResponseDTO> response = new ApiResponse<>(HttpStatus.CREATED.value(), "Post created successfully", post);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostResponseDTO>>> getAllPosts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder) {
        Page<PostResponseDTO> postsPage = postService.getAllPosts(page, size, sortBy, sortOrder);
        ApiResponse<Page<PostResponseDTO>> response = new ApiResponse<>(HttpStatus.OK.value(), "Posts retrieved successfully", postsPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDTO>>  getPost(@PathVariable Long postId) throws ServiceException {
        try {
            PostResponseDTO post = postService.getPost(postId);
            ApiResponse<PostResponseDTO> response = new ApiResponse<>(HttpStatus.OK.value(), "Post retrieved successfully", post);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ServiceException e) {
            ApiResponse<PostResponseDTO> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Post not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{postId}/{userId}")
    public ResponseEntity<ApiResponse<String>> deletePost(@PathVariable Long postId, @PathVariable Long userId) {
        try {
            postService.deletePost(postId, userId);
            ApiResponse<String> response = new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "Post deleted successfully", null);
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        } catch (ServiceException e) {
            ApiResponse<String> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Post not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDTO>> updatePost(@PathVariable Long postId, @RequestBody PostDTO postDto) {
        try {
            PostResponseDTO updatedPost = postService.updatePost(postId, postDto);
            ApiResponse<PostResponseDTO> response = new ApiResponse<>(HttpStatus.OK.value(), "Post updated successfully", updatedPost);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ServiceException e) {
            ApiResponse<PostResponseDTO> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Post not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{postId}/like/{userId}")
    public ResponseEntity<ApiResponse<PostResponseDTO>> likePost(@PathVariable Long postId, @PathVariable Long userId) throws ServiceException {
        PostResponseDTO post = postService.likePost(postId, userId);
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), "Post liked successfully", post), HttpStatus.OK);
    }

    @PostMapping("/{postId}/comment/{userId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> commentOnPost(@PathVariable Long postId, @PathVariable Long userId, @RequestBody CommentDTO commentDto) throws ServiceException {
        CommentResponseDto comment = postService.commentOnPost(postId, userId, commentDto);
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), "Comment added successfully", comment), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PostResponseDTO>>> searchAndFilterPosts(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "sortOrder", defaultValue = "asc") String sortOrder) {
        List<PostResponseDTO> filteredPosts = postService.searchAndFilterPosts(keyword, userId, sortOrder);
        ApiResponse<List<PostResponseDTO>> response = new ApiResponse<>(HttpStatus.OK.value(), "Posts retrieved successfully", filteredPosts);
        return ResponseEntity.ok(response);
    }

}
