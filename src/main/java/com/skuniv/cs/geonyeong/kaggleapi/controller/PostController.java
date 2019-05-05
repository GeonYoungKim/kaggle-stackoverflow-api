package com.skuniv.cs.geonyeong.kaggleapi.controller;

import com.skuniv.cs.geonyeong.kaggleapi.service.PostService;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Answer;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Comment;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Question;
import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@Api(value = "/api/v1/kaggle/stackoverflow/post", tags = {"Kaggle Stackoverflow Post" })
@SwaggerDefinition(tags = {
        @Tag(name = "Kaggle Stackoverflow Post", description = "캐글 스택오버플로우 Post관련 controller")
})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kaggle/stackoverflow/post")
public class PostController {
    private final PostService postService;

    @RequestMapping(value = "/search/{pageNo}", method = {RequestMethod.GET})
    public String search(@PathVariable int pageNo, @RequestParam(value = "content", defaultValue = "") String content) {
        return pageNo+content;
    }

    @RequestMapping(value = "/question", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Question createQuestion(@RequestBody Question question) {
        return postService.createQuestion(question);
    }

    @RequestMapping(value = "/question", method = {RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Question updateQuestion(@RequestBody Question question) {
        return postService.updateQuestion(question);
    }

    @RequestMapping(value = "/question/{questionId}", method = {RequestMethod.DELETE}, produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteQuestion(@PathVariable String questionId) {
        return postService.deleteQuestion(questionId);
    }

    @RequestMapping(value = "/answer", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Answer createAnswer(@RequestBody Answer answer) {
        return postService.createAnswer(answer);
    }
    @RequestMapping(value = "/answer", method = {RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Answer updateAnswer(@RequestBody Answer answer) {
        return postService.updateAnswer(answer);
    }

    @RequestMapping(value = "/answer", method = {RequestMethod.DELETE}, produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteAnswer(@RequestParam("answerId") String answerId, @RequestParam("questionId") String questionId) {
        return postService.deleteAnswer(answerId, questionId);
    }

    @RequestMapping(value = "/comment", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Comment createComment(@RequestBody Comment comment)  {
        return postService.createComment(comment);
    }
    @RequestMapping(value = "/comment", method = {RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Comment updateComment(@RequestBody Comment comment) {
        return postService.updateComment(comment);
    }

    @RequestMapping(value = "/comment", method = {RequestMethod.DELETE}, produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteComment(@RequestParam("commentId") String commentId, @RequestParam("postId") String postId) {
        return postService.deleteComment(commentId, postId);
    }
}
