package com.skuniv.cs.geonyeong.kaggleapi.service;

import com.skuniv.cs.geonyeong.kaggleapi.dao.PostDao;
import com.skuniv.cs.geonyeong.kaggleapi.utils.TimeUtil;
import com.skuniv.cs.geonyeong.kaggleapi.vo.*;
import com.skuniv.cs.geonyeong.kaggleapi.vo.meta.PostMeta;
import com.skuniv.cs.geonyeong.kaggleapi.vo.meta.QnAMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostDao postDao;

    private final String ANSWER_JOIN_NAME = "answer";
    private final String QUESTION_JOIN_NAME = "question";
    private final Integer INIT_VALUE = 0;

    public Comment insertComment(Comment comment) throws IOException {
        comment = (Comment) setBasePostMeta(comment);
        comment.setCommentId(createUUID());
        return postDao.insertComment(comment);
    }

    public Answer insertAnswer(Answer answer) throws IOException {
        answer = (Answer) setBaseQnAMeta(answer, ANSWER_JOIN_NAME);
        answer.setId(createUUID());
        answer.getQnaJoin().setParent(answer.getParentId());
        return postDao.insertAnswer(answer);
    }

    public Question insertQuestion(Question question) throws IOException {
        question = (Question) setBaseQnAMeta(question, QUESTION_JOIN_NAME);
        question.setId(createUUID());
        question.setAnswerCount(INIT_VALUE);
        question.setFavoriteCount(INIT_VALUE);
        question.setViewCount(INIT_VALUE);
        return postDao.insertQuestion(question);
    }

    public Comment updateComment(Comment comment) {
        return postDao.updateComment(comment);
    }

    public Answer updateAnswer(Answer answer) {
        return postDao.updateAnswer(answer);
    }

    public Question updateQuestion(Question question) {
        return postDao.updateQuestion(question);
    }

    public String deleteComment(String commentId) {
        return postDao.deleteComment(commentId);
    }

    public String deleteAnswer(String answerId) {
        return postDao.deleteAnswer(answerId);
    }

    public String deleteQuestion(String questionId) {
        return postDao.deleteQuestion(questionId);
    }

    private String createUUID() {
        UUID uuid = UUID.randomUUID();
        return String.valueOf(Math.abs(uuid.hashCode()));
    }

    private QnAMeta setBaseQnAMeta(QnAMeta qnAMeta, String joinName) {
        qnAMeta = (QnAMeta) setBasePostMeta(qnAMeta);
        qnAMeta.setCommentCount(INIT_VALUE);
        qnAMeta.setCommentList(new ArrayList<Comment>());
        qnAMeta.setLinkList(new ArrayList<Link>());
        qnAMeta.setTags("");
        qnAMeta.setQnaJoin(
                QnaJoin.builder()
                        .name(joinName)
                        .build()
        );
        return qnAMeta;
    }

    private PostMeta setBasePostMeta(PostMeta postMeta) {
        postMeta.setCreateDate(TimeUtil.toStr(new Date()));
        postMeta.setScore(INIT_VALUE);
        return postMeta;
    }
}
