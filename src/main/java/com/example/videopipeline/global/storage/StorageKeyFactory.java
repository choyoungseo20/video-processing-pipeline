package com.example.videopipeline.global.storage;

import java.util.UUID;
import org.springframework.stereotype.Component;

// 영상 하나의 모든 객체는 videos/{uuid}/ 아래에 모인다
@Component
public class StorageKeyFactory {

    private static final String ORIGINAL_KEY_FORMAT = "videos/%s/original";
    private static final String THUMBNAIL_KEY_NAME = "thumbnail.jpg";

    public String originalKey() {
        return ORIGINAL_KEY_FORMAT.formatted(UUID.randomUUID());
    }

    public String thumbnailKey(String originalKey) {
        return originalKey.substring(0, originalKey.lastIndexOf('/') + 1) + THUMBNAIL_KEY_NAME;
    }
}
