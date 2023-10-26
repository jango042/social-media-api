package com.jango.socialmediaapi.service;

import com.jango.socialmediaapi.dto.CommentDTO;
import com.jango.socialmediaapi.entity.Comment;
import com.jango.socialmediaapi.exceptions.ServiceException;

import java.util.List;

public interface CommentService {
    Comment createComment(CommentDTO commentDto, Long userId, Long postId) throws ServiceException;
    List<Comment> getAllComments();
    Comment getCommentById(Long commentId) throws ServiceException;
    boolean deleteComment(Long commentId) throws ServiceException;
    Comment updateComment(Long commentId, CommentDTO commentDto) throws ServiceException;
}
