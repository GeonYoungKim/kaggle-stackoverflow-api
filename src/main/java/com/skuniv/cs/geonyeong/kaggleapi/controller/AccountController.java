package com.skuniv.cs.geonyeong.kaggleapi.controller;

import com.skuniv.cs.geonyeong.kaggleapi.service.AccountService;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Account;
import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
    public Account createAccount(@RequestBody Account account) {
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
}
