package com.example.videopipeline.domain.job.service;

import com.example.videopipeline.domain.job.entity.JobType;
import com.example.videopipeline.domain.job.entity.ProcessingJob;
import com.example.videopipeline.domain.job.exception.InvalidJobTransitionException;
import com.example.videopipeline.domain.job.exception.StaleJobAttemptException;
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
        } catch (InvalidJobTransitionException e) {
            log.info("실행 가능 상태가 아니라 건너뜀: jobId={}, type={}", job.getId(), job.getType());
            return;
        }
        try {
            String filePath = videoService.getFilePath(job.getVideoId());
            processors.get(job.getType()).process(job.getVideoId(), filePath);
        } catch (Exception e) {
            log.error("job 실행 실패: jobId={}, type={}", job.getId(), job.getType(), e);
            record(job, () -> jobService.markFailed(job.getId(), e.getMessage(), attempt));
            return;
        }
        record(job, () -> jobService.markSucceeded(job.getId(), attempt));
    }

    // 둘 다 좀비 판정 후 만료된 시도의 의도된 거부 — 그 외 예외는 잡지 않고 드러낸다
    private void record(ProcessingJob job, Runnable recording) {
        try {
            recording.run();
        } catch (StaleJobAttemptException | InvalidJobTransitionException e) {
            log.warn("결과 기록 거부됨: jobId={}, type={}, 사유={}", job.getId(), job.getType(), e.getMessage());
        }
    }
}
