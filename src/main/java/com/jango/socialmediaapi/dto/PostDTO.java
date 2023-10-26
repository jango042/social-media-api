package com.jango.socialmediaapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {

    private String content;
    @CreationTimestamp
    @JsonFormat(pattern = "yyy-MM-dd HH:mm:ss a")
    private LocalDateTime creationDate;
    private Long likesCount;
    private UserDto user;
}
