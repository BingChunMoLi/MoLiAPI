package com.bingchunmoli.api.version;

import com.baomidou.mybatisplus.test.autoconfigure.AutoConfigureMybatisPlus;
import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.controller.VersionController;
import com.bingchunmoli.api.utils.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMybatisPlus
@WebMvcTest(VersionController.class)
public class VersionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${moli.version}")
    private String version;
    @MockBean
    private RedisUtil redisUtil;

    @Test
    void getVersion() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/version"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(ResultVO.ok(version))));
    }
}
