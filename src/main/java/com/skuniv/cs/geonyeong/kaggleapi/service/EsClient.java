package com.skuniv.cs.geonyeong.kaggleapi.service;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.skuniv.cs.geonyeong.kaggleapi.enums.CrudType;
import com.skuniv.cs.geonyeong.kaggleapi.enums.PostType;
import com.skuniv.cs.geonyeong.kaggleapi.exception.EsResponseParsingException;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Comment;
import com.skuniv.cs.geonyeong.kaggleapi.vo.param.CommentListParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class EsClient {
    private static final Gson gson = new Gson();

    private final RestHighLevelClient restHighLevelClient;

    private final String COMMENT_COUNT_FIELD_NAME = "commentCount";
    private final String COMMENT_LIST_FIELD_NAME = "commentList";


    public CommentListParam search(String index, String type, String postId) throws IOException {
        BoolQueryBuilder searchCommentLstQuery = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("id", postId));
        SearchRequest searchRequest = new SearchRequest(index).types(type).source(new SearchSourceBuilder().query(searchCommentLstQuery));
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        Integer commentCount = (Integer) searchResponse.getHits().getAt(0).getSourceAsMap().get(COMMENT_COUNT_FIELD_NAME);
        List<LinkedTreeMap> searchCommentList = gson.fromJson(gson.toJson(searchResponse.getHits().getAt(0).getSourceAsMap().get(COMMENT_LIST_FIELD_NAME)), List.class);
        List<Comment> updateCommentList = searchCommentList.stream().map(item -> {
            String jsonComment = gson.toJson(item);
            return gson.fromJson(jsonComment, Comment.class);
        }).collect(Collectors.toList());
        return new CommentListParam(updateCommentList, commentCount);
    }

    public void insert(IndexRequest indexRequest, PostType postType) {
        doInsert(indexRequest, postType);
    }

    public void insert(IndexRequest indexRequest) {
        doInsert(indexRequest, null);
    }

    private void doInsert(IndexRequest indexRequest, PostType postType) {
        try {
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("{} CREATE response body parsing error => {}", Optional.ofNullable(postType).isPresent() ? postType.name() : "", e);
            throw new EsResponseParsingException();
        }
    }

    public void bulk(List<DocWriteRequest> docWriteRequests, PostType postType, CrudType crudType) {
        BulkRequest bulkRequest = new BulkRequest();
        docWriteRequests.forEach(docWriteRequest -> bulkRequest.add(docWriteRequest));
        try {
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("{} {} response body parsing error => {}", postType.name(), crudType.name(), e);
            throw new EsResponseParsingException();
        }
    }

    public void delete(DeleteRequest deleteRequest, PostType postType) {
        doDelete(deleteRequest, postType);
    }

    public void delete(DeleteRequest deleteRequest) {
        doDelete(deleteRequest, null);
    }

    private void doDelete(DeleteRequest deleteRequest, PostType postType) {
        try {
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("{} delete response body parsing error => {}", Optional.ofNullable(postType).isPresent() ? postType.name() : "", e);
            throw new EsResponseParsingException();
        }
    }

    public void update(UpdateRequest updateRequest, PostType postType) {
        doUpdate(updateRequest, postType);
    }

    public void update(UpdateRequest updateRequest) {
        doUpdate(updateRequest, null);
    }

    private void doUpdate(UpdateRequest updateRequest, PostType postType) {
        try {
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("{} update response body parsing error => {}", Optional.ofNullable(postType).isPresent() ? postType.name() : "", e);
            throw new EsResponseParsingException();
        }
    }

    public SearchResponse search(SearchRequest searchRequest) {
        try {
            return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("search response body parsing error => {}", e);
            throw new EsResponseParsingException();
        }
    }

    public void update(UpdateByQueryRequest updateByQueryRequest) {
        try {
            restHighLevelClient.updateByQuery(updateByQueryRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("updateByQuery response body parsing error => {}", e);
            throw new EsResponseParsingException();
        }
    }
}
