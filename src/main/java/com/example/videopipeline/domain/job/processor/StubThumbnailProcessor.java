package com.example.videopipeline.domain.job.processor;

import com.example.videopipeline.domain.job.entity.JobType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// FFmpeg 연동 전까지 처리 시간을 흉내 내는 스텁
@Slf4j
@Component
public class StubThumbnailProcessor implements JobProcessor {

    @Override
    public JobType supportedType() {
        return JobType.THUMBNAIL;
    }

    @Override
    public void process(Long videoId) {
        log.info("[스텁] 썸네일 생성 시작: videoId={}", videoId);
        sleep(2_000);
        log.info("[스텁] 썸네일 생성 완료: videoId={}", videoId);
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
