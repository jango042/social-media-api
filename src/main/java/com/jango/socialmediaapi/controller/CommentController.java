package com.jango.socialmediaapi.controller;

import com.jango.socialmediaapi.dto.CommentDTO;
import com.jango.socialmediaapi.dto.response.ApiResponse;
import com.jango.socialmediaapi.dto.response.CommentResponseDto;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/comments")
@SecurityRequirement(name = "jango")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create")
    @Operation(summary = "Create Comment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Users retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
    })
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(@Valid @RequestBody CommentDTO commentDto, @RequestParam Long userId, @RequestParam Long postId) throws ServiceException {
        CommentResponseDto comment = commentService.createComment(commentDto, userId, postId);
        ApiResponse<CommentResponseDto> response = new ApiResponse<>(HttpStatus.CREATED.value(), "Comment created successfully", comment);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all comments")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
    })
    public ResponseEntity<ApiResponse<Page<CommentResponseDto>>> getAllComments(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder
    ) {
        Page<CommentResponseDto> comments = commentService.getAllComments(page, size, sortBy, sortOrder);
        ApiResponse<Page<CommentResponseDto>> response = new ApiResponse<>(HttpStatus.OK.value(), "Comments retrieved successfully", comments);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{commentId}")
    @Operation(summary = "Get comment by id")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found"),
    })
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
    @Operation(summary = "Update Comment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found"),
    })
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
    @Operation(summary = "Delete comment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found"),
    })
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
