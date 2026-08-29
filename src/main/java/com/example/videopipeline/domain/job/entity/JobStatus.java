package com.example.videopipeline.domain.job.entity;

public enum JobStatus {
    PENDING,    // 실행 대기 — 폴러가 집어간다
    RUNNING,    // 실행 중
    SUCCEEDED,  // 성공 (종료 상태)
    FAILED,     // 실패 — 재시도 상한 전까지 폴러가 다시 집어간다
    EXHAUSTED,  // 재시도 상한 초과 — 수동 재시도로만 되살아난다
}
