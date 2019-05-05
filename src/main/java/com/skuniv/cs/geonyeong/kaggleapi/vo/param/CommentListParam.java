package com.skuniv.cs.geonyeong.kaggleapi.vo.param;

import com.skuniv.cs.geonyeong.kaggleapi.vo.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CommentListParam {
    private List<Comment> commentList;
    private Integer commentCount;
}
