package com.example.videopipeline.domain.job.entity;

import java.util.List;

// job 전체를 묶어 본 처리 상태 — DB 컬럼이 아니라 조회 시점에 파생한다
public enum OverallStatus {
    PROCESSING,
    COMPLETED,
    FAILED;

    public static OverallStatus from(List<ProcessingJob> jobs) {
        if (jobs.stream().allMatch(j -> j.getStatus() == JobStatus.SUCCEEDED)) {
            return COMPLETED;
        }
        if (jobs.stream().anyMatch(j -> j.getStatus() == JobStatus.EXHAUSTED)) {
            return FAILED;
        }
        return PROCESSING;
    }
}
