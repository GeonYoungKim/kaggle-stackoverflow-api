package com.skuniv.cs.geonyeong.kaggleapi.enums;

import com.skuniv.cs.geonyeong.kaggleapi.exception.SignInInvalidException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StackoverFlowExceptionType {
    ES_RESPONSE_PARSING_EXCPETION(80801,"es response body parsing error"),
    NONE_QUESTION_DATE_EXCPETION(80802,"empty question data"),
    SIGN_IN_INVALID_EXCEPTION(80803, "signIn invalid id or password"),
    FIND_ID_EXCEPTION(80804, "아이디를 찾지 못하였습니다."),
    FIND_PASSWORD_EXCEPTION(80805, "비밀번호를 찾지 못하였습니다.");
    ;
    private int status;
    private String message;
}
