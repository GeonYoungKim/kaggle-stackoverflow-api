package com.skuniv.cs.geonyeong.kaggleapi.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skuniv.cs.geonyeong.kaggleapi.vo.meta.QnAMeta;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Question extends QnAMeta {

    private String id;
    private String title;
    private Integer answerCount;
    private Integer favoriteCount;
    private Integer viewCount;

    @Builder
    public Question(String body, String createDate, Integer score, Account account,
        Integer commentCount, String tags, List<Comment> commentList, List<Link> linkList,
        QnaJoin qnaJoin, String id, String title, Integer answerCount, Integer favoriteCount,
        Integer viewCount) {
        super(body, createDate, score, account, commentCount, tags, commentList, linkList, qnaJoin);
        this.id = id;
        this.title = title;
        this.answerCount = answerCount;
        this.favoriteCount = favoriteCount;
        this.viewCount = viewCount;
    }
}
