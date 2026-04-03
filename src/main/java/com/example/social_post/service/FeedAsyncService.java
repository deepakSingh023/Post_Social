package com.example.social_post.service;

import com.example.social_post.dto.CreateFeed;
import com.example.social_post.util.PostClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class FeedAsyncService {

    private final PostClient postClient;

    @Async("feedCreate")
    public void createFeed(CreateFeed data , String token){

        postClient.createFeed(token,data);

    }
}
