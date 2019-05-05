package com.skuniv.cs.geonyeong.kaggleapi.dao;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.skuniv.cs.geonyeong.kaggleapi.exception.EsResponseParsingException;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Answer;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Comment;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Question;
import com.skuniv.cs.geonyeong.kaggleapi.vo.meta.QnAMeta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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

    @Value("${com.skuniv.cs.geonyeong.kaggle.es.index}")
    private String esIndex;

    @Value("${com.skuniv.cs.geonyeong.kaggle.es.type}")
    private String esType;

    private final RestHighLevelClient restHighLevelClient;

    private final String ANSWER_COUNT_PLUS_SCRIPT = "ctx._source.answerCount += 1";
    private final String ANSWER_COUNT_MINUST_SCRIPT = "ctx._source.answerCount -= 1";
    private final String COMMENT_COUNT_FIELD_NAME = "commentCount";
    private final String COMMENT_LIST_FIELD_NAME = "commentList";

    public Question createQuestion(Question question) {
        IndexRequest indexRequest = createIndexRequest(question, question.getId()).routing(question.getId());
        callIndexRequest(indexRequest, PostType.QUESTION);
        return question;
    }

    public Question updateQuestion(Question question) {
        UpdateRequest updateRequest = createUpdateRequest(question, question.getId());
        callUpdateRequest(updateRequest, PostType.QUESTION);
        return question;
    }

    public String deleteQuestion(String questionId) {
        DeleteRequest deleteRequest = new DeleteRequest(esIndex, esType, questionId);
        callDeleteRequest(deleteRequest, PostType.QUESTION);
        return questionId;
    }

    public Answer createAnswer(Answer answer) {
        IndexRequest indexRequest = createIndexRequest(answer, answer.getId()).routing(answer.getParentId());
        UpdateRequest updateRequest = createUpdateRequestByScript(ANSWER_COUNT_PLUS_SCRIPT, answer.getParentId());
        callBulkRequest(Arrays.asList(indexRequest, updateRequest), PostType.ANSWER, CrudType.CREATE);
        return answer;
    }

    public Answer updateAnswer(Answer answer) {
        UpdateRequest updateRequest = createUpdateRequest(answer, answer.getId());
        callUpdateRequest(updateRequest, PostType.ANSWER);
        return answer;
    }

    public String deleteAnswer(String answerId, String questionId) {
        DeleteRequest deleteRequest = new DeleteRequest(esIndex, esType, answerId);
        UpdateRequest updateRequest = createUpdateRequestByScript(ANSWER_COUNT_MINUST_SCRIPT, questionId);
        callBulkRequest(Arrays.asList(deleteRequest, updateRequest), PostType.ANSWER, CrudType.DELETE);
        return answerId;
    }

    public Comment createComment(Comment comment) {
        try {
            CommentListParam commentListParam = createCommentListParamBySearch(comment.getPostId());
            commentListParam.getCommentList().add(comment);
            commentListParam.setCommentCount(commentListParam.getCommentCount() + 1);
            UpdateRequest request = createUpdateRequestByCommentListParam(commentListParam, comment.getPostId());
            callUpdateRequest(request, PostType.COMMENT);
        } catch (IOException e) {
            log.error("comment create response body parsing error => {}", e);
            throw new EsResponseParsingException();
        }
        return comment;
    }

    public Comment updateComment(Comment comment) {
        try {
            CommentListParam commentListParam = createCommentListParamBySearch(comment.getPostId());
            List<Comment> updateCommentList = commentListParam.getCommentList().stream()
                    .map(item -> {
                        if (StringUtils.equals(item.getCommentId(), comment.getCommentId()))
                            return comment;
                        return item;
                    }).collect(Collectors.toList());
            commentListParam.setCommentList(updateCommentList);
            UpdateRequest request = createUpdateRequestByCommentListParam(commentListParam, comment.getPostId());
            callUpdateRequest(request, PostType.COMMENT);
        } catch (IOException e) {
            log.error("comment update response body parsing error => {}", e);
            throw new EsResponseParsingException();
        }
        return comment;
    }

    public String deleteComment(String commentId, String postId) {

        try {
            CommentListParam commentListParam = createCommentListParamBySearch(postId);
            List<Comment> deleteCommentList = commentListParam.getCommentList().stream()
                    .filter(item -> !StringUtils.equals(String.valueOf(item.getCommentId()), commentId))
                    .collect(Collectors.toList());
            commentListParam.setCommentList(deleteCommentList);
            commentListParam.setCommentCount(commentListParam.getCommentCount() - 1);
            UpdateRequest request = createUpdateRequestByCommentListParam(commentListParam, postId);
            callUpdateRequest(request, PostType.COMMENT);
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

    private CommentListParam createCommentListParamBySearch(String postId) throws IOException {
        BoolQueryBuilder searchCommentLstQuery = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("id", postId));
        SearchRequest searchRequest = new SearchRequest(esIndex).types(esType).source(new SearchSourceBuilder().query(searchCommentLstQuery));
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        Integer commentCount = (Integer) searchResponse.getHits().getAt(0).getSourceAsMap().get("commentCount");
        List<LinkedTreeMap> searchCommentList = gson.fromJson(gson.toJson(searchResponse.getHits().getAt(0).getSourceAsMap().get("commentList")), List.class);
        List<Comment> updateCommentList = searchCommentList.stream().map(item -> {
            String jsonComment = gson.toJson(item);
            return gson.fromJson(jsonComment, Comment.class);
        }).collect(Collectors.toList());
        return new CommentListParam(updateCommentList, commentCount);
    }

    private void callIndexRequest(IndexRequest indexRequest, PostType postType) {
        IndexResponse indexResponse = null;
        try {
            indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("{} CREATE response body parsing error => {}", postType.name(), e);
            throw new EsResponseParsingException();
        }
        log.info("indexResponse => {}", indexResponse.getResult().getLowercase());
    }

    private void callBulkRequest(List<DocWriteRequest> docWriteRequests, PostType postType, CrudType crudType) {
        BulkRequest bulkRequest = new BulkRequest();
        docWriteRequests.forEach(docWriteRequest -> bulkRequest.add(docWriteRequest));
        try {
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("{} {} response body parsing error => {}", postType.name(), crudType.name(), e);
            throw new EsResponseParsingException();
        }
    }

    private void callDeleteRequest(DeleteRequest deleteRequest, PostType postType) {
        try {
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("{} delete response body parsing error => {}", postType.name(), e);
            throw new EsResponseParsingException();
        }
    }

    private void callUpdateRequest(UpdateRequest updateRequest, PostType postType) {
        try {
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("{} update response body parsing error => {}", postType.name(), e);
            throw new EsResponseParsingException();
        }
    }

    @Getter
    @AllArgsConstructor
    private enum PostType {
        QUESTION,
        ANSWER,
        COMMENT
    }

    @Getter
    @AllArgsConstructor
    private enum CrudType {
        CREATE,
        DELETE,
        UPDATE
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private class CommentListParam {
        private List<Comment> commentList;
        private Integer commentCount;
    }
}
