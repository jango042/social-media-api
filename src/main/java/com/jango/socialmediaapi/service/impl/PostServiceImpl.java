package com.jango.socialmediaapi.service.impl;

import com.jango.socialmediaapi.dto.CommentDTO;
import com.jango.socialmediaapi.dto.PostDTO;
import com.jango.socialmediaapi.dto.response.CommentResponseDto;
import com.jango.socialmediaapi.dto.response.PostResponseDTO;
import com.jango.socialmediaapi.dto.response.UserResponseDto;
import com.jango.socialmediaapi.entity.Comment;
import com.jango.socialmediaapi.entity.Post;
import com.jango.socialmediaapi.entity.User;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.repository.PostRepository;
import com.jango.socialmediaapi.repository.UserRepository;
import com.jango.socialmediaapi.service.NotificationService;
import com.jango.socialmediaapi.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentServiceImpl commentService;
    private final NotificationService notificationService;



    @Override
    public Page<PostResponseDTO> getAllPosts(int page, int size, String sortBy, String sortOrder) {
        // Define a Pageable object for pagination and sorting
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy);

        Page<Post> postsPage = postRepository.findAll(pageable);

        return postsPage.map(this::convertToPostDTO);
    }

    @Override
    public PostResponseDTO getPost(Long postId) throws ServiceException {
        Post post = getPostById(postId);
        return convertToPostDTO(post);
    }

    @Override
    public PostResponseDTO createPost(PostDTO postDto) throws ServiceException {
        User user = getUserById(postDto.getUserId());

        Post post = new Post();
        post.setContent(postDto.getContent());
        post.setLikesCount(0L);
        post.setUser(user);

        Post savedPost = postRepository.save(post);

        return convertToPostDTO(savedPost);
    }

    @Override
    public PostResponseDTO updatePost(Long postId, PostDTO postDto) throws ServiceException {
        Post existingPost = getPostById(postId);
        existingPost.setContent(postDto.getContent());
        Post savedPost =  postRepository.save(existingPost);

        return convertToPostDTO(savedPost);
    }

    @Override
    public void deletePost(Long postId, Long userId) throws ServiceException {

        Post post = getPostById(postId);
        if (!post.getUser().getId().equals(userId)) {
            throw new ServiceException("You are not the owner of this post and cannot delete it.");
        }
        postRepository.delete(post);
    }

    @Override
    public PostResponseDTO likePost(Long postId, Long userId) throws ServiceException {
        Post post = getPostById(postId);
        User user = getUserById(userId);

        // Check if the user has already liked the post
        if (post.getLikes().contains(user)) {
            throw new ServiceException("You have already liked this post.");
        }

        post.getLikes().add(user);
        post.setLikesCount(post.getLikesCount() + 1);
        Post savedPost = postRepository.save(post);

        CompletableFuture.runAsync(() -> {
            notificationService.createLikeNotification(user,post);
        });

        return convertToPostDTO(savedPost);

    }

    @Override
    public CommentResponseDto commentOnPost(Long postId, Long userId, CommentDTO commentDto) throws ServiceException {
        Post post = getPostById(postId);
        User user = getUserById(userId);

        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setUser(user);

        post.getComments().add(comment);
        postRepository.save(post);

        return commentService.convertToCommentDTO(comment);
    }

    @Override
    public List<PostResponseDTO> searchAndFilterPosts(String keyword, Long userId, String sortOrder) {
        List<Post> filteredPosts = postRepository.searchAndFilter(keyword, userId, sortOrder);
        return filteredPosts.stream()
                .map(this::convertToPostDTO)
                .collect(Collectors.toList());
    }

    private Post getPostById(Long postId) throws ServiceException {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ServiceException("Post not found with id: " + postId));
    }

    private User getUserById(Long userId) throws ServiceException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("User not found with id: " + userId));
    }

    private PostResponseDTO convertToPostDTO(Post post) {
        // Convert the Post entity to a PostDTO
        PostResponseDTO postDTO = new PostResponseDTO();
        postDTO.setContent(post.getContent());
        postDTO.setLikesCount(post.getLikesCount());
        postDTO.setUser(new UserResponseDto(post.getUser()));
        postDTO.setComments(commentService.getCommentDTOs(post.getComments()));
        return postDTO;
    }

}
