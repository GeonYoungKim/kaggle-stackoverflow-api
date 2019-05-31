package com.skuniv.cs.geonyeong.kaggleapi.service;

import com.google.gson.Gson;
import com.skuniv.cs.geonyeong.kaggleapi.dao.PostDao;
import com.skuniv.cs.geonyeong.kaggleapi.exception.NoneQuestionDataExcepion;
import com.skuniv.cs.geonyeong.kaggleapi.utils.TimeUtil;
import com.skuniv.cs.geonyeong.kaggleapi.vo.*;
import com.skuniv.cs.geonyeong.kaggleapi.vo.meta.PostMeta;
import com.skuniv.cs.geonyeong.kaggleapi.vo.meta.QnAMeta;
import com.skuniv.cs.geonyeong.kaggleapi.vo.response.PostResult;
import com.skuniv.cs.geonyeong.kaggleapi.vo.response.SearchResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private Gson gson = new Gson();

    private final PostDao postDao;

    private final String ANSWER_JOIN_NAME = "answer";
    private final String QUESTION_JOIN_NAME = "question";
    private final Integer INIT_VALUE = 0;

    public Question createQuestion(Question question) {
        question = (Question) setBaseQnAMeta(question, QUESTION_JOIN_NAME);
        question.setId(createUUID());
        question.setAnswerCount(INIT_VALUE);
        question.setFavoriteCount(INIT_VALUE);
        question.setViewCount(INIT_VALUE);
        log.info("create question => {}", gson.toJson(question));
        return postDao.createQuestion(question);
    }

    public Question updateQuestion(Question question) {
        return postDao.updateQuestion(question);
    }

    public String deleteQuestion(String questionId) {
        return postDao.deleteQuestion(questionId);
    }

    public Answer createAnswer(Answer answer) {
        answer = (Answer) setBaseQnAMeta(answer, ANSWER_JOIN_NAME);
        answer.setId(createUUID());
        answer.getQnaJoin().setParent(answer.getParentId());
        log.info("create answer => {}", gson.toJson(answer));
        return postDao.createAnswer(answer);
    }

    public Answer updateAnswer(Answer answer) {
        return postDao.updateAnswer(answer);
    }

    public String deleteAnswer(String answerId, String questionId) {
        return postDao.deleteAnswer(answerId, questionId);
    }

    public Comment createComment(Comment comment) {
        comment = (Comment) setBasePostMeta(comment);
        comment.setCommentId(createUUID());
        log.info("comment create => {}", gson.toJson(comment));
        return postDao.createComment(comment);
    }

    public Comment updateComment(Comment comment) {
        return postDao.updateComment(comment);
    }

    public String deleteComment(String commentId, String postId) {
        return postDao.deleteComment(commentId, postId);
    }

    private String createUUID() {
        UUID uuid = UUID.randomUUID();
        return String.valueOf(uuid);
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

    public SearchResult search(int pageNo, String content) {
        return postDao.search(pageNo, content);
    }

    public PostResult getPost(String postId) throws NoneQuestionDataExcepion {
        return postDao.getPost(postId);
    }
}
