package com.example.social_post.controller;

import com.example.social_post.dto.PostCreation;
import com.example.social_post.entity.Post;
import com.example.social_post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // ---------------- CREATE POST ----------------

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<Post> createPost(
            Authentication authentication,
            @ModelAttribute PostCreation postCreation
    ) throws Exception {

        String userId = authentication.getName();
        Post post = postService.createPost(userId, postCreation);

        return ResponseEntity.ok(post);
    }

    // ---------------- DELETE POST ----------------

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<String> deletePost(
            Authentication authentication,
            @PathVariable String postId
    ) {

        String userId = authentication.getName(); // owner check
        postService.deletePost(userId, postId);

        return ResponseEntity.ok("Post deleted successfully");
    }
}


