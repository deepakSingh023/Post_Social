package com.example.social_post.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
public class PostCreation {

    private String caption;

    private List<MultipartFile> images; // Max 5

    private MultipartFile video; // Max 40MB

    private List<String> postTags; // Optional list of tags provided by the user
}