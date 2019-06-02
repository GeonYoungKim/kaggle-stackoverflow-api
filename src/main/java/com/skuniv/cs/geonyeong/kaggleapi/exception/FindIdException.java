package com.skuniv.cs.geonyeong.kaggleapi.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FindIdException extends Exception {

    public FindIdException(String message) {
        super(message);
    }

    public FindIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public FindIdException(Throwable cause) {
        super(cause);
    }
}
