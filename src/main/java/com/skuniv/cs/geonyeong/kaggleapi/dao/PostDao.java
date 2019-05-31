package com.skuniv.cs.geonyeong.kaggleapi.dao;

import static com.skuniv.cs.geonyeong.kaggleapi.enums.SuggestType.BODY_SUGGEST;
import static com.skuniv.cs.geonyeong.kaggleapi.enums.SuggestType.TITLE_SUGGEST;

import com.google.gson.Gson;
import com.skuniv.cs.geonyeong.kaggleapi.enums.CrudType;
import com.skuniv.cs.geonyeong.kaggleapi.enums.PostType;
import com.skuniv.cs.geonyeong.kaggleapi.exception.EsResponseParsingException;
import com.skuniv.cs.geonyeong.kaggleapi.exception.NoneQuestionDataExcepion;
import com.skuniv.cs.geonyeong.kaggleapi.service.EsClient;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Answer;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Comment;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Question;
import com.skuniv.cs.geonyeong.kaggleapi.vo.meta.QnAMeta;
import com.skuniv.cs.geonyeong.kaggleapi.vo.param.CommentListParam;
import com.skuniv.cs.geonyeong.kaggleapi.vo.response.PostResult;
import com.skuniv.cs.geonyeong.kaggleapi.vo.response.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetRequest.Item;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry.Option;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

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
    private final int SEARCH_SIZE = 10;
    private final String PARENT_ID_FIELD = "parentId";
    private final String TITLE_FIELD = "title";
    private final String BODY_FIELD = "body";
    private final String ID_FIELD = "id";
    private final String[] INCLUDE_SEARCH_FIELD = {PARENT_ID_FIELD, ID_FIELD, TITLE_FIELD,
        BODY_FIELD};
    private final String[] EXCLUDE_SEARCH_FIELD = {};
    private final String[] SEARCH_LIST_FIELD = {TITLE_FIELD, BODY_FIELD};
    private final String[] SEARCH_DETAIL_FIELD = {ID_FIELD, PARENT_ID_FIELD};

    public Question createQuestion(Question question) {
        IndexRequest indexRequest = createIndexRequest(question, question.getId())
            .routing(question.getId());
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
        IndexRequest indexRequest = createIndexRequest(answer, answer.getId())
            .routing(answer.getParentId());
        UpdateRequest updateRequest = createUpdateRequestByScript(ANSWER_COUNT_PLUS_SCRIPT,
            answer.getParentId());
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
        UpdateRequest updateRequest = createUpdateRequestByScript(ANSWER_COUNT_MINUST_SCRIPT,
            questionId);
        esClient
            .bulk(Arrays.asList(deleteRequest, updateRequest), PostType.ANSWER, CrudType.DELETE);
        return answerId;
    }

    public Comment createComment(Comment comment) {
        try {
            CommentListParam commentListParam = esClient
                .search(esIndex, esType, comment.getPostId());
            commentListParam.getCommentList().add(comment);
            commentListParam.setCommentCount(commentListParam.getCommentCount() + 1);
            UpdateRequest request = createUpdateRequestByCommentListParam(commentListParam,
                comment.getPostId());
            esClient.update(request, PostType.COMMENT);
        } catch (IOException e) {
            log.error("comment create response body parsing error => {}", e);
            throw new EsResponseParsingException();
        }
        return comment;
    }

    public Comment updateComment(Comment comment) {
        try {
            CommentListParam commentListParam = esClient
                .search(esIndex, esType, comment.getPostId());
            List<Comment> updateCommentList = commentListParam.getCommentList().stream()
                .map(item -> {
                    if (StringUtils.equals(item.getCommentId(), comment.getCommentId())) {
                        return comment;
                    }
                    return item;
                }).collect(Collectors.toList());
            commentListParam.setCommentList(updateCommentList);
            UpdateRequest request = createUpdateRequestByCommentListParam(commentListParam,
                comment.getPostId());
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

    private UpdateRequest createUpdateRequestByCommentListParam(CommentListParam commentListParam,
        String id) {
        String jsonString = gson.toJson(commentListParam);
        log.info("jsonString => {}", jsonString);
        UpdateRequest request = new UpdateRequest(esIndex, esType, id)
            .doc(jsonString, XContentType.JSON);
        return request;
    }

    private IndexRequest createIndexRequest(QnAMeta qnAMeta, String id) {
        return new IndexRequest(esIndex, esType, id)
            .source(gson.toJson(qnAMeta), XContentType.JSON);
    }

    public SearchResult search(int pageNo, String content) {
        List<String> suggestionList = new ArrayList<>();
        List<Question> questionList = new ArrayList<>();
        MultiGetRequest multiGetRequest = new MultiGetRequest();
        SearchRequest searchRequest = makeSearchRequest(pageNo, content);
        SearchResponse searchResponse = esClient.search(searchRequest);
        Iterator<Suggest.Suggestion<? extends Entry<? extends Option>>> iterator = searchResponse
            .getSuggest().iterator();
        while (iterator.hasNext()) {
            iterator.next().getEntries().forEach(item -> {
                item.getOptions().forEach(option -> {
                    suggestionList.add(option.getText().string());
                });
            });
        }
        Arrays.stream(searchResponse.getHits().getHits())
            .filter(item -> StringUtils.isNoneBlank(item.getSourceAsString()))
            .forEach(item -> {
                if (Optional.ofNullable(item.getSourceAsMap().get(PARENT_ID_FIELD)).isPresent()) {
                    String id = String.valueOf(item.getSourceAsMap().get(PARENT_ID_FIELD));
                    multiGetRequest.add(new Item(esIndex, esType, id).fetchSourceContext(
                        new FetchSourceContext(true, INCLUDE_SEARCH_FIELD, EXCLUDE_SEARCH_FIELD)));
                    return;
                }
                questionList.add(makeSearchQuestion(item.getSourceAsString()));
            });
        if (multiGetRequest.getItems().size() > 0) {

            MultiGetResponse multiGetResponse = esClient.multiGet(multiGetRequest);
            Arrays.stream(multiGetResponse.getResponses())
                .filter(item -> StringUtils.isNotEmpty(item.getResponse().getSourceAsString()))
                .forEach(item -> {
                    questionList.add(makeSearchQuestion(item.getResponse().getSourceAsString()));
                });
        }

        return SearchResult.builder()
            .suggestList(suggestionList)
            .questionList(questionList)
            .build();
    }

    private Question makeSearchQuestion(String questionStr) {
        Question question = gson.fromJson(questionStr, Question.class);
        if (question.getBody().length() > 200) {
            question.setBody(question.getBody().substring(0, 200));
        }
        convertLineBreak(question);
        return question;
    }

    private SearchRequest makeSearchRequest(int pageNo, String content) {
        // multimatch query
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
            .multiMatchQuery(content, SEARCH_LIST_FIELD);

        //suggestion query
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion(TITLE_SUGGEST.getSuggest(),
            SuggestBuilders.termSuggestion(TITLE_FIELD).text(content).size(SEARCH_SIZE))
            .addSuggestion(BODY_SUGGEST.getSuggest(),
                SuggestBuilders.termSuggestion(BODY_FIELD).text(content).size(SEARCH_SIZE));

        // search query
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
            .query(multiMatchQueryBuilder).suggest(suggestBuilder)
            .fetchSource(INCLUDE_SEARCH_FIELD, EXCLUDE_SEARCH_FIELD)
            .from((pageNo - 1) * 10)
            .size(SEARCH_SIZE);

        return new SearchRequest(esIndex).types(esType)
            .source(searchSourceBuilder);
    }

    public PostResult getPost(String postId) throws NoneQuestionDataExcepion {
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
            .multiMatchQuery(postId, SEARCH_DETAIL_FIELD);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
            .query(multiMatchQueryBuilder);
        SearchRequest searchRequest = new SearchRequest(esIndex).types(esType)
            .source(searchSourceBuilder);
        SearchResponse searchResponse = esClient.search(searchRequest);
        List<Answer> answerList = new ArrayList<>();
        PostResult postResult = new PostResult();
        Arrays.stream(searchResponse.getHits().getHits()).forEach(item -> {
            if (Optional.ofNullable(item.getSourceAsMap().get(PARENT_ID_FIELD)).isPresent()) {
                Answer answer = gson.fromJson(item.getSourceAsString(), Answer.class);
                convertLineBreak(answer);
                if (answer.getBody().length() > 2000) {
                    answer.setBody(answer.getBody().substring(0, 2000));
                }
                answerList.add(answer);
                return;
            }
            Question question = gson.fromJson(item.getSourceAsString(), Question.class);
            convertLineBreak(question);
            if (question.getBody().length() > 2000) {
                question.setBody(question.getBody().substring(0, 2000));
            }
            postResult.setQuestion(question);
        });
        if (!Optional.ofNullable(postResult.getQuestion()).isPresent()) {
            throw new NoneQuestionDataExcepion();
        }

        Collections.sort(answerList, (o1, o2) -> {
            if (o1.getScore() > o2.getScore()) {
                return 1;
            } else if (o1.getScore() < o2.getScore()) {
                return -1;
            } else {
                return 0;
            }
        });
        postResult.setAnswerList(answerList);
        log.info("postResult => {}", gson.toJson(postResult));
        return postResult;
    }

    private void convertLineBreak(QnAMeta qnAMeta) {
        qnAMeta.setBody(qnAMeta.getBody().replaceAll("@", "\n"));
    }
}
