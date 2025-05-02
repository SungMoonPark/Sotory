package com.sotory.user.exception;

import com.sotory.common.exception.ErrorCode;

public class UserException {

    public static class UserNotFoundException extends RuntimeException {
        private final ErrorCode errorCode;

        public UserNotFoundException(ErrorCode errorCode) {
            super(errorCode.getMessage());
            this.errorCode = errorCode;
        }

        public ErrorCode getErrorCode() {
            return errorCode;
        }
    }
}
