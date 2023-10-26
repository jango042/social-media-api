package com.jango.socialmediaapi.service.impl;

import com.jango.socialmediaapi.dto.PostDTO;
import com.jango.socialmediaapi.entity.Post;
import com.jango.socialmediaapi.entity.User;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.repository.PostRepository;
import com.jango.socialmediaapi.repository.UserRepository;
import com.jango.socialmediaapi.service.PostService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;


    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Post getPostById(Long postId) throws ServiceException {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ServiceException("Post not found with ID: " + postId));
    }

    @Override
    public Post createPost(PostDTO postDto, Long userId) throws ServiceException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("User not found"));

        Post post = new Post();
        post.setContent(postDto.getContent());
        post.setLikesCount(postDto.getLikesCount());
        post.setUser(user);

        return postRepository.save(post);
    }

    @Override
    public Post updatePost(Long postId, PostDTO postDto) throws ServiceException {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ServiceException("Post not found with ID: " + postId));
        existingPost.setContent(postDto.getContent());
        existingPost.setLikesCount(postDto.getLikesCount());
        return postRepository.save(existingPost);
    }

    @Override
    public void deletePost(Long postId) throws ServiceException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ServiceException("Post not found with ID: " + postId));
        postRepository.delete(post);
    }
}
