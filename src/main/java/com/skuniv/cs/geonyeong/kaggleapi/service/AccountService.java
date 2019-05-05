package com.skuniv.cs.geonyeong.kaggleapi.service;

import com.skuniv.cs.geonyeong.kaggleapi.dao.AccountDao;
import com.skuniv.cs.geonyeong.kaggleapi.utils.TimeUtil;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountDao accountDao;

    private final Integer INIT_VALUE = 0;

    public Account selectAccount(String accountId) {
        return accountDao.selectAccount(accountId);
    }

    public Account createAccount(Account account) {
        account.setUpvotes(INIT_VALUE);
        account.setDownVotes(INIT_VALUE);
        account.setCreateDate(TimeUtil.toStr(new Date()));
        return accountDao.createAccount(account);
    }

    public Account updateAccount(Account account) {
        return accountDao.updateAccount(account);
    }

    public String deleteAccount(String accountId) {
        return accountDao.deleteAccount(accountId);
    }
}
