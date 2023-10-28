package com.jango.socialmediaapi.controller;

import com.jango.socialmediaapi.dto.CommentDTO;
import com.jango.socialmediaapi.dto.response.ApiResponse;
import com.jango.socialmediaapi.dto.response.CommentResponseDto;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class CommentControllerTest {

    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentServiceImpl commentService;
    private MockMvc mockMvc;


    @Test
    public void testCreateComment() throws ServiceException {

        CommentDTO commentDto = new CommentDTO();
        commentDto.setContent("Sample Comment Content");

        CommentResponseDto expectedCommentResponse = new CommentResponseDto();
        expectedCommentResponse.setId(1L);
        expectedCommentResponse.setContent("Sample Comment Content");

        when(commentService.createComment(any(CommentDTO.class), any(Long.class), any(Long.class)))
                .thenReturn(expectedCommentResponse);

        ResponseEntity<ApiResponse<CommentResponseDto>> response = commentController.createComment(commentDto, 1L, 2L);

        assertEquals(200, response.getStatusCodeValue());

        CommentResponseDto responseDto = Objects.requireNonNull(response.getBody()).getData();
        assertEquals(expectedCommentResponse.getId(), responseDto.getId());

        verify(commentService, times(1)).createComment(any(CommentDTO.class), any(Long.class), any(Long.class));
    }

    @Test
    public void testGetAllComments() {

        Page<CommentResponseDto> commentsPage = createSampleCommentsPage();

        when(commentService.getAllComments(any(Integer.class), any(Integer.class), any(String.class), any(String.class)))
                .thenReturn(commentsPage);

        ResponseEntity<ApiResponse<Page<CommentResponseDto>>> response = commentController.getAllComments(0, 10, "jane", "asc");

        assertEquals(200, response.getStatusCodeValue());

        Page<CommentResponseDto> responsePage = Objects.requireNonNull(response.getBody()).getData();
        assertEquals(commentsPage.getTotalElements(), responsePage.getTotalElements());

        verify(commentService, times(1)).getAllComments(0, 10, "jane", "asc");
    }

    @Test
    public void testGetComment() throws ServiceException {
        CommentResponseDto sampleComment = createSampleComment();

        when(commentService.getCommentById(any(Long.class)))
                .thenReturn(sampleComment);

        ResponseEntity<ApiResponse<CommentResponseDto>> response = commentController.getComment(1L);

        assertEquals(200, response.getStatusCodeValue());

        CommentResponseDto responseComment = Objects.requireNonNull(response.getBody()).getData();
        assertEquals(sampleComment.getId(), responseComment.getId());

        verify(commentService, times(1)).getCommentById(1L);
    }

    @Test
    public void testUpdateComment() throws ServiceException {
        CommentDTO updatedComment = createSampleCommentDTO();

        CommentResponseDto sampleUpdatedComment = createSampleComment();

        when(commentService.updateComment(any(Long.class), any(CommentDTO.class)))
                .thenReturn(sampleUpdatedComment);

        ResponseEntity<ApiResponse<CommentResponseDto>> response = commentController.updateComment(1L, updatedComment);

        assertEquals(200, response.getStatusCodeValue());

        CommentResponseDto responseComment = Objects.requireNonNull(response.getBody()).getData();
        assertEquals(sampleUpdatedComment.getId(), responseComment.getId());

        verify(commentService, times(1)).updateComment(1L, updatedComment);
    }

    @Test
    public void testDeleteComment_Success() throws ServiceException {
        when(commentService.deleteComment(any(Long.class), any(Long.class)))
                .thenReturn(true);

        ResponseEntity<ApiResponse<String>> response = commentController.deleteComment(1L, 2L);

        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    public void testDeleteComment_NotFound() throws ServiceException {
        when(commentService.deleteComment(any(Long.class), any(Long.class)))
                .thenReturn(false);

        ResponseEntity<ApiResponse<String>> response = commentController.deleteComment(1L, 2L);

        assertEquals(404, response.getStatusCodeValue());
    }
    private CommentDTO createSampleCommentDTO() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent("Updated comment content");
        return commentDTO;
    }
    private Page<CommentResponseDto> createSampleCommentsPage() {
        List<CommentResponseDto> commentsList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            CommentResponseDto comment = new CommentResponseDto();
            comment.setId((long) i);
            commentsList.add(comment);
        }

        return new PageImpl<>(commentsList);
    }
    private CommentResponseDto createSampleComment() {
        CommentResponseDto comment = new CommentResponseDto();
        comment.setId(1L);
        comment.setContent("Test comment content");
        return comment;
    }
}
