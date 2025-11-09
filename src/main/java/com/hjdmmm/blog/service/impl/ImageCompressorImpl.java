package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.service.ImageCompressor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ImageCompressorImpl implements ImageCompressor {

    private static final Set<MediaType> supportedMediaTypes = Arrays.stream(new MediaType[]{
        MediaType.IMAGE_JPEG,
        MediaType.IMAGE_PNG
    }).collect(Collectors.toSet());

    @Override
    public boolean support(MediaType mediaType) {
        return supportedMediaTypes.contains(mediaType);
    }

    @Override
    public void compress(Path file, int maxWidth) throws Exception {
        BufferedImage image = ImageIO.read(file.toFile());

        if (image.getWidth() <= maxWidth) {
            return;
        }

        Thumbnails.of(image).width(maxWidth).toFile(file.toFile());
    }
}
