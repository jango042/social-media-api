package com.jango.socialmediaapi.repository;

import com.jango.socialmediaapi.entity.Notification;
import com.jango.socialmediaapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientAndRead(User recipient, boolean read);
}
