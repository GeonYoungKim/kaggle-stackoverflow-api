package com.skuniv.cs.geonyeong.kaggleapi.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EsResponseParsingException extends IllegalArgumentException {
    public EsResponseParsingException(String message) {
        super(message);
    }

    public EsResponseParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public EsResponseParsingException(Throwable cause) {
        super(cause);
    }
}
