package com.jango.socialmediaapi.service;

import com.jango.socialmediaapi.dto.CommentDTO;
import com.jango.socialmediaapi.dto.PostDTO;
import com.jango.socialmediaapi.dto.response.CommentResponseDto;
import com.jango.socialmediaapi.dto.response.PostResponseDTO;
import com.jango.socialmediaapi.entity.Comment;
import com.jango.socialmediaapi.entity.Post;
import com.jango.socialmediaapi.exceptions.ServiceException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {
    Page<PostResponseDTO> getAllPosts(int page, int size, String sortBy, String sortOrder);
    PostResponseDTO getPost(Long postId) throws ServiceException;
    PostResponseDTO createPost(PostDTO postDto) throws ServiceException;
    PostResponseDTO updatePost(Long postId, PostDTO postDto) throws ServiceException;
    void deletePost(Long postId, Long userId) throws ServiceException;
    PostResponseDTO likePost(Long postId, Long userId) throws ServiceException;
    CommentResponseDto commentOnPost(Long postId, Long userId, CommentDTO commentDto) throws ServiceException;
    List<PostResponseDTO> searchAndFilterPosts(String keyword, Long userId, String sortOrder);
}
