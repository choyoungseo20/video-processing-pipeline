package com.example.videopipeline.domain.job.service;

import com.example.videopipeline.domain.job.entity.JobType;
import com.example.videopipeline.domain.job.entity.ProcessingJob;
import com.example.videopipeline.domain.job.repository.ProcessingJobRepository;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 상태 전이를 각각 짧은 트랜잭션으로 커밋해, 처리 도중에도 현재 상태가 조회에 보이게 한다
@Service
@RequiredArgsConstructor
public class JobService {

    private final ProcessingJobRepository jobRepository;

    @Transactional(readOnly = true)
    public List<ProcessingJob> findAllFor(Long videoId) {
        return jobRepository.findByVideoId(videoId);
    }

    // 업로드 트랜잭션에 합류(REQUIRED)해 video 저장과 원자적으로 묶인다
    @Transactional
    public void createAllFor(Long videoId) {
        Arrays.stream(JobType.values())
                .map(type -> ProcessingJob.create(videoId, type))
                .forEach(jobRepository::save);
    }

    @Transactional
    public void markStarted(Long jobId) {
        findJob(jobId).start();
    }

    @Transactional
    public void markSucceeded(Long jobId) {
        findJob(jobId).succeed();
    }

    @Transactional
    public void markFailed(Long jobId, String reason) {
        findJob(jobId).fail(reason);
    }

    private ProcessingJob findJob(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 job: " + jobId));
    }
}
