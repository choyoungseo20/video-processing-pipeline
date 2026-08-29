package com.example.videopipeline.domain.video.controller;

import com.example.videopipeline.domain.video.dto.VideoUploadResponse;
import com.example.videopipeline.domain.video.facade.VideoFacade;
import com.example.videopipeline.global.apipayload.CommonResponse;
import com.example.videopipeline.global.apipayload.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoFacade videoFacade;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<VideoUploadResponse> upload(@RequestPart("file") MultipartFile file) {
        return CommonResponse.onSuccess(SuccessStatus.CREATED, videoFacade.upload(file));
    }
}
