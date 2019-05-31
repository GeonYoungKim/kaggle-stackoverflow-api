package com.skuniv.cs.geonyeong.kaggleapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StackoverFlowExceptionType {
    ES_RESPONSE_PARSING_EXCPETION(80801,"es response body parsing error"),
    NONE_QUESTION_DATE_EXCPETION(80802,"empty question data")
    ;
    private int status;
    private String message;
}
