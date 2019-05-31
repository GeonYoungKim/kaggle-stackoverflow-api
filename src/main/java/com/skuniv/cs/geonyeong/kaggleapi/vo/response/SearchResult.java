package com.skuniv.cs.geonyeong.kaggleapi.vo.response;

import com.skuniv.cs.geonyeong.kaggleapi.vo.Question;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchResult {
    private List<Question> questionList;
    private List<String> suggestList;
}
