package com.example.videopipeline.domain.video.controller;

import com.example.videopipeline.domain.video.dto.VideoStatusResponse;
import com.example.videopipeline.domain.video.dto.VideoUploadResponse;
import com.example.videopipeline.domain.video.facade.VideoFacade;
import com.example.videopipeline.domain.video.service.VideoService;
import com.example.videopipeline.global.apipayload.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final VideoService videoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<VideoUploadResponse> upload(@RequestPart("file") MultipartFile file) {
        return CommonResponse.onSuccess(videoFacade.upload(file));
    }

    @GetMapping("/{videoId}")
    public CommonResponse<VideoStatusResponse> getStatus(@PathVariable Long videoId) {
        return CommonResponse.onSuccess(videoService.getStatus(videoId));
    }
}
