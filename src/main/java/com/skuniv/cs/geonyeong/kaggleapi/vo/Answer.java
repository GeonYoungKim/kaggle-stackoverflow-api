package com.skuniv.cs.geonyeong.kaggleapi.vo;

import com.skuniv.cs.geonyeong.kaggleapi.vo.meta.QnAMeta;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class Answer extends QnAMeta {
    private String id;
    private String parentId;

    public Answer() {
    }

    @Builder
    public Answer(String body, String createDate, Integer score, Account account, Integer commentCount, String tags, List<Comment> commentList, List<Link> linkList, QnaJoin qnaJoin, String id, String parentId) {
        super(body, createDate, score, account, commentCount, tags, commentList, linkList, qnaJoin);
        this.id = id;
        this.parentId = parentId;
    }
}
