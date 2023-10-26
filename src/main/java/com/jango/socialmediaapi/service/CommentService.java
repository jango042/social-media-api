package com.jango.socialmediaapi.service;

import com.jango.socialmediaapi.dto.CommentDTO;
import com.jango.socialmediaapi.dto.response.CommentResponseDto;
import com.jango.socialmediaapi.entity.Comment;
import com.jango.socialmediaapi.exceptions.ServiceException;

import java.util.List;

public interface CommentService {
    CommentResponseDto createComment(CommentDTO commentDto, Long userId, Long postId) throws ServiceException;
    List<CommentResponseDto> getAllComments();
    CommentResponseDto getCommentById(Long commentId) throws ServiceException;
    boolean deleteComment(Long commentId, Long userId) throws ServiceException;
    CommentResponseDto updateComment(Long commentId, CommentDTO commentDto) throws ServiceException;
}
