package com.jango.socialmediaapi.service;

import com.jango.socialmediaapi.entity.Comment;
import com.jango.socialmediaapi.entity.Notification;
import com.jango.socialmediaapi.entity.Post;
import com.jango.socialmediaapi.entity.User;

import java.util.List;

public interface NotificationService {
    void createLikeNotification(User recipient, Post post);
    void createCommentNotification(User recipient, Comment comment);
    List<Notification> getUnreadNotifications(User recipient);
}
