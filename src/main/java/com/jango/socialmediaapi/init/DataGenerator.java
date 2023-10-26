package com.jango.socialmediaapi.init;

import com.github.javafaker.Faker;
import com.jango.socialmediaapi.dto.PostDTO;
import com.jango.socialmediaapi.dto.UserDto;
import com.jango.socialmediaapi.entity.Comment;
import com.jango.socialmediaapi.entity.Post;
import com.jango.socialmediaapi.entity.User;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.repository.PostRepository;
import com.jango.socialmediaapi.repository.UserRepository;
import com.jango.socialmediaapi.service.PostService;
import com.jango.socialmediaapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataGenerator implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Autowired
    public DataGenerator(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Override
    public void run(String... args) {

        Faker faker = new Faker();

        // Generate 15 unique users with random names and emails
        Set<String> usedEmails = new HashSet<>();
        for (int i = 0; i < 15; i++) {
            User user = new User();
            String username = faker.name().firstName() + faker.number().numberBetween(100, 999);
            String email = faker.internet().safeEmailAddress();
            while (usedEmails.contains(email)) {
                email = faker.internet().safeEmailAddress();
            }
            usedEmails.add(email);
            user.setUsername(username);
            user.setEmail(email);
            user.setProfilePicture("profile.jpg");
            userRepository.save(user);
        }

        // Generate 15 posts with random content
        for (int i = 0; i < 15; i++) {
            Post post = new Post();
            String content = faker.lorem().sentence(10);
            post.setContent(content);
            post.setLikesCount(0L);

            Long userId = getRandomUserId();
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                post.setUser(user);
                postRepository.save(post);
            }
        }
    }

    private Long getRandomUserId() {
        return (long) (Math.random() * 15 + 1);
    }
}
