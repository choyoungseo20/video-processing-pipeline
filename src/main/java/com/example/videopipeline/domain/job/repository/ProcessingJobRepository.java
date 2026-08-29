package com.example.videopipeline.domain.job.repository;

import com.example.videopipeline.domain.job.entity.JobStatus;
import com.example.videopipeline.domain.job.entity.ProcessingJob;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface ProcessingJobRepository extends JpaRepository<ProcessingJob, Long> {

    List<ProcessingJob> findByVideoId(Long videoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ProcessingJob> findWithLockById(Long id);

    List<ProcessingJob> findByStatus(JobStatus status);

    List<ProcessingJob> findByStatusAndCreatedAtBefore(JobStatus status, LocalDateTime threshold);

    List<ProcessingJob> findByStatusAndStartedAtBefore(JobStatus status, LocalDateTime threshold);
}
