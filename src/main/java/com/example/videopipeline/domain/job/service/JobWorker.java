package com.example.videopipeline.domain.job.service;

import com.example.videopipeline.domain.job.entity.JobType;
import com.example.videopipeline.domain.job.entity.ProcessingJob;
import com.example.videopipeline.domain.job.processor.JobProcessor;
import com.example.videopipeline.domain.video.service.VideoService;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JobWorker {

    private final JobService jobService;
    private final VideoService videoService;
    private final Map<JobType, JobProcessor> processors;

    public JobWorker(JobService jobService, VideoService videoService, List<JobProcessor> processors) {
        this.jobService = jobService;
        this.videoService = videoService;
        this.processors = processors.stream()
                .collect(Collectors.toMap(JobProcessor::supportedType, Function.identity()));
    }

    @Async
    public void execute(ProcessingJob job) {
        int attempt;
        try {
            attempt = jobService.markStarted(job.getId());
        } catch (IllegalStateException e) {
            log.info("실행 가능 상태가 아니라 건너뜀: jobId={}, type={}", job.getId(), job.getType());
            return;
        }
        try {
            String filePath = videoService.getFilePath(job.getVideoId());
            processors.get(job.getType()).process(job.getVideoId(), filePath);
            jobService.markSucceeded(job.getId(), attempt);
        } catch (Exception e) {
            log.error("job 실행 실패: jobId={}, type={}", job.getId(), job.getType(), e);
            jobService.markFailed(job.getId(), e.getMessage(), attempt);
        }
    }
}
