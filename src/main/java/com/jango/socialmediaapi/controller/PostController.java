package com.jango.socialmediaapi.controller;

import com.jango.socialmediaapi.dto.PostDTO;
import com.jango.socialmediaapi.entity.Post;
import com.jango.socialmediaapi.exceptions.ServiceException;
import com.jango.socialmediaapi.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostDTO postDto, @RequestParam Long userId) throws ServiceException {
        Post post = postService.createPost(postDto, userId);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{postId}")
    public Post getPost(@PathVariable Long postId) throws ServiceException {
        return postService.getPostById(postId);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId) throws ServiceException {
        postService.deletePost(postId);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody PostDTO postDto) throws ServiceException {
        Post updatedPost = postService.updatePost(postId, postDto);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

}
