package com.example.social_post.entity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection="posts")

public class Post {

    @Id
    private String id;

    private String userId;

    private List<String> imageUrls;

    private String videoUrl;

    private String caption;

    private Instant createdAt;

    private int likes = 0;

    private int comments = 0;

    private List<String> tags;

}
