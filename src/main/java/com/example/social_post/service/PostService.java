package com.example.social_post.service;
import com.example.social_post.dto.PostCreation;
import com.example.social_post.entity.Post;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface PostService {
    Post createPost(String userId, PostCreation dto) throws Exception;

    void deletePost(String userId, String postId);

}


