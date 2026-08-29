package com.example.videopipeline.domain.job.listener;

import com.example.videopipeline.domain.job.repository.ProcessingJobRepository;
import com.example.videopipeline.domain.job.service.JobWorker;
import com.example.videopipeline.domain.video.event.VideoUploaded;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class VideoUploadedEventListener {

    private final ProcessingJobRepository jobRepository;
    private final JobWorker jobWorker;

    // AFTER_COMMIT: 업로드 트랜잭션이 커밋된 뒤에만 실행 — job 행이 보이는 것을 보장한다
    @TransactionalEventListener
    public void handle(VideoUploaded event) {
        jobRepository.findByVideoId(event.videoId())
                .forEach(jobWorker::execute);
    }
}
