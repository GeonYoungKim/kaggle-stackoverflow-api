package com.skuniv.cs.geonyeong.kaggleapi.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseExceptionResponse {
    private int status;
    private String message;
}
