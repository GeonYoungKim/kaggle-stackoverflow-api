package com.skuniv.cs.geonyeong.kaggleapi.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SignInInvalidException extends Exception {

    public SignInInvalidException(String message) {
        super(message);
    }

    public SignInInvalidException(Throwable cause) {
        super(cause);
    }

    public SignInInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
