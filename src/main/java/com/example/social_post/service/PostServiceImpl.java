package com.example.social_post.service;
import com.example.social_post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.social_post.repository.PostRepository;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final S3Service s3Service; // helper to talk to Cloudflare R2

    @Override
    public Post createPost(String userId, String caption,
                           List<MultipartFile> images,
                           MultipartFile video,List<String>postTags) throws Exception {

        if (images != null && images.size() > 5) {
            throw new IllegalArgumentException("Max 5 images allowed");
        }

        List<String> imageUrls = new ArrayList<>();
        if (images != null) {
            for (MultipartFile img : images) {
                if (img.getSize() > 5 * 1024 * 1024) { // 5MB limit example
                    throw new IllegalArgumentException("Image size must be <= 5MB");
                }
                String url = s3Service.uploadFile(img);
                imageUrls.add(url);
            }
        }

        List<String> tags = new ArrayList<>();
        if (postTags != null && !postTags.isEmpty()) {
            tags.addAll(postTags);
        }


        String videoUrl = null;
        long duration = 0;
        if (video != null && !video.isEmpty()) {
            if (video.getSize() > 40 * 1024 * 1024) {
                throw new IllegalArgumentException("Video must be <= 40MB");
            }

            // detect duration using Tika
            try (InputStream is = video.getInputStream()) {
                Metadata metadata = new Metadata();
                AutoDetectParser parser = new AutoDetectParser();
                parser.parse(is, new BodyContentHandler(), metadata);

                String dur = metadata.get("xmpDM:duration");
                if (dur != null) {
                    duration = Math.round(Double.parseDouble(dur) / 1000);
                    if (duration > 30) {
                        throw new IllegalArgumentException("Video must be <= 30s");
                    }
                }
            }

            videoUrl = s3Service.uploadFile(video);
        }

        Post post = Post.builder()
                .userId(userId)
                .caption(caption)
                .imageUrls(imageUrls)
                .videoUrl(videoUrl)
                .tags(postTags)
                .createdAt(Instant.now())
                .build();

        return postRepository.save(post);
    }
}
