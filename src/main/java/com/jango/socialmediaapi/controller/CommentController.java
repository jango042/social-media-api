package com.jango.socialmediaapi.controller;

import com.jango.socialmediaapi.dto.CommentDTO;
import com.jango.socialmediaapi.dto.response.ApiResponse;
import com.jango.socialmediaapi.entity.Comment;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.service.CommentService;
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

    @PostMapping
    public ResponseEntity<ApiResponse<Comment>> createComment(@RequestBody CommentDTO commentDto, @RequestParam Long userId, @RequestParam Long postId) throws ServiceException {
        Comment comment = commentService.createComment(commentDto, userId, postId);
        ApiResponse<Comment> response = new ApiResponse<>(HttpStatus.CREATED.value(), "Comment created successfully", comment);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Comment>>> getAllComments() {
        List<Comment> comments = commentService.getAllComments();
        ApiResponse<List<Comment>> response = new ApiResponse<>(HttpStatus.OK.value(), "Comments retrieved successfully", comments);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Comment>> getComment(@PathVariable Long commentId) {
        try {
            Comment comment = commentService.getCommentById(commentId);
            ApiResponse<Comment> response = new ApiResponse<>(HttpStatus.OK.value(), "Comment retrieved successfully", comment);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ServiceException e) {
            ApiResponse<Comment> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Comment not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Comment>> updateComment(@PathVariable Long commentId, @RequestBody CommentDTO commentDto) {
        try {
            Comment updatedComment = commentService.updateComment(commentId, commentDto);
            ApiResponse<Comment> response = new ApiResponse<>(HttpStatus.OK.value(), "Comment updated successfully", updatedComment);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ServiceException e) {
            ApiResponse<Comment> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Comment not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId) throws ServiceException {
        boolean deleted = commentService.deleteComment(commentId);

        if (deleted) {
            ApiResponse<Void> response = new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "Comment deleted successfully", null);
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        } else {
            ApiResponse<Void> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Comment not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
