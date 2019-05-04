package com.skuniv.cs.geonyeong.kaggleapi.controller;

import com.skuniv.cs.geonyeong.kaggleapi.vo.Account;
import org.springframework.web.bind.annotation.*;

@RestController
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
