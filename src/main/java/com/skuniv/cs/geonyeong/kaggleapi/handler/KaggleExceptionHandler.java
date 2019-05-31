package com.skuniv.cs.geonyeong.kaggleapi.handler;

import com.skuniv.cs.geonyeong.kaggleapi.enums.StackoverFlowExceptionType;
import com.skuniv.cs.geonyeong.kaggleapi.exception.EsResponseParsingException;
import com.skuniv.cs.geonyeong.kaggleapi.exception.NoneQuestionDataExcepion;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.skuniv.cs.geonyeong.kaggleapi.controller")
public class KaggleExceptionHandler {

    @ExceptionHandler(value = EsResponseParsingException.class)
    public BaseExceptionResponse handleParsingException(EsResponseParsingException e) {
        return new BaseExceptionResponse(
                StackoverFlowExceptionType.ES_RESPONSE_PARSING_EXCPETION.getStatus(),
                StackoverFlowExceptionType.ES_RESPONSE_PARSING_EXCPETION.getMessage());
    }

    @ExceptionHandler(value = NoneQuestionDataExcepion.class)
    public BaseExceptionResponse handleNoneQuestionException(NoneQuestionDataExcepion e) {
        return new BaseExceptionResponse(
            StackoverFlowExceptionType.NONE_QUESTION_DATE_EXCPETION.getStatus(),
            StackoverFlowExceptionType.NONE_QUESTION_DATE_EXCPETION.getMessage()
        );
    }

    @AllArgsConstructor
    private class BaseExceptionResponse {
        private int status;
        private String message;
    }
}
