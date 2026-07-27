package com.bingchunmoli.api.host;

import com.bingchunmoli.api.controller.advice.ResponseControllerAdvice;
import com.bingchunmoli.api.host.controller.HostController;
import com.bingchunmoli.api.host.service.HostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HostControllerTest {
    private static final String HOSTS = "#start\n127.0.0.1 localhost\n#end";

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        final HostService hostService = mock(HostService.class);
        when(hostService.getHosts(anyList())).thenReturn(HOSTS);
        mockMvc = MockMvcBuilders.standaloneSetup(new HostController(hostService))
                .setControllerAdvice(new ResponseControllerAdvice())
                .build();
    }

    @Test
    void returnsWrappedJsonResponse() throws Exception {
        mockMvc.perform(get("/host/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value(HOSTS));
    }

    @Test
    void returnsRawTextResponse() throws Exception {
        mockMvc.perform(get("/host/raw"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(HOSTS));
    }

    @Test
    void returnsDownloadWithoutJsonWrapping() throws Exception {
        mockMvc.perform(get("/host/file"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"hosts\""
                ))
                .andExpect(content().bytes(HOSTS.getBytes(StandardCharsets.UTF_8)));
    }
}
