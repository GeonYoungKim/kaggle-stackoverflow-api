package com.skuniv.cs.geonyeong.kaggleapi;

import com.skuniv.cs.geonyeong.kaggleapi.configuration.EsConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class UpdateTest {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void test() throws IOException {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "shinjiin");
        UpdateRequest request = new UpdateRequest("test","_doc", "0")
                .doc(jsonMap);
        restHighLevelClient.update(request, RequestOptions.DEFAULT);
    }
}
