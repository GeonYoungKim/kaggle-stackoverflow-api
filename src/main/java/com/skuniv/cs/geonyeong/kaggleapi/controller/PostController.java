package com.skuniv.cs.geonyeong.kaggleapi.controller;

import com.skuniv.cs.geonyeong.kaggleapi.service.PostService;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Answer;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Comment;
import com.skuniv.cs.geonyeong.kaggleapi.vo.Question;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kaggle/stackoverflow/post")
public class PostController {
    private final PostService postService;

    @RequestMapping(value = "/search/{pageNo}", method = {RequestMethod.GET})
    public String search(@PathVariable int pageNo, @RequestParam(value = "content", defaultValue = "") String content) {
        return pageNo+content;
    }

    @RequestMapping(value = "/comment", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Comment insertComment(@RequestBody Comment comment) throws IOException {
        return postService.insertComment(comment);
    }

    @RequestMapping(value = "/answer", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Answer insertAnswer(@RequestBody Answer answer) throws IOException {
        return postService.insertAnswer(answer);
    }

    @RequestMapping(value = "/question", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Question insertQuestion(@RequestBody Question question) throws IOException {
        log.info("insertQuestion");
        return postService.insertQuestion(question);
    }

    @RequestMapping(value = "/comment", method = {RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Comment updateComment(@RequestBody Comment comment) {
        return postService.updateComment(comment);
    }

    @RequestMapping(value = "/answer", method = {RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Answer updateAnswer(@RequestBody Answer answer) {
        return postService.updateAnswer(answer);
    }

    @RequestMapping(value = "/question", method = {RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Question updateQuestion(@RequestBody Question question) {
        return postService.updateQuestion(question);
    }

    @RequestMapping(value = "/comment/{commentId}", method = {RequestMethod.DELETE}, produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteComment(@PathVariable String commentId) {
        return postService.deleteComment(commentId);
    }

    @RequestMapping(value = "/answer{answerId}", method = {RequestMethod.DELETE}, produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteAnswer(@PathVariable String answerId) {
        return postService.deleteAnswer(answerId);
    }

    @RequestMapping(value = "/question/{questionId}", method = {RequestMethod.DELETE}, produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteQuestion(@PathVariable String questionId) {
        return postService.deleteQuestion(questionId);
    }
}
