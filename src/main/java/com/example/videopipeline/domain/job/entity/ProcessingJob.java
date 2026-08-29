package com.example.videopipeline.domain.job.entity;

import com.example.videopipeline.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "processing_job",
        uniqueConstraints = @UniqueConstraint(columnNames = {"video_id", "type"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcessingJob extends BaseEntity {

    public static final int MAX_RETRY_COUNT = 3;

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
    private int retryCount;

    // 시도별 이력이 아닌 가장 최근 시도 기준 값들
    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    private String lastFailureReason;

    private ProcessingJob(Long videoId, JobType type) {
        this.videoId = videoId;
        this.type = type;
        this.status = JobStatus.PENDING;
        this.retryCount = 0;
    }

    public static ProcessingJob create(Long videoId, JobType type) {
        return new ProcessingJob(videoId, type);
    }

    public void start() {
        this.status = JobStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
        this.finishedAt = null;
    }

    public void succeed() {
        this.status = JobStatus.SUCCEEDED;
        this.finishedAt = LocalDateTime.now();
    }

    public void fail(String reason) {
        this.retryCount++;
        this.status = retryCount >= MAX_RETRY_COUNT ? JobStatus.EXHAUSTED : JobStatus.FAILED;
        this.lastFailureReason = reason;
        this.finishedAt = LocalDateTime.now();
    }

    // EXHAUSTED 상태에서 사람이 원인을 고친 뒤 수동으로 되살릴 때 사용한다
    public void resetForManualRetry() {
        this.status = JobStatus.PENDING;
        this.retryCount = 0;
        this.lastFailureReason = null;
    }
}
