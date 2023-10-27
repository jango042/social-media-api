package com.jango.socialmediaapi.init;

import com.github.javafaker.Faker;
import com.jango.socialmediaapi.entity.Post;
import com.jango.socialmediaapi.entity.User;
import com.jango.socialmediaapi.repository.PostRepository;
import com.jango.socialmediaapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataGenerator implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataGenerator(UserRepository userRepository, PostRepository postRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        Faker faker = new Faker();

        // Generate 15 unique users with random names, emails, and passwords
        for (int i = 0; i < 15; i++) {
            User user = new User();
            String username = faker.name().firstName() + faker.number().numberBetween(100, 999);
            String email = faker.internet().safeEmailAddress();
            String password = generateRandomPassword(8);

            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password)); // Encrypt the password
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

    private String generateRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?";
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }

        return password.toString();
    }
}
