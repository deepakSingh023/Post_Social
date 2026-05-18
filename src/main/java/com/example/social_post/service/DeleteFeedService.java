package com.example.social_post.service;


import com.example.social_post.util.FeedClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteFeedService {

    private final FeedClient feedClient;

    @Value("service.secret")
    private String token;

    public void deleteFeed(String postId){

        feedClient.deleteFeedPost(postId,token);
    }
}
