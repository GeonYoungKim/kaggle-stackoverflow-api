package com.skuniv.cs.geonyeong.kaggleapi.dao;

import com.google.gson.Gson;
import com.skuniv.cs.geonyeong.kaggleapi.service.EsClient;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AccountDao {

    private final static Gson gson = new Gson();

    @Value("${com.skuniv.cs.geonyeong.kaggle.es.index.account}")
    private String esAccountIndex;

    @Value("${com.skuniv.cs.geonyeong.kaggle.es.index.post}")
    private String esPostIndex;

    @Value("${com.skuniv.cs.geonyeong.kaggle.es.type}")
    private String esType;

    private final EsClient esClient;

    private final String ACCOUNT_SCRIPT_SOURCE_PREFIX = "ctx._source.account.";
    private final String ACCOUNT_SCRIPT_PARAM_PREFIX = "params.";

    public Account selectAccount(String accountId) {
        BoolQueryBuilder searchCommentLstQuery = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("id", accountId));
        SearchRequest searchRequest = new SearchRequest(esAccountIndex).types(esType).source(new SearchSourceBuilder().query(searchCommentLstQuery));
        SearchResponse searchResponse = esClient.search(searchRequest);
        String accountJson = searchResponse.getHits().getAt(0).getSourceAsString();
        Account account = gson.fromJson(accountJson, Account.class);
        return account;
    }

    public Account createAccount(Account account) {
        IndexRequest indexRequest = new IndexRequest(esAccountIndex, esType, account.getId()).source(gson.toJson(account), XContentType.JSON);
        esClient.insert(indexRequest);
        return account;
    }

    public Account updateAccount(Account account) {
        UpdateRequest updateRequest = new UpdateRequest(esAccountIndex, esType, account.getId()).doc(gson.toJson(account), XContentType.JSON);
        esClient.update(updateRequest);

        BoolQueryBuilder searchCommentLstQuery = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("account.id", account.getId()));
        Map<String, Object> parameters = gson.fromJson(gson.toJson(account), Map.class);

        Script script = new Script(ScriptType.INLINE, "painless", createScriptCodeByMap(parameters), parameters);
        UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest(esPostIndex);
        updateByQueryRequest.setDocTypes(esType);
        updateByQueryRequest.setScript(script);
        updateByQueryRequest.setQuery(searchCommentLstQuery);
        esClient.update(updateByQueryRequest);
        return account;
    }

    public String deleteAccount(String accountId) {
        DeleteRequest deleteRequest = new DeleteRequest(esAccountIndex, esType, accountId);
        esClient.delete(deleteRequest);
        return accountId;
    }

    private String createScriptCodeByMap(Map<String, Object> parameters) {
        StringBuilder sb = new StringBuilder();
        parameters.keySet().forEach(k -> {
            sb.append(ACCOUNT_SCRIPT_SOURCE_PREFIX + k + " = " + ACCOUNT_SCRIPT_PARAM_PREFIX + k + "; ");
        });
        log.info("script code => {}", sb.toString());
        return sb.toString();
    }
}
