package com.skuniv.cs.geonyeong.kaggleapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Account;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Comment;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class UpdateTest {
    private final static Gson gson = new Gson();
    private final String esIndex = "stackoverflow";
    private final String esType = "_doc";

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void test() throws IOException {
        UpdateRequest request = new UpdateRequest(esIndex, esType, "42829365");
        Script inline = new Script("ctx._source.answerCount -= 1");
        request.script(inline);
        restHighLevelClient.update(request, RequestOptions.DEFAULT);
    }

    @Test
    public void uuidTest() {
        UUID uuid = UUID.randomUUID();
        String uuidStr = uuid.toString();
        log.info("uui => {}", uuidStr);
        log.info("uui => {}", String.valueOf(Math.abs(uuid.hashCode())));
    }

    @Test
    public void searchCommentTest() {
        BoolQueryBuilder searchCommentLstQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("id", "10362376"));
        SearchRequest searchRequest = new SearchRequest(esIndex).types(esType).source(new SearchSourceBuilder().query(searchCommentLstQuery));
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            List<Comment> commentList = gson.fromJson(gson.toJson(searchResponse.getHits().getAt(0).getSourceAsMap().get("commentList").toString()) , List.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void accountSelectTest() {
        BoolQueryBuilder searchCommentLstQuery = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("account.id", "847917"));
        SearchRequest searchRequest = new SearchRequest(esIndex).types(esType).source(new SearchSourceBuilder().fetchSource(new String[]{"account.*"},new String[]{}).query(searchCommentLstQuery).size(1));
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String accountJson = gson.toJson(searchResponse.getHits().getAt(0).getSourceAsMap().get("account"));
        Account account = gson.fromJson(accountJson, Account.class);
        log.info("account => {}", account.toString());
    }

    @Test
    public void objectToMapTest() {
        Account account = Account.builder()
                .id("1106578")
                .displayName("부천 사는 94년생 개발자")
                .age("26")
                .createDate("2019-05-05 15:57:00")
                .build()
                ;
    }
}
