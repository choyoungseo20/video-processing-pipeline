package com.example.videopipeline.domain.job.processor;

import com.example.videopipeline.domain.job.entity.JobType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// FFmpeg 연동 전까지 처리 시간을 흉내 내는 스텁
@Slf4j
@Component
public class StubMetadataProcessor implements JobProcessor {

    @Override
    public JobType supportedType() {
        return JobType.METADATA;
    }

    @Override
    public void process(Long videoId) {
        log.info("[스텁] 메타데이터 추출 시작: videoId={}", videoId);
        sleep(500);
        log.info("[스텁] 메타데이터 추출 완료: videoId={}", videoId);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("처리 중단됨", e);
        }
    }
}
