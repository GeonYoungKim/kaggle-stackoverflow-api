package com.skuniv.cs.geonyeong.kaggleapi.vo.meta;

import com.skuniv.cs.geonyeong.kaggleapi.vo.Account;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PostMeta {
    private String body;
    private String createDate;
    private Integer score;
    private Account account;

    public PostMeta() {
    }

    public PostMeta(String body, String createDate, Integer score, Account account) {
        this.body = body;
        this.createDate = createDate;
        this.score = score;
        this.account = account;
    }
}
