package com.skuniv.cs.geonyeong.kaggleapi.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PostAuthenticationException extends Exception {

    public PostAuthenticationException(String message) {
        super(message);
    }

    public PostAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PostAuthenticationException(Throwable cause) {
        super(cause);
    }
}
