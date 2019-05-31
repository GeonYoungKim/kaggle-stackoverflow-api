package com.skuniv.cs.geonyeong.kaggleapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuggestType {
    TITLE_SUGGEST("title_suggest"),
    BODY_SUGGEST("body_suggest")
    ;
    private String suggest;
}
