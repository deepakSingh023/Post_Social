package com.example.social_post.service;
import com.example.social_post.entity.Post;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface PostService {
    Post createPost(String userId, String caption,
                    List<MultipartFile> images,
                    MultipartFile video, List<String>postTags) throws Exception;
}
