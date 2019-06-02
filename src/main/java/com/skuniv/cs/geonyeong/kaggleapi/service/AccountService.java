package com.skuniv.cs.geonyeong.kaggleapi.service;

import com.skuniv.cs.geonyeong.kaggleapi.dao.AccountDao;
import com.skuniv.cs.geonyeong.kaggleapi.exception.FindIdException;
import com.skuniv.cs.geonyeong.kaggleapi.exception.FindPasswordException;
import com.skuniv.cs.geonyeong.kaggleapi.exception.SignInInvalidException;
import com.skuniv.cs.geonyeong.kaggleapi.utils.TimeUtil;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Account;
import com.skuniv.cs.geonyeong.kaggleapi.vo.response.SignInResult;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountDao accountDao;
    private final CryptorService cryptorService;
    private final AuthenticationManageService authenticationManageService;

    private final Integer INIT_VALUE = 0;

    public Account selectAccount(String accountId) {
        return accountDao.selectAccount(accountId);
    }

    public Account createAccount(Account account) throws UnsupportedEncodingException {
        account.setUpvotes(INIT_VALUE);
        account.setDownVotes(INIT_VALUE);
        account.setCreateDate(TimeUtil.toStr(new Date()));
        account.setPassword(cryptorService.encryptBase64(account.getPassword()));
        return accountDao.createAccount(account);
    }

    public Account updateAccount(Account account) {
        return accountDao.updateAccount(account);
    }

    public String deleteAccount(String accountId) {
        return accountDao.deleteAccount(accountId);
    }

    public Account findId(Account account) throws FindIdException {
        return accountDao.findId(account);
    }

    public Account findPassword(Account account)
        throws UnsupportedEncodingException, FindPasswordException {
        Account findAccount = accountDao.findPassword(account);
        findAccount.setPassword(cryptorService.decryptBase64(findAccount.getPassword()));
        return findAccount;
    }

    public SignInResult signIn(Account account)
        throws UnsupportedEncodingException, SignInInvalidException {
        log.info("account before service {}", account);
        account.setPassword(cryptorService.encryptBase64(account.getPassword()));
        Account signInAccount = accountDao.signIn(account);
        log.info("signInAccount => {}", signInAccount);
        String token = authenticationManageService.addAuthenticate(account);
        log.info("token => {}", token);
        SignInResult signInResult = SignInResult.builder().account(signInAccount).token(token).build();
        return signInResult;
    }
}
