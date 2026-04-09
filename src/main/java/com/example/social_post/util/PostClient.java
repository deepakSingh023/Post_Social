package com.example.social_post.util;


import com.example.social_post.dto.CreateFeed;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name="postClient" , url="${post.uri}")
public interface PostClient {

    @PostMapping("/api/feeds/create-feed")
    void createFeed(
            @RequestHeader("X-SECRET-TOKEN") String token,
            @RequestBody CreateFeed data
            );




}
