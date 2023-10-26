package com.jango.socialmediaapi.controller;

import com.jango.socialmediaapi.dto.CommentDTO;
import com.jango.socialmediaapi.dto.response.ApiResponse;
import com.jango.socialmediaapi.dto.response.CommentResponseDto;
import com.jango.socialmediaapi.entity.Comment;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.service.CommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(@Valid @RequestBody CommentDTO commentDto, @RequestParam Long userId, @RequestParam Long postId) throws ServiceException {
        CommentResponseDto comment = commentService.createComment(commentDto, userId, postId);
        ApiResponse<CommentResponseDto> response = new ApiResponse<>(200, "Comment created successfully", comment);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponseDto>>> getAllComments() {
        List<CommentResponseDto> comments = commentService.getAllComments();
        ApiResponse<List<CommentResponseDto>> response = new ApiResponse<>(HttpStatus.OK.value(), "Comments retrieved successfully", comments);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> getComment(@PathVariable Long commentId) {
        try {
            CommentResponseDto comment = commentService.getCommentById(commentId);
            ApiResponse<CommentResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(), "Comment retrieved successfully", comment);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ServiceException e) {
            ApiResponse<CommentResponseDto> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Comment not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(@PathVariable Long commentId, @RequestBody CommentDTO commentDto) {
        try {
            CommentResponseDto updatedComment = commentService.updateComment(commentId, commentDto);
            ApiResponse<CommentResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(), "Comment updated successfully", updatedComment);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ServiceException e) {
            ApiResponse<CommentResponseDto> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Comment not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{commentId}/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(@PathVariable Long commentId, @PathVariable Long userId) throws ServiceException {
        boolean deleted = commentService.deleteComment(commentId, userId);

        if (deleted) {
            ApiResponse<String> response = new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "Comment deleted successfully", null);
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        } else {
            ApiResponse<String> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Comment not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
