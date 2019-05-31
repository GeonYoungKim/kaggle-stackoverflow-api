package com.skuniv.cs.geonyeong.kaggleapi.controller;

import com.google.gson.Gson;
import com.skuniv.cs.geonyeong.kaggleapi.exception.NoneQuestionDataExcepion;
import com.skuniv.cs.geonyeong.kaggleapi.service.PostService;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Answer;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Comment;
import com.skuniv.cs.geonyeong.kaggleapi.vo.QnaJoin;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Question;
import com.skuniv.cs.geonyeong.kaggleapi.vo.response.PostResult;
import com.skuniv.cs.geonyeong.kaggleapi.vo.response.SearchResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin("*")
@Api(value = "/api/v1/kaggle/stackoverflow/post", tags = {"Kaggle Stackoverflow Post"})
@SwaggerDefinition(tags = {
    @Tag(name = "Kaggle Stackoverflow Post", description = "캐글 스택오버플로우 Post관련 controller")
})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kaggle/stackoverflow/post")
public class PostController {
    private final static Gson gson = new Gson();
    private final PostService postService;

    @RequestMapping(value = "/search/{pageNo}", method = {RequestMethod.GET})
    public SearchResult search(
        @PathVariable int pageNo,
        @RequestParam(value = "content", defaultValue = "") String content) {
        SearchResult searchResult = postService.search(pageNo, content);
        log.info("searchresult => {}", gson.toJson(searchResult));
        return searchResult;
    }

    @RequestMapping(value = "/{postId}", method = {
        RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    public PostResult selectPost(@PathVariable String postId) throws NoneQuestionDataExcepion {
        log.info("postId => {}", postId);
        PostResult postResult = postService.getPost(postId);
        log.info("postResult => {}", postResult);
        return postResult;
    }

    @RequestMapping(value = "/question", method = {
        RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Question createQuestion(@RequestBody Question question) {
        return postService.createQuestion(question);
    }

    @RequestMapping(value = "/question", method = {
        RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Question updateQuestion(@RequestBody Question question) {
        return postService.updateQuestion(question);
    }

    @RequestMapping(value = "/question/{questionId}", method = {RequestMethod.DELETE})
    public String deleteQuestion(@PathVariable String questionId) {
        return postService.deleteQuestion(questionId);
    }

    @RequestMapping(value = "/answer", method = {
        RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Answer createAnswer(@RequestBody Answer answer) {
        return postService.createAnswer(answer);
    }

    @RequestMapping(value = "/answer", method = {
        RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Answer updateAnswer(@RequestBody Answer answer) {
        return postService.updateAnswer(answer);
    }

    @RequestMapping(value = "/answer", method = {RequestMethod.DELETE})
    public String deleteAnswer(@RequestParam("answerId") String answerId,
        @RequestParam("questionId") String questionId) {
        return postService.deleteAnswer(answerId, questionId);
    }

    @RequestMapping(value = "/comment", method = {
        RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Comment createComment(@RequestBody Comment comment) {
        return postService.createComment(comment);
    }

    @RequestMapping(value = "/comment", method = {
        RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Comment updateComment(@RequestBody Comment comment) {
        return postService.updateComment(comment);
    }

    @RequestMapping(value = "/comment", method = {RequestMethod.DELETE})
    public String deleteComment(@RequestParam("commentId") String commentId,
        @RequestParam("postId") String postId) {
        return postService.deleteComment(commentId, postId);
    }
}
