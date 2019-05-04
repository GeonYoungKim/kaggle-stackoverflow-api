package com.skuniv.cs.geonyeong.kaggleapi.controller;

import com.skuniv.cs.geonyeong.kaggleapi.vo.Account;
import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @RequestMapping(value = "/{accountId}", method = {RequestMethod.GET})
    public String selectAccount(@PathVariable String accountId) {
        return "";
    }

    @RequestMapping(value = "/", method = {RequestMethod.POST})
    public Account insertAccount(@RequestBody Account account) {
        return account;
    }

    @RequestMapping(value = "/", method = {RequestMethod.PUT})
    public Account updateAccount(@RequestBody Account account) {
        return account;
    }

    @RequestMapping(value = "/{accountId}", method = {RequestMethod.DELETE})
    public String deleteAccount(@PathVariable String accountId) {
        return "";
    }
}
