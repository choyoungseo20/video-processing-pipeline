package com.example.videopipeline.domain.video.service;

import com.example.videopipeline.domain.job.service.JobService;
import com.example.videopipeline.domain.video.dto.VideoUploadResponse;
import com.example.videopipeline.domain.video.entity.Video;
import com.example.videopipeline.domain.video.event.VideoUploaded;
import com.example.videopipeline.domain.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final JobService jobService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public VideoUploadResponse register(String originalName, long fileSize, String key) {
        Video video = Video.builder()
                .originalName(originalName)
                .filePath(key)
                .fileSize(fileSize)
                .build();
        videoRepository.save(video);

        jobService.createAllFor(video.getId());

        // 리스너는 AFTER_COMMIT에 실행되므로 트랜잭션 안에서 발행해야 한다
        eventPublisher.publishEvent(VideoUploaded.of(video));

        return VideoUploadResponse.from(video);
    }
}
