package com.example.videopipeline.domain.video.repository;

import com.example.videopipeline.domain.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
