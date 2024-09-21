package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.exception.ImageCompressException;
import com.hjdmmm.blog.service.ImageCompressor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
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
    public void compress(File file, int maxWidth) throws ImageCompressException {
        BufferedImage image;
        try {
            image = ImageIO.read(file);
        } catch (Exception e) {
            throw new ImageCompressException(String.format("压缩图片 %s 时，无法将其转换为BufferedImage", file.getName()), e);
        }

        if (image.getWidth() <= maxWidth) {
            return;
        }

        try {
            Thumbnails.of(image).width(maxWidth).toFile(file);
        } catch (Exception e) {
            throw new ImageCompressException(String.format("压缩图片 %s 失败", file.getName()), e);
        }
    }
}
