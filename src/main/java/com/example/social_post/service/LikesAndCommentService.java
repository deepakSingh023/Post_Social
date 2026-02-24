package com.example.social_post.service;


import com.example.social_post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikesAndCommentService {

    private final MongoTemplate mongoTemplate;

    public void incrementLike(String postId){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(postId));

        Update update = new Update();
        update.inc("likes",1);

        mongoTemplate.updateFirst(query,update, Post.class);

    }

    public void incrementComment(String postId){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(postId));

        Update update = new Update();
        update.inc("comments",1);

        mongoTemplate.updateFirst(query,update, Post.class);

    }
    public void decrementLike(String postId){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(postId));

        Update update = new Update();
        update.inc("likes",-1);

        mongoTemplate.updateFirst(query,update, Post.class);

    }

    public void decrementComment(String postId){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(postId));

        Update update = new Update();
        update.inc("comments",-1);

        mongoTemplate.updateFirst(query,update, Post.class);

    }
}
