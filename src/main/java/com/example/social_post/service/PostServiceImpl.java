package com.example.social_post.service;
import com.example.social_post.dto.PostCreation;
import com.example.social_post.entity.Post;
import com.example.social_post.util.ImageCompressor;
import com.example.social_post.util.VideoCompressor;
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

    private final S3Service s3Service;
    private final PostRepository postRepository;

    @Override
    public Post createPost(String userId, PostCreation dto) throws Exception {

        List<String> imageUrls = new ArrayList<>();
        String videoUrl = null;

        // 🔥 1. IMAGE COMPRESSION (max 7)
        if (dto.getImages() != null) {
            if (dto.getImages().size() > 7)
                throw new IllegalArgumentException("Max 7 images allowed");

            for (MultipartFile img : dto.getImages()) {
                byte[] compressedImg = ImageCompressor.compress(img.getBytes());

                String url = s3Service.uploadBytes(
                        compressedImg,
                        img.getOriginalFilename(),
                        img.getContentType()
                );

                imageUrls.add(url);
            }
        }

        // 🔥 2. VIDEO COMPRESSION + DURATION CHECK
        if (dto.getVideo() != null && !dto.getVideo().isEmpty()) {

            // extract duration using Apache Tika
            long durationSec = extractVideoDuration(dto.getVideo());
            if (durationSec > 40)
                throw new IllegalArgumentException("Video must be <= 40 seconds");

            // compress video using FFmpeg
            byte[] compressedVideo = VideoCompressor.compress(dto.getVideo());

            videoUrl = s3Service.uploadBytes(
                    compressedVideo,
                    dto.getVideo().getOriginalFilename(),
                    dto.getVideo().getContentType()
            );
        }

        // 🔥 3. SAVE POST IN DB
        Post post = Post.builder()
                .userId(userId)
                .caption(dto.getCaption())
                .imageUrls(imageUrls)
                .videoUrl(videoUrl)
                .songUrl(dto.getSongUrl())
                .songName(dto.getSongName())
                .artistName(dto.getArtistName())
                .tags(dto.getTags())
                .isPrivate(dto.isPrivate())
                .createdAt(Instant.now())
                .build();

        return postRepository.save(post);
    }

    @Override
    public void deletePost(String userId, String postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 🔥 Owner check: Only the creator can delete
        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("You are not allowed to delete this post");
        }

        // 🔥 Delete media from R2
        if (post.getImageUrls() != null) {
            for (String url : post.getImageUrls()) {
                s3Service.deleteFile(url);
            }
        }

        if (post.getVideoUrl() != null) {
            s3Service.deleteFile(post.getVideoUrl());
        }

        // 🔥 Delete post from DB
        postRepository.deleteById(postId);
    }


    private long extractVideoDuration(MultipartFile video) throws Exception {
        try (InputStream is = video.getInputStream()) {
            Metadata metadata = new Metadata();
            AutoDetectParser parser = new AutoDetectParser();
            parser.parse(is, new BodyContentHandler(), metadata);

            String dur = metadata.get("xmpDM:duration");
            if (dur == null) return 0;

            return Math.round(Double.parseDouble(dur) / 1000);
        }
    }
}
