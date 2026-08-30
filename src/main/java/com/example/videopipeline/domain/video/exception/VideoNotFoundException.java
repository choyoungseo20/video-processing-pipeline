package com.example.videopipeline.domain.video.exception;

import com.example.videopipeline.global.apipayload.ErrorStatus;
import com.example.videopipeline.global.exception.GeneralException;

public class VideoNotFoundException extends GeneralException {

    public VideoNotFoundException(Long videoId) {
        super(ErrorStatus.VIDEO_NOT_FOUND, "존재하지 않는 영상: id=" + videoId);
    }
}
