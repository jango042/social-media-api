package com.jango.socialmediaapi.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification extends BaseModel{
    @ManyToOne
    private User recipient;

    @ManyToOne
    private Post post;

    @ManyToOne
    private Comment comment;

    private String message;
    private boolean read;
}
