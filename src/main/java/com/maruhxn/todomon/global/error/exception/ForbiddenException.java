package com.maruhxn.todomon.global.error.exception;

import com.maruhxn.todomon.global.error.ErrorCode;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends TodomonException {
    public ForbiddenException(ErrorCode code) {
        super(code, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(ErrorCode code, String message) {
        super(code, HttpStatus.FORBIDDEN, message);
    }
}
