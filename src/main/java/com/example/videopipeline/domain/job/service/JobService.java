package com.example.videopipeline.domain.job.service;

import com.example.videopipeline.domain.job.entity.JobStatus;
import com.example.videopipeline.domain.job.entity.JobType;
import com.example.videopipeline.domain.job.entity.ProcessingJob;
import com.example.videopipeline.domain.job.repository.ProcessingJobRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 상태 전이를 각각 짧은 트랜잭션으로 커밋해, 처리 도중에도 현재 상태가 조회에 보이게 한다
@Service
@RequiredArgsConstructor
public class JobService {

    private static final String JOB_NOT_FOUND_MESSAGE = "존재하지 않는 job: %d";
    private static final String TIMED_OUT_FAILURE_REASON = "실행 타임아웃 초과 — 서버 중단으로 유실된 시도로 판정";

    private final ProcessingJobRepository jobRepository;

    @Transactional(readOnly = true)
    public List<ProcessingJob> findAllFor(Long videoId) {
        return jobRepository.findByVideoId(videoId);
    }

    @Transactional
    public void createAllFor(Long videoId) {
        Arrays.stream(JobType.values())
                .map(type -> ProcessingJob.create(videoId, type))
                .forEach(jobRepository::save);
    }

    @Transactional
    public void markStarted(Long jobId) {
        jobRepository.findWithLockById(jobId)
                .orElseThrow(() -> new IllegalStateException(JOB_NOT_FOUND_MESSAGE.formatted(jobId)))
                .start();
    }

    @Transactional
    public void markSucceeded(Long jobId) {
        findJob(jobId).succeed();
    }

    @Transactional
    public void markFailed(Long jobId, String reason) {
        findJob(jobId).fail(reason);
    }

    // 폴러 전용
    @Transactional
    public List<ProcessingJob> failTimedOut(LocalDateTime startedBefore) {
        List<ProcessingJob> zombies =
                jobRepository.findByStatusAndStartedAtBefore(JobStatus.RUNNING, startedBefore);
        zombies.forEach(job -> job.fail(TIMED_OUT_FAILURE_REASON));
        return zombies;
    }

    // 폴러 전용
    @Transactional(readOnly = true)
    public List<ProcessingJob> findRecoverable(LocalDateTime pendingCreatedBefore) {
        List<ProcessingJob> recoverable =
                jobRepository.findByStatusAndCreatedAtBefore(JobStatus.PENDING, pendingCreatedBefore);
        recoverable.addAll(jobRepository.findByStatus(JobStatus.FAILED));
        return recoverable;
    }

    private ProcessingJob findJob(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalStateException(JOB_NOT_FOUND_MESSAGE.formatted(jobId)));
    }
}
