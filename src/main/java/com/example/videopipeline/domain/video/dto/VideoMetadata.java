package com.example.videopipeline.domain.video.dto;

public record VideoMetadata(
        Double durationSec,
        Integer width,
        Integer height,
        String videoCodec,
        String audioCodec) {
}
