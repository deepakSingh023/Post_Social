package com.example.social_post.util;

import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageCompressor {

    public static byte[] compress(byte[] input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        BufferedImage original = ImageIO.read(new ByteArrayInputStream(input));

        // quality 0.7 but still sharp, non-blurry
        Thumbnails.of(original)
                .scale(1.0)
                .outputQuality(0.7)
                .toOutputStream(baos);

        return baos.toByteArray();
    }
}
