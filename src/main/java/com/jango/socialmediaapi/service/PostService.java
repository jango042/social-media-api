package com.jango.socialmediaapi.service;

import com.jango.socialmediaapi.dto.PostDTO;
import com.jango.socialmediaapi.entity.Post;
import com.jango.socialmediaapi.exceptions.ServiceException;

import java.util.List;

public interface PostService {
    List<Post> getAllPosts();
    Post getPostById(Long postId) throws ServiceException;
    Post createPost(PostDTO postDto, Long userId) throws ServiceException;
    Post updatePost(Long postId, PostDTO postDto) throws ServiceException;
    void deletePost(Long postId) throws ServiceException;
}
