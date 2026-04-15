package com.example.social_post.util;

import com.example.social_post.dto.InternalProfile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "profile" , url = "${profile.uri}")
public interface ProfileClient {


    @GetMapping("/api/profiles/get/profile-stuff/{userId}")
        InternalProfile getInternalData(
                @PathVariable String userId
    );

}
