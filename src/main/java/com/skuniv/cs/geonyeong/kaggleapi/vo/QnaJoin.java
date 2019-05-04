package com.skuniv.cs.geonyeong.kaggleapi.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QnaJoin {
    private String name;
    private String parent;
}
