package com.example.social_post.controller;


import com.example.social_post.service.LikesAndCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/post")
public class IncAndDecController {

    private final LikesAndCommentService likesAndCommentService;


    @PutMapping("/like/inc/{postId}")
    public ResponseEntity<Void> likeInc(
            @PathVariable String postId
    ){

        likesAndCommentService.incrementLike(postId);

        return ResponseEntity.accepted().build();


    }


    @PutMapping("/like/dec/{postId}")
    public ResponseEntity<Void> likeDec(
            @PathVariable String postId
    ){

        likesAndCommentService.decrementLike(postId);

        return ResponseEntity.accepted().build();


    }


    @PutMapping("/comment/inc/{postId}")
    public ResponseEntity<Void> commentInc(
            @PathVariable String postId
    ){

        likesAndCommentService.incrementComment(postId);

        return ResponseEntity.accepted().build();


    }


    @PutMapping("/comment/dec/{postId}")
    public ResponseEntity<Void> commentDec(
            @PathVariable String postId
    ){

        likesAndCommentService.decrementComment(postId);

        return ResponseEntity.accepted().build();


    }
}
