package com.jango.socialmediaapi.service.impl;

import com.jango.socialmediaapi.dto.CommentDTO;
import com.jango.socialmediaapi.dto.PostDTO;
import com.jango.socialmediaapi.entity.Comment;
import com.jango.socialmediaapi.entity.Post;
import com.jango.socialmediaapi.entity.User;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.repository.PostRepository;
import com.jango.socialmediaapi.repository.UserRepository;
import com.jango.socialmediaapi.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;


    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Post getPost(Long postId) throws ServiceException {
        return getPostById(postId);
    }

    @Override
    public Post createPost(PostDTO postDto, Long userId) throws ServiceException {
        User user = getUserById(userId);

        Post post = new Post();
        post.setContent(postDto.getContent());
        post.setLikesCount(postDto.getLikesCount());
        post.setUser(user);

        return postRepository.save(post);
    }

    @Override
    public Post updatePost(Long postId, PostDTO postDto) throws ServiceException {
        Post existingPost = getPostById(postId);
        existingPost.setContent(postDto.getContent());
        return postRepository.save(existingPost);
    }

    @Override
    public void deletePost(Long postId) throws ServiceException {
        Post post = getPostById(postId);
        postRepository.delete(post);
    }

    @Override
    public Post likePost(Long postId, Long userId) throws ServiceException {
        Post post = getPostById(postId);
        User user = getUserById(userId);

        post.setLikesCount(post.getLikesCount() + 1);
        postRepository.save(post);

        return post;
    }

    @Override
    public Comment commentOnPost(Long postId, Long userId, CommentDTO commentDto) throws ServiceException {
        Post post = getPostById(postId);
        User user = getUserById(userId);

        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setUser(user);

        post.getComments().add(comment);
        postRepository.save(post);

        return comment;
    }

    private Post getPostById(Long postId) throws ServiceException {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ServiceException("Post not found with id: " + postId));
    }

    private User getUserById(Long userId) throws ServiceException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("User not found with id: " + userId));
    }

}
