package com.skuniv.cs.geonyeong.kaggleapi.dao;

import com.google.gson.Gson;
import com.skuniv.cs.geonyeong.kaggleapi.enums.CrudType;
import com.skuniv.cs.geonyeong.kaggleapi.enums.PostType;
import com.skuniv.cs.geonyeong.kaggleapi.exception.EsResponseParsingException;
import com.skuniv.cs.geonyeong.kaggleapi.service.EsClient;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Answer;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Comment;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Question;
import com.skuniv.cs.geonyeong.kaggleapi.vo.meta.QnAMeta;
import com.skuniv.cs.geonyeong.kaggleapi.vo.param.CommentListParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.script.Script;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostDao {
    private final static Gson gson = new Gson();

    @Value("${com.skuniv.cs.geonyeong.kaggle.es.index.post}")
    private String esIndex;

    @Value("${com.skuniv.cs.geonyeong.kaggle.es.type}")
    private String esType;

    private final EsClient esClient;

    private final String ANSWER_COUNT_PLUS_SCRIPT = "ctx._source.answerCount += 1";
    private final String ANSWER_COUNT_MINUST_SCRIPT = "ctx._source.answerCount -= 1";

    public Question createQuestion(Question question) {
        IndexRequest indexRequest = createIndexRequest(question, question.getId()).routing(question.getId());
        esClient.insert(indexRequest, PostType.QUESTION);
        return question;
    }

    public Question updateQuestion(Question question) {
        UpdateRequest updateRequest = createUpdateRequest(question, question.getId());
        esClient.update(updateRequest, PostType.QUESTION);
        return question;
    }

    public String deleteQuestion(String questionId) {
        DeleteRequest deleteRequest = new DeleteRequest(esIndex, esType, questionId);
        esClient.delete(deleteRequest, PostType.QUESTION);
        return questionId;
    }

    public Answer createAnswer(Answer answer) {
        IndexRequest indexRequest = createIndexRequest(answer, answer.getId()).routing(answer.getParentId());
        UpdateRequest updateRequest = createUpdateRequestByScript(ANSWER_COUNT_PLUS_SCRIPT, answer.getParentId());
        esClient.bulk(Arrays.asList(indexRequest, updateRequest), PostType.ANSWER, CrudType.CREATE);
        return answer;
    }

    public Answer updateAnswer(Answer answer) {
        UpdateRequest updateRequest = createUpdateRequest(answer, answer.getId());
        esClient.update(updateRequest, PostType.ANSWER);
        return answer;
    }

    public String deleteAnswer(String answerId, String questionId) {
        DeleteRequest deleteRequest = new DeleteRequest(esIndex, esType, answerId);
        UpdateRequest updateRequest = createUpdateRequestByScript(ANSWER_COUNT_MINUST_SCRIPT, questionId);
        esClient.bulk(Arrays.asList(deleteRequest, updateRequest), PostType.ANSWER, CrudType.DELETE);
        return answerId;
    }

    public Comment createComment(Comment comment) {
        try {
            CommentListParam commentListParam = esClient.search(esIndex, esType, comment.getPostId());
            commentListParam.getCommentList().add(comment);
            commentListParam.setCommentCount(commentListParam.getCommentCount() + 1);
            UpdateRequest request = createUpdateRequestByCommentListParam(commentListParam, comment.getPostId());
            esClient.update(request, PostType.COMMENT);
        } catch (IOException e) {
            log.error("comment create response body parsing error => {}", e);
            throw new EsResponseParsingException();
        }
        return comment;
    }

    public Comment updateComment(Comment comment) {
        try {
            CommentListParam commentListParam = esClient.search(esIndex, esType, comment.getPostId());
            List<Comment> updateCommentList = commentListParam.getCommentList().stream()
                    .map(item -> {
                        if (StringUtils.equals(item.getCommentId(), comment.getCommentId()))
                            return comment;
                        return item;
                    }).collect(Collectors.toList());
            commentListParam.setCommentList(updateCommentList);
            UpdateRequest request = createUpdateRequestByCommentListParam(commentListParam, comment.getPostId());
            esClient.update(request, PostType.COMMENT);
        } catch (IOException e) {
            log.error("comment update response body parsing error => {}", e);
            throw new EsResponseParsingException();
        }
        return comment;
    }

    public String deleteComment(String commentId, String postId) {

        try {
            CommentListParam commentListParam = esClient.search(esIndex, esType, postId);
            List<Comment> deleteCommentList = commentListParam.getCommentList().stream()
                    .filter(item -> !StringUtils.equals(String.valueOf(item.getCommentId()), commentId))
                    .collect(Collectors.toList());
            commentListParam.setCommentList(deleteCommentList);
            commentListParam.setCommentCount(commentListParam.getCommentCount() - 1);
            UpdateRequest request = createUpdateRequestByCommentListParam(commentListParam, postId);
            esClient.update(request, PostType.COMMENT);
        } catch (IOException e) {
            log.error("comment delete response body parsing error => {}", e);
            throw new EsResponseParsingException();
        }
        return commentId;
    }

    private UpdateRequest createUpdateRequestByScript(String script, String id) {
        Script inline = new Script(script);
        UpdateRequest updateRequest = new UpdateRequest(esIndex, esType, id).script(inline);
        return updateRequest;
    }

    private UpdateRequest createUpdateRequest(QnAMeta qnAMeta, String id) {
        return new UpdateRequest(esIndex, esType, id).doc(gson.toJson(qnAMeta), XContentType.JSON);
    }

    private UpdateRequest createUpdateRequestByCommentListParam(CommentListParam commentListParam, String id) {
        String jsonString = gson.toJson(commentListParam);
        log.info("jsonString => {}", jsonString);
        UpdateRequest request = new UpdateRequest(esIndex, esType, id).doc(jsonString, XContentType.JSON);
        return request;
    }

    private IndexRequest createIndexRequest(QnAMeta qnAMeta, String id) {
        return new IndexRequest(esIndex, esType, id).source(gson.toJson(qnAMeta), XContentType.JSON);
    }
}
