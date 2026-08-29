package com.example.videopipeline.domain.job.repository;

import com.example.videopipeline.domain.job.entity.ProcessingJob;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessingJobRepository extends JpaRepository<ProcessingJob, Long> {

    List<ProcessingJob> findByVideoId(Long videoId);
}
