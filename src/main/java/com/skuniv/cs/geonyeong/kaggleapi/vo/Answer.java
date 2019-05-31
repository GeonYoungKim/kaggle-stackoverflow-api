package com.skuniv.cs.geonyeong.kaggleapi.vo;

import com.skuniv.cs.geonyeong.kaggleapi.vo.meta.QnAMeta;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Answer extends QnAMeta {

    private String id;
    private String parentId;

    @Builder
    public Answer(String body, String createDate, Integer score, Account account,
        Integer commentCount, String tags, List<Comment> commentList, List<Link> linkList,
        QnaJoin qnaJoin, String id, String parentId) {
        super(body, createDate, score, account, commentCount, tags, commentList, linkList, qnaJoin);
        this.id = id;
        this.parentId = parentId;
    }
}
