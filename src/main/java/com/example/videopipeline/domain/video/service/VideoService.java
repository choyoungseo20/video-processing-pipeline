package com.example.videopipeline.domain.video.service;

import com.example.videopipeline.domain.job.entity.OverallStatus;
import com.example.videopipeline.domain.job.entity.ProcessingJob;
import com.example.videopipeline.domain.job.service.JobService;
import com.example.videopipeline.domain.video.dto.VideoMetadata;
import com.example.videopipeline.domain.video.dto.VideoStatusResponse;
import com.example.videopipeline.domain.video.dto.VideoUploadResponse;
import com.example.videopipeline.domain.video.entity.Video;
import com.example.videopipeline.domain.video.event.VideoUploaded;
import com.example.videopipeline.domain.video.repository.VideoRepository;
import com.example.videopipeline.global.apipayload.ErrorStatus;
import com.example.videopipeline.global.exception.GeneralException;
import java.util.List;
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

    @Transactional(readOnly = true)
    public String getFilePath(Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.VIDEO_NOT_FOUND))
                .getFilePath();
    }

    @Transactional
    public void applyMetadata(Long videoId, VideoMetadata metadata) {
        videoRepository.updateMetadata(
                videoId,
                metadata.durationSec(),
                metadata.width(),
                metadata.height(),
                metadata.videoCodec(),
                metadata.audioCodec());
    }

    @Transactional
    public void applyThumbnail(Long videoId, String thumbnailPath) {
        videoRepository.updateThumbnailPath(videoId, thumbnailPath);
    }

    @Transactional
    public void applyPlaylist(Long videoId, String playlistPath) {
        videoRepository.updatePlaylistPath(videoId, playlistPath);
    }

    @Transactional(readOnly = true)
    public VideoStatusResponse getStatus(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.VIDEO_NOT_FOUND));
        List<ProcessingJob> jobs = jobService.findAllFor(videoId);
        return VideoStatusResponse.of(video, jobs, OverallStatus.from(jobs));
    }
}
