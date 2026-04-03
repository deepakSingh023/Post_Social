package com.example.social_post.util;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "like-service")
public interface LikeClient {

    @PostMapping("/likes/bulk-status")
    Map<String, Boolean> getLikedStatus(
            @RequestParam String userId,
            @RequestBody List<String> postIds
    );
}