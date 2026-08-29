package com.example.videopipeline.domain.video.facade;

import com.example.videopipeline.domain.video.dto.VideoUploadResponse;
import com.example.videopipeline.domain.video.service.VideoService;
import com.example.videopipeline.global.apipayload.ErrorStatus;
import com.example.videopipeline.global.exception.GeneralException;
import com.example.videopipeline.global.storage.S3FileStorage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

// 업로드 흐름의 조율자. S3 업로드는 오래 걸리므로 트랜잭션 밖(여기)에서 먼저 하고,
// DB 작업은 VideoService의 짧은 트랜잭션으로 위임한다.
// 실패 시 어느 시점이든 "파일은 있는데 DB가 없는" 무해한 방향으로만 쓰러진다.
@Component
@RequiredArgsConstructor
public class VideoFacade {

    private static final String ORIGINAL_KEY_FORMAT = "videos/%s/original";

    private final S3FileStorage s3FileStorage;
    private final VideoService videoService;

    public VideoUploadResponse upload(MultipartFile file) {
        validate(file);

        String key = originalKey();
        s3FileStorage.save(file, key);

        return videoService.register(file.getOriginalFilename(), file.getSize(), key);
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new GeneralException(ErrorStatus.VIDEO_EMPTY_FILE);
        }
    }

    private String originalKey() {
        return ORIGINAL_KEY_FORMAT.formatted(UUID.randomUUID());
    }
}
