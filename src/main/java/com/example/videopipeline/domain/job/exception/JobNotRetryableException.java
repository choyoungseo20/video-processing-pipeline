package com.example.videopipeline.domain.job.exception;

import com.example.videopipeline.domain.job.entity.JobStatus;
import com.example.videopipeline.global.apipayload.ErrorStatus;
import com.example.videopipeline.global.exception.GeneralException;

public class JobNotRetryableException extends GeneralException {

    public JobNotRetryableException(Long jobId, JobStatus status) {
        super(ErrorStatus.JOB_NOT_RETRYABLE, "재시도 불가 상태: job=%d, status=%s".formatted(jobId, status));
    }
}
