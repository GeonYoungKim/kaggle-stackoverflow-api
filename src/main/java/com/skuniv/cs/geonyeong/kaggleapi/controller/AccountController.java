package com.skuniv.cs.geonyeong.kaggleapi.controller;

import com.skuniv.cs.geonyeong.kaggleapi.exception.FindIdException;
import com.skuniv.cs.geonyeong.kaggleapi.exception.FindPasswordException;
import com.skuniv.cs.geonyeong.kaggleapi.exception.SignInInvalidException;
import com.skuniv.cs.geonyeong.kaggleapi.service.AccountService;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Account;
import com.skuniv.cs.geonyeong.kaggleapi.vo.response.SignInResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@Slf4j
@Api(value = "/api/v1/kaggle/stackoverflow/account", tags = {"Kaggle Stackoverflow Account" })
@SwaggerDefinition(tags = {
        @Tag(name = "Kaggle Stackoverflow Account", description = "캐글 스택오버플로우 유저관련 controller")
})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kaggle/stackoverflow/account")
public class AccountController {
    private final AccountService accountService;

    @RequestMapping(value = "/{accountId}", method = {RequestMethod.GET})
    public Account selectAccount(@PathVariable String accountId) {
        return accountService.selectAccount(accountId);
    }

    @RequestMapping(method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Account createAccount(@RequestBody Account account) throws UnsupportedEncodingException {
        return accountService.createAccount(account);
    }

    @RequestMapping(method = {RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Account updateAccount(@RequestBody Account account) {
        return accountService.updateAccount(account);
    }

    @RequestMapping(value = "/{accountId}", method = {RequestMethod.DELETE})
    public String deleteAccount(@PathVariable String accountId) {
        return accountService.deleteAccount(accountId);
    }

    @RequestMapping(value = "/findId", method = {RequestMethod.GET})
    public Account findId(@ModelAttribute Account account) throws FindIdException {
        return accountService.findId(account);
    }

    @RequestMapping(value = "/findPassword", method = {RequestMethod.GET})
    public Account findPassword(@ModelAttribute Account account)
        throws UnsupportedEncodingException, FindPasswordException {
        return accountService.findPassword(account);
    }

    @RequestMapping(value = "/signIn", method = {RequestMethod.POST})
    public SignInResult signIn(@RequestBody Account account)
        throws UnsupportedEncodingException, SignInInvalidException {
        SignInResult signInResult = accountService.signIn(account);
        return signInResult;
    }
}
