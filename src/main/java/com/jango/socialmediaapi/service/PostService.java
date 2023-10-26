package com.jango.socialmediaapi.service;

import com.jango.socialmediaapi.dto.CommentDTO;
import com.jango.socialmediaapi.dto.PostDTO;
import com.jango.socialmediaapi.entity.Comment;
import com.jango.socialmediaapi.entity.Post;
import com.jango.socialmediaapi.exceptions.ServiceException;

import java.util.List;

public interface PostService {
    List<Post> getAllPosts();
    Post getPost(Long postId) throws ServiceException;
    Post createPost(PostDTO postDto, Long userId) throws ServiceException;
    Post updatePost(Long postId, PostDTO postDto) throws ServiceException;
    void deletePost(Long postId) throws ServiceException;
    Post likePost(Long postId, Long userId) throws ServiceException;
    Comment commentOnPost(Long postId, Long userId, CommentDTO commentDto) throws ServiceException;
}
