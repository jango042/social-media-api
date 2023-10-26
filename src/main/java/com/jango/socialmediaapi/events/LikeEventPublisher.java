package com.jango.socialmediaapi.events;

import com.jango.socialmediaapi.entity.Post;
import com.jango.socialmediaapi.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LikeEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishLikeEvent(User liker, Post post) {
        applicationEventPublisher.publishEvent(new LikeEvent(this, liker, post));
    }
}
