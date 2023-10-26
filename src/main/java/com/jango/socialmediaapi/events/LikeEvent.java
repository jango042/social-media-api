package com.jango.socialmediaapi.events;

import com.jango.socialmediaapi.entity.Post;
import com.jango.socialmediaapi.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class LikeEvent extends ApplicationEvent {

    private final User liker;
    private final Post post;

    public LikeEvent(Object source, User liker, Post post) {
        super(source);
        this.liker = liker;
        this.post = post;
    }
}
