package com.example.videopipeline.domain.video.dto;

import com.example.videopipeline.domain.video.entity.Video;

public record VideoUploadResponse(Long videoId, String originalName) {

    public static VideoUploadResponse from(Video video) {
        return new VideoUploadResponse(video.getId(), video.getOriginalName());
    }
}
