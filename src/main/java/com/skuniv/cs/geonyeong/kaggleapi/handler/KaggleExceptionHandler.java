package com.skuniv.cs.geonyeong.kaggleapi.handler;

import com.skuniv.cs.geonyeong.kaggleapi.enums.KaggleExceptionType;
import com.skuniv.cs.geonyeong.kaggleapi.exception.EsResponseParsingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.skuniv.cs.geonyeong.kaggleapi.controller")
public class KaggleExceptionHandler {
    @ExceptionHandler(value = EsResponseParsingException.class)
    public BaseExceptionResponse handleReviewParamException(EsResponseParsingException e) {
        return new BaseExceptionResponse(
                KaggleExceptionType.ES_RESPONSE_PARSING_EXCPETION.getStatus(),
                KaggleExceptionType.ES_RESPONSE_PARSING_EXCPETION.getMessage());
    }


    @AllArgsConstructor
    private class BaseExceptionResponse {
        private int status;
        private String message;
    }
}
