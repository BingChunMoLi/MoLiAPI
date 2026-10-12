package com.bingchunmoli.api.version;

import com.baomidou.mybatisplus.test.autoconfigure.AutoConfigureMybatisPlus;
import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.config.ApiConfig;
import com.bingchunmoli.api.config.SystemController;
import com.bingchunmoli.api.config.service.ConfigService;
import com.bingchunmoli.api.config.web.ApiVersionWebMvcRegistrations;
import com.bingchunmoli.api.config.web.WebMvcConfig;
import com.bingchunmoli.api.controller.VersionController;
import com.bingchunmoli.api.controller.advice.ExceptionControllerAdvice;
import com.bingchunmoli.api.interceptor.IpInterceptor;
import com.bingchunmoli.api.interceptor.RequestTraceIdInterceptor;
import com.bingchunmoli.api.interceptor.SystemAuthenticationInterceptor;
import com.bingchunmoli.api.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMybatisPlus
@WebMvcTest(VersionController.class)
@Import({VersionController.class, SystemController.class, ApiConfig.class,
        ApiVersionWebMvcRegistrations.class, WebMvcConfig.class, IpInterceptor.class,
        RequestTraceIdInterceptor.class, SystemAuthenticationInterceptor.class,
        ExceptionControllerAdvice.class, VersionControllerTest.VersionedProbeController.class,
        VersionControllerTest.ApiDocumentationProbeController.class})
public class VersionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${moli.version}")
    private String version;
    @MockitoBean
    private RedisUtil redisUtil;
    @MockitoBean
    private ConfigService configService;

    @SpringBootConfiguration
    static class TestApplication {
    }

    @Test
    void getVersion() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/version"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(ResultVO.ok(version))));
    }

    @ParameterizedTest
    @CsvSource({"/version,getVersion", "/v1/version,getVersion", "/v2/version,getVersionV2"})
    void routesApplicationVersionQuery(final String path, final String method) {
        assertDoesNotThrow(() -> mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(handler().methodName(method))
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data").value(version)));
    }

    @ParameterizedTest
    @CsvSource({"/version-probe/42,v1:42", "/v1/version-probe/42,v1:42", "/v2/version-probe/42,v2:42"})
    void dispatchesToTheDeclaredNativeVersion(final String path, final String expected) {
        assertDoesNotThrow(() -> mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(expected)));
    }

    @Test
    void ignoresTheOldHeaderWhenThePathHasNoPrefix() {
        assertDoesNotThrow(() -> mockMvc.perform(get("/version-probe/42").header("moli-version", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("v1:42")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/v0/version", "/v3/version", "/v999999999999/version", "/v2/system"})
    void rejectsUnsupportedVersionsAndDoesNotFallBackToV1(final String path) {
        assertDoesNotThrow(() -> mockMvc.perform(get(path))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("A0001")));
        verifyNoInteractions(configService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/system", "/v1/system"})
    void protectsConfigurationThroughBothPaths(final String path) {
        assertDoesNotThrow(() -> mockMvc.perform(get(path)).andExpect(status().isUnauthorized()));
        verifyNoInteractions(configService);
    }

    @Test
    void permitsAuthenticatedV1ConfigurationRequests() {
        assertDoesNotThrow(() -> mockMvc.perform(get("/v1/system").sessionAttr("user", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000")));
    }

    @Test
    void resolvesVersionsRelativeToTheServletContext() {
        assertDoesNotThrow(() -> mockMvc.perform(get("/api/v2/version-probe/42").contextPath("/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("v2:42")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/v3/api-docs", "/v3/api-docs.yaml", "/v3/api-docs/swagger-config"})
    void keepsOpenApiPathsOutsideBusinessApiVersioning(final String path) {
        assertDoesNotThrow(() -> mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("OpenAPI")));
    }

    @RestController
    static class ApiDocumentationProbeController {

        @GetMapping({"/v3/api-docs", "/v3/api-docs.yaml", "/v3/api-docs/swagger-config"})
        public ResultVO<String> documentation() {
            return ResultVO.ok("OpenAPI");
        }
    }

    @RestController
    @RequestMapping("/version-probe")
    static class VersionedProbeController {

        @GetMapping(path = "/{id}", version = "1")
        public ResultVO<String> v1(@PathVariable final int id) {
            return ResultVO.ok("v1:" + id);
        }

        @GetMapping(path = "/{id}", version = "2")
        public ResultVO<String> v2(@PathVariable final int id) {
            return ResultVO.ok("v2:" + id);
        }
    }
}
