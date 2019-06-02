package com.skuniv.cs.geonyeong.kaggleapi.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NoneQuestionDataExcepion extends Exception {

    public NoneQuestionDataExcepion(String message) {
        super(message);
    }

    public NoneQuestionDataExcepion(String message, Throwable cause) {
        super(message, cause);
    }

    public NoneQuestionDataExcepion(Throwable cause) {
        super(cause);
    }
}
