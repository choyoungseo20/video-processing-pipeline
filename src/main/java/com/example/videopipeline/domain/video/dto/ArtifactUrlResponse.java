package com.example.videopipeline.domain.video.dto;

public record ArtifactUrlResponse(String url) {

    public static ArtifactUrlResponse of(String url) {
        return new ArtifactUrlResponse(url);
    }
}
