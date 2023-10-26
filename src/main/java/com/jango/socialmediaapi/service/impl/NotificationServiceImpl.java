package com.jango.socialmediaapi.service.impl;

import com.jango.socialmediaapi.entity.Comment;
import com.jango.socialmediaapi.entity.Notification;
import com.jango.socialmediaapi.entity.Post;
import com.jango.socialmediaapi.entity.User;
import com.jango.socialmediaapi.repository.NotificationRepository;
import com.jango.socialmediaapi.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void createLikeNotification(User recipient, Post post) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setPost(post);
        notification.setMessage(recipient.getUsername() + " liked your post.");
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    @Override
    public void createCommentNotification(User recipient, Comment comment) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setComment(comment);
        notification.setMessage(recipient.getUsername() + " commented on your post.");
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getUnreadNotifications(User recipient) {
        return notificationRepository.findByRecipientAndRead(recipient, false);
    }
}
