package com.example.videopipeline.domain.video.repository;

import com.example.videopipeline.domain.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface VideoRepository extends JpaRepository<Video, Long> {

    @Modifying
    @Query("""
            update Video v
            set v.durationSec = :durationSec,
                v.width = :width,
                v.height = :height,
                v.videoCodec = :videoCodec,
                v.audioCodec = :audioCodec
            where v.id = :videoId
            """)
    void updateMetadata(
            Long videoId,
            Double durationSec,
            Integer width,
            Integer height,
            String videoCodec,
            String audioCodec);

    @Modifying
    @Query("""
            update Video v
            set v.thumbnailPath = :thumbnailPath
            where v.id = :videoId
            """)
    void updateThumbnailPath(Long videoId, String thumbnailPath);

    @Modifying
    @Query("""
            update Video v
            set v.playlistPath = :playlistPath
            where v.id = :videoId
            """)
    void updatePlaylistPath(Long videoId, String playlistPath);
}
