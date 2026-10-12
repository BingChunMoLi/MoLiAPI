package com.bingchunmoli.api.shici;

import com.bingchunmoli.api.controller.advice.ResponseControllerAdvice;
import com.bingchunmoli.api.shici.bean.ShiCi;
import com.bingchunmoli.api.shici.controller.ShiCiController;
import com.bingchunmoli.api.shici.service.ShiCiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ShiCiControllerTest {
    private final ShiCiService service = mock(ShiCiService.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ShiCiController(service))
                .setControllerAdvice(new ResponseControllerAdvice()).build();
    }

    @Test
    void returnsSinglePoemWithExistingResponseShape() {
        when(service.findRandomShiCi()).thenReturn(ShiCi.builder().id(42L).content("poem").deleted(0).build());

        assertDoesNotThrow(() -> mockMvc.perform(get("/shici/random"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data.id").value(42))
                .andExpect(jsonPath("$.data.content").value("poem"))
                .andExpect(jsonPath("$.data.deleted").doesNotExist()));
        verify(service).findRandomShiCi();
    }

    @Test
    void preservesEmptyResultResponse() {
        assertDoesNotThrow(() -> mockMvc.perform(get("/shici/random"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data").doesNotExist()));
    }
}
