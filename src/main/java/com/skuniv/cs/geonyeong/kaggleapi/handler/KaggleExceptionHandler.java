package com.skuniv.cs.geonyeong.kaggleapi.handler;

import com.skuniv.cs.geonyeong.kaggleapi.enums.StackoverFlowExceptionType;
import com.skuniv.cs.geonyeong.kaggleapi.exception.EsResponseParsingException;
import com.skuniv.cs.geonyeong.kaggleapi.exception.FindIdException;
import com.skuniv.cs.geonyeong.kaggleapi.exception.FindPasswordException;
import com.skuniv.cs.geonyeong.kaggleapi.exception.NoneQuestionDataExcepion;
import com.skuniv.cs.geonyeong.kaggleapi.exception.SignInInvalidException;
import com.skuniv.cs.geonyeong.kaggleapi.vo.response.BaseExceptionResponse;
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

    @ExceptionHandler(value = SignInInvalidException.class)
    public BaseExceptionResponse handleSignInInvalidException(SignInInvalidException e) {
        log.info("handleSignInInvalidException");
        return new BaseExceptionResponse(
            StackoverFlowExceptionType.SIGN_IN_INVALID_EXCEPTION.getStatus(),
            StackoverFlowExceptionType.SIGN_IN_INVALID_EXCEPTION.getMessage()
        );
    }

    @ExceptionHandler(value = FindIdException.class)
    public BaseExceptionResponse handleFindIdException(FindIdException e) {
        return new BaseExceptionResponse(
            StackoverFlowExceptionType.FIND_ID_EXCEPTION.getStatus(),
            StackoverFlowExceptionType.FIND_ID_EXCEPTION.getMessage()
        );
    }

    @ExceptionHandler(value = FindPasswordException.class)
    public BaseExceptionResponse handleFindPasswordException(FindPasswordException e) {
        return new BaseExceptionResponse(
            StackoverFlowExceptionType.FIND_PASSWORD_EXCEPTION.getStatus(),
            StackoverFlowExceptionType.FIND_PASSWORD_EXCEPTION.getMessage()
        );
    }
}
