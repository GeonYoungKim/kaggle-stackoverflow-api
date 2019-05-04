package com.skuniv.cs.geonyeong.kaggleapi.dao;

import com.google.gson.Gson;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Answer;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Comment;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Question;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostDao {
    private final static Gson gson = new Gson();

    @Value("${com.skuniv.cs.geonyeong.kaggle.es.index}")
    private String esIndexName;

    @Value("${com.skuniv.cs.geonyeong.kaggle.es.type}")
    private String esType;

    private final RestHighLevelClient restHighLevelClient;

    public Comment insertComment(Comment comment) throws IOException {
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("id", comment.getPostId()))
                ;
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query);
        SearchRequest searchRequest = new SearchRequest(esIndexName).source(searchSourceBuilder).types(esType);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        List<Comment> commentList = (List<Comment>) searchResponse.getHits().getAt(0).getSourceAsMap().get("commentList");
        commentList.add(comment);
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("commentList", commentList);
        UpdateRequest request = new UpdateRequest(esIndexName,esType, comment.getPostId()).doc(jsonMap);
        restHighLevelClient.update(request, RequestOptions.DEFAULT);
        return comment;
    }

    public Answer insertAnswer(Answer answer) throws IOException {
        IndexRequest indexRequest = createIndexRequest()
                .id(answer.getId())
                .source(gson.toJson(answer), XContentType.JSON)
                ;
        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        return answer;
    }

    public Question insertQuestion(Question question) throws IOException {
        IndexRequest indexRequest = createIndexRequest()
                .id(question.getId())
                .source(gson.toJson(question), XContentType.JSON)
                ;
        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        return question;
    }

    public Comment updateComment(Comment comment) {


        return comment;
    }

    public Answer updateAnswer(Answer answer) {


        return answer;
    }

    public Question updateQuestion(Question question) {


        return question;
    }

    public String deleteComment(String commentId) {
        return commentId;
    }

    public String deleteAnswer(String answerId) {
        DeleteRequest deleteRequest = new DeleteRequest(esIndexName).type(esType).id(answerId);
        try {
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("");
        }
        return answerId;
    }

    public String deleteQuestion(String questionId) {
        DeleteRequest deleteRequest = new DeleteRequest(esIndexName).type(esType).id(questionId);
        try {
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("");
        }
        return questionId;
    }

    private IndexRequest createIndexRequest() {
        return new IndexRequest(esIndexName)
                .type(esType)
                ;
    }
}
