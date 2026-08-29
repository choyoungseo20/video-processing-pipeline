package com.example.videopipeline.domain.job.processor;

import com.example.videopipeline.domain.job.entity.JobType;

public interface JobProcessor {

    JobType supportedType();

    void process(Long videoId, String filePath);
}
