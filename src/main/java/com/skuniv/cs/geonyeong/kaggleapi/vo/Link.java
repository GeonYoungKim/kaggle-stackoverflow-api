package com.skuniv.cs.geonyeong.kaggleapi.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Link {
    private String linkId;
    private String postId;
    private String relatedPostId;
}
