package com.skuniv.cs.geonyeong.kaggleapi.vo.meta;

import com.skuniv.cs.geonyeong.kaggleapi.vo.Account;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Comment;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Link;
import com.skuniv.cs.geonyeong.kaggleapi.vo.QnaJoin;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class QnAMeta extends PostMeta {
    private Integer commentCount;
    private String tags;
    private List<Comment> commentList;
    private List<Link> linkList;
    private QnaJoin qnaJoin;

    public QnAMeta() {
    }

    public QnAMeta(String body, String createDate, Integer score, Account account, Integer commentCount, String tags, List<Comment> commentList, List<Link> linkList, QnaJoin qnaJoin) {
        super(body, createDate, score, account);
        this.commentCount = commentCount;
        this.tags = tags;
        this.commentList = commentList;
        this.linkList = linkList;
        this.qnaJoin = qnaJoin;
    }
}
