package com.jango.socialmediaapi.dto.response;

import com.jango.socialmediaapi.dto.CommentDTO;
import com.jango.socialmediaapi.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDTO {
    private String content;
    private Long likesCount;
    private UserResponseDto user;
    private Set<CommentResponseDto> comments;
}
