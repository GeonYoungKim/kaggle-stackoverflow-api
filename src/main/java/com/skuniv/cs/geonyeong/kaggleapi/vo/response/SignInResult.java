package com.skuniv.cs.geonyeong.kaggleapi.vo.response;

import com.skuniv.cs.geonyeong.kaggleapi.vo.Account;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignInResult {
    private Account account;
    private String token;
}
