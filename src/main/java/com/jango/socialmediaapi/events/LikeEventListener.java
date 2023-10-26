package com.jango.socialmediaapi.events;

import com.jango.socialmediaapi.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LikeEventListener {
    private final NotificationService notificationService;

    @EventListener
    public void handleLikeEvent(LikeEvent likeEvent) {
        notificationService.createLikeNotification(likeEvent.getLiker(), likeEvent.getPost());
    }
}
