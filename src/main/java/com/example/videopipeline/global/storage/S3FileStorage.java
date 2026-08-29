package com.example.videopipeline.global.storage;

import com.example.videopipeline.global.apipayload.ErrorStatus;
import com.example.videopipeline.global.config.StorageConfig.StorageProperties;
import com.example.videopipeline.global.exception.GeneralException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileStorage {

    private final S3Client s3;
    private final StorageProperties props;

    @PostConstruct
    void ensureBucket() {
        try {
            s3.headBucket(b -> b.bucket(props.bucket()));
        } catch (NoSuchBucketException e) {
            s3.createBucket(b -> b.bucket(props.bucket()));
        }
    }

    public String save(MultipartFile file, String key) {
        try (InputStream in = file.getInputStream()) {
            s3.putObject(
                    b -> b.bucket(props.bucket()).key(key).contentLength(file.getSize()),
                    RequestBody.fromInputStream(in, file.getSize()));
            return key;
        } catch (IOException | SdkException e) {
            log.warn("파일 저장 실패: key={}", key);
            throw new GeneralException(ErrorStatus.STORAGE_SAVE_FAILURE, e);
        }
    }

    public Path downloadToTemp(String key) {
        try {
            Path target = Files.createTempFile("video-original-", null);
            Files.delete(target);
            s3.getObject(b -> b.bucket(props.bucket()).key(key), target);
            return target;
        } catch (IOException | SdkException e) {
            throw new IllegalStateException("원본 다운로드 실패: key=" + key, e);
        }
    }

    public void deleteQuietly(String key) {
        try {
            s3.deleteObject(b -> b.bucket(props.bucket()).key(key));
        } catch (SdkException e) {
            log.warn("보상 삭제 실패 (고아 객체로 남음): key={}", key, e);
        }
    }
}
