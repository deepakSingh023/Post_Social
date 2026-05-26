package com.example.social_post.util;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "feed-service" , url="${feed.url}")
public interface FeedClient {

    @DeleteMapping("/delete-feed-post")
    void  deleteFeedPost(
            @RequestParam String postId,
            @RequestHeader("X-SECRET-TOKEN") String token

    );
}
