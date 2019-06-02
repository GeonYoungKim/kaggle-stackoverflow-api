package com.skuniv.cs.geonyeong.kaggleapi.service;

import com.skuniv.cs.geonyeong.kaggleapi.vo.Account;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class AuthenticationManageService implements InitializingBean {
    private MessageDigest md;

    // TODO : OAUTH
    private final Map<String, Account> tokenMap = new HashMap<>();

    public String addAuthenticate(Account account) {
        String token = generateToken(account.getId());
        tokenMap.put(token, account);
        return token;
    }

    private String generateToken(String source) {
        md.update(source.getBytes());
        byte byteData[] = md.digest();
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < byteData.length ; i++){
            sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        md = MessageDigest.getInstance("MD5");
    }
}
