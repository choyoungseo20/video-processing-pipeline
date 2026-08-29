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

    // 반환된 시도 번호는 결과 기록 시 fencing 토큰으로 쓰인다
    @Transactional
    public int markStarted(Long jobId) {
        ProcessingJob job = jobRepository.findWithLockById(jobId)
                .orElseThrow(() -> new IllegalStateException(JOB_NOT_FOUND_MESSAGE.formatted(jobId)));
        job.start();
        return job.getAttemptCount();
    }

    @Transactional
    public void markSucceeded(Long jobId, int attempt) {
        findJob(jobId).succeed(attempt);
    }

    @Transactional
    public void markFailed(Long jobId, String reason, int attempt) {
        findJob(jobId).fail(reason, attempt);
    }

    // 폴러 전용 — 잠금 조회로 최신 상태를 읽어, 뒤늦게 커밋되는 결과 기록과의 경합을 차단한다
    @Transactional
    public List<ProcessingJob> failTimedOut(LocalDateTime startedBefore) {
        List<ProcessingJob> zombies =
                jobRepository.findWithLockByStatusAndStartedAtBefore(JobStatus.RUNNING, startedBefore);
        zombies.forEach(job -> job.fail(TIMED_OUT_FAILURE_REASON, job.getAttemptCount()));
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

    // 잠금 조회 — 상태·attemptCount 검사와 갱신이 같은 잠금 아래 원자적으로 수행되게 한다
    private ProcessingJob findJob(Long jobId) {
        return jobRepository.findWithLockById(jobId)
                .orElseThrow(() -> new IllegalStateException(JOB_NOT_FOUND_MESSAGE.formatted(jobId)));
    }
}
