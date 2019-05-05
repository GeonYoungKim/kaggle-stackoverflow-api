package com.skuniv.cs.geonyeong.kaggleapi.exception;


public class EsResponseParsingException extends RuntimeException {

    public EsResponseParsingException() {
    }

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
