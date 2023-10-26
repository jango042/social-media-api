package com.jango.socialmediaapi.service.impl;

import com.jango.socialmediaapi.dto.CommentDTO;
import com.jango.socialmediaapi.dto.response.CommentResponseDto;
import com.jango.socialmediaapi.dto.response.UserResponseDto;
import com.jango.socialmediaapi.entity.Comment;
import com.jango.socialmediaapi.entity.Post;
import com.jango.socialmediaapi.entity.User;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.repository.CommentRepository;
import com.jango.socialmediaapi.repository.PostRepository;
import com.jango.socialmediaapi.repository.UserRepository;
import com.jango.socialmediaapi.service.CommentService;
import com.jango.socialmediaapi.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    @Override
    public CommentResponseDto createComment(CommentDTO commentDto, Long userId, Long postId) throws ServiceException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("User not found with ID: "+ userId));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ServiceException("Post not found with ID: " + postId));

        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setUser(user);
        comment.setPost(post);

        Comment savedComment = commentRepository.save(comment);

        CompletableFuture.runAsync(() -> {
            notificationService.createLikeNotification(user,post);
        });
        return convertToCommentDTO(savedComment);
    }

    @Override
    public Page<CommentResponseDto> getAllComments(int page, int size, String sortBy, String sortOrder) {
        Sort.Direction sortDirection = Sort.Direction.fromString(sortOrder);
        Pageable pageable = PageRequest.of(page, size, sortDirection, sortBy);
        Page<Comment> commentPage = commentRepository.findAll(pageable);

        return commentPage.map(this::convertToCommentDTO);
    }

    @Override
    public CommentResponseDto getCommentById(Long commentId) throws ServiceException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ServiceException("Comment not found with ID: " + commentId));

        return convertToCommentDTO(comment);

    }

    @Override
    public boolean deleteComment(Long commentId, Long userId) throws ServiceException {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ServiceException("Comment not found with ID: " + commentId));
        if (!existingComment.getUser().getId().equals(userId) || !existingComment.getPost().getUser().getId().equals(userId)) {
            throw new ServiceException("You are not the owner of this comment and cannot delete it.");
        }
        commentRepository.delete(existingComment);
        return true;
    }

    @Override
    public CommentResponseDto updateComment(Long commentId, CommentDTO commentDto) throws ServiceException {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ServiceException("Comment not found with ID: " + commentId));
        existingComment.setContent(commentDto.getContent());
        Comment savedComment = commentRepository.save(existingComment);
        return convertToCommentDTO(savedComment);
    }

    public Set<CommentResponseDto> getCommentDTOs(Set<Comment> comments) {
        Set<CommentResponseDto> commentDTOs = new HashSet<>();

        for (Comment comment : comments) {
            CommentResponseDto commentDTO = convertToCommentDTO(comment);
            commentDTOs.add(commentDTO);
        }

        return commentDTOs;
    }

    public CommentResponseDto convertToCommentDTO(Comment comment) {
        CommentResponseDto commentDTO = new CommentResponseDto();
        commentDTO.setContent(comment.getContent());
        commentDTO.setId(commentDTO.getId());
        commentDTO.setUser(new UserResponseDto(comment.getUser()));
        return commentDTO;
    }
}
