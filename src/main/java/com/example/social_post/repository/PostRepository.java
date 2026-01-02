package com.example.social_post.repository;

import com.example.social_post.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

    // Custom query method: find all posts for a given userId
    List<Post> findByUserId(String userId);
}
