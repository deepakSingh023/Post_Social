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

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Post> createPost(
            Authentication authentication,
            @ModelAttribute PostCreation postCreation // use @ModelAttribute for multipart + DTO
    ) throws Exception {

        String userId = authentication.getName(); // from JWT
        Post post = postService.createPost(
                userId,
                postCreation.getCaption(),
                postCreation.getImages(),
                postCreation.getVideo(),
                postCreation.getPostTags()
        );

        return ResponseEntity.ok(post);
    }
}
