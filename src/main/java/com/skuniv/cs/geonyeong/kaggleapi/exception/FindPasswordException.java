package com.skuniv.cs.geonyeong.kaggleapi.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FindPasswordException extends Exception {

    public FindPasswordException(String message) {
        super(message);
    }

    public FindPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public FindPasswordException(Throwable cause) {
        super(cause);
    }
}
