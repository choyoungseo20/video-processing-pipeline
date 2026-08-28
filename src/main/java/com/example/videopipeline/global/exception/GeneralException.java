package com.example.videopipeline.global.exception;

import com.example.videopipeline.global.apipayload.ErrorCode;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

    private final ErrorCode code;

    public GeneralException(ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }
}
