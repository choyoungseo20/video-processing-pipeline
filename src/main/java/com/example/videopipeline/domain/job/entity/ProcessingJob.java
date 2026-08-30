package com.example.videopipeline.domain.job.entity;

import com.example.videopipeline.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "processing_job",
        uniqueConstraints = @UniqueConstraint(columnNames = {"video_id", "type"}),
        indexes = @Index(name = "idx_processing_job_status", columnList = "status"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcessingJob extends BaseEntity {

    private static final int MAX_ATTEMPT_COUNT = 3;

    private static final String INVALID_TRANSITION_MESSAGE = "허용되지 않은 상태 전이: job=%d, %s에서 전이 불가";

    private static final String STALE_ATTEMPT_MESSAGE = "만료된 시도의 기록 거부: job=%d, 시도 %d ≠ 현재 %d";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "video_id", nullable = false)
    private Long videoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    @Column(nullable = false)
    private int attemptCount;

    // 시도별 이력이 아닌 가장 최근 시도 기준 값들
    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    private String lastFailureReason;

    private ProcessingJob(Long videoId, JobType type) {
        this.videoId = videoId;
        this.type = type;
        this.status = JobStatus.PENDING;
        this.attemptCount = 0;
    }

    public static ProcessingJob create(Long videoId, JobType type) {
        return new ProcessingJob(videoId, type);
    }

    public void start() {
        ensureStatusIn(JobStatus.PENDING, JobStatus.FAILED);
        this.attemptCount++;
        this.status = JobStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
        this.finishedAt = null;
    }

    public void succeed(int expectedAttempt) {
        ensureStatusIn(JobStatus.RUNNING);
        ensureCurrentAttempt(expectedAttempt);
        this.status = JobStatus.SUCCEEDED;
        this.finishedAt = LocalDateTime.now();
    }

    public void fail(String reason, int expectedAttempt) {
        ensureStatusIn(JobStatus.RUNNING);
        ensureCurrentAttempt(expectedAttempt);
        this.status = attemptCount >= MAX_ATTEMPT_COUNT ? JobStatus.EXHAUSTED : JobStatus.FAILED;
        this.lastFailureReason = reason;
        this.finishedAt = LocalDateTime.now();
    }

    public boolean isRetryable() {
        return status == JobStatus.FAILED || status == JobStatus.EXHAUSTED;
    }

    public void resetForManualRetry() {
        ensureStatusIn(JobStatus.EXHAUSTED, JobStatus.FAILED);
        this.status = JobStatus.PENDING;
        this.startedAt = null;
        this.finishedAt = null;
        this.lastFailureReason = null;
    }

    // fencing — 좀비로 판정돼 재실행된 job에 옛 시도가 뒤늦게 결과를 쓰는 것을 막는다
    private void ensureCurrentAttempt(int expectedAttempt) {
        if (this.attemptCount != expectedAttempt) {
            throw new IllegalStateException(
                    STALE_ATTEMPT_MESSAGE.formatted(id, expectedAttempt, attemptCount));
        }
    }

    private void ensureStatusIn(JobStatus... allowed) {
        if (!List.of(allowed).contains(this.status)) {
            throw new IllegalStateException(INVALID_TRANSITION_MESSAGE.formatted(id, status));
        }
    }
}
