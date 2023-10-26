package com.jango.socialmediaapi.service.impl;

import com.jango.socialmediaapi.dto.CommentDTO;
import com.jango.socialmediaapi.entity.Comment;
import com.jango.socialmediaapi.entity.Post;
import com.jango.socialmediaapi.entity.User;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.repository.CommentRepository;
import com.jango.socialmediaapi.repository.PostRepository;
import com.jango.socialmediaapi.repository.UserRepository;
import com.jango.socialmediaapi.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    @Override
    public Comment createComment(CommentDTO commentDto, Long userId, Long postId) throws ServiceException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("User not found with ID: "+ userId));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ServiceException("Post not found with ID: " + postId));
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setUser(user);
        comment.setPost(post);

        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @Override
    public Comment getCommentById(Long commentId) throws ServiceException {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ServiceException("Comment not found with ID: " + commentId));

    }

    @Override
    public boolean deleteComment(Long commentId) throws ServiceException {
        Comment existingComment = getCommentById(commentId);
        commentRepository.delete(existingComment);
        return true;
    }

    @Override
    public Comment updateComment(Long commentId, CommentDTO commentDto) throws ServiceException {
        Comment existingComment = getCommentById(commentId);
        existingComment.setContent(commentDto.getContent());
        return commentRepository.save(existingComment);
    }
}
