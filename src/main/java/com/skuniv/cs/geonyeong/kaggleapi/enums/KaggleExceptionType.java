package com.skuniv.cs.geonyeong.kaggleapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KaggleExceptionType {
    ES_RESPONSE_PARSING_EXCPETION(80801,"es response body parsing error")
    ;
    private int status;
    private String message;
}
