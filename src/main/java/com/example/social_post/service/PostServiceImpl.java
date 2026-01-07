package com.example.social_post.service;
import com.example.social_post.dto.PostCreation;
import com.example.social_post.entity.Post;
import com.example.social_post.repository.PostRepository;
import com.example.social_post.util.ImageCompressor;
import com.example.social_post.util.VideoCompressor;
import lombok.RequiredArgsConstructor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final S3Service s3Service;
    private final PostRepository postRepository;

    @Override
    public Post createPost(String userId, PostCreation dto) throws Exception {

        List<CompletableFuture<String>> imageFutures = new ArrayList<>();
        String videoUrl = null;

        // ================= IMAGES =================
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {

            if (dto.getImages().size() > 7) {
                throw new IllegalArgumentException("Max 7 images allowed");
            }

            for (MultipartFile img : dto.getImages()) {

                if (img.isEmpty()) continue;

                String contentType = img.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new IllegalArgumentException("Invalid image file");
                }

                byte[] compressedImg = ImageCompressor.compress(img.getBytes());

                imageFutures.add(
                        s3Service.uploadBytesAsync(
                                compressedImg,
                                img.getOriginalFilename(),
                                contentType
                        )
                );
            }
        }

        // ================= VIDEO =================
        if (dto.getVideo() != null && !dto.getVideo().isEmpty()) {

            String contentType = dto.getVideo().getContentType();
            if (contentType == null || !contentType.startsWith("video/")) {
                throw new IllegalArgumentException("Invalid video file");
            }

            long durationSec = extractVideoDuration(dto.getVideo());
            if (durationSec > 40) {
                throw new IllegalArgumentException("Video must be <= 40 seconds");
            }

            byte[] compressedVideo = VideoCompressor.compress(dto.getVideo());

            videoUrl = s3Service.uploadBytesAsync(
                    compressedVideo,
                    dto.getVideo().getOriginalFilename(),
                    "video/mp4"
            ).get(); // wait ONLY for video
        }

        // ================= WAIT FOR IMAGES =================
        List<String> imageUrls = imageFutures.stream()
                .map(f -> {
                    try {
                        return f.get();
                    } catch (Exception e) {
                        throw new RuntimeException("Image upload failed", e);
                    }
                })
                .toList();

        // ================= SAVE POST =================
        Post post = Post.builder()
                .userId(userId)
                .caption(dto.getCaption())
                .imageUrls(imageUrls)
                .videoUrl(videoUrl)
                .username(dto.getUsername())
                .avatar(dto.getAvatar())
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

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("You are not allowed to delete this post");
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        if (post.getImageUrls() != null) {
            for (String url : post.getImageUrls()) {
                futures.add(s3Service.deleteFileAsync(url));
            }
        }

        if (post.getVideoUrl() != null) {
            futures.add(s3Service.deleteFileAsync(post.getVideoUrl()));
        }

        // Wait for all deletions
        futures.forEach(f -> {
            try {
                f.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        postRepository.deleteById(postId);
    }

    @Override
    public List<Post> getPostsByUserId(String userId) {
        return postRepository.findByUserId(userId);
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

