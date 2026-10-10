package com.bingchunmoli.api.config;

import com.bingchunmoli.api.app.DeviceService;
import com.bingchunmoli.api.config.service.ConfigService;
import com.bingchunmoli.api.config.web.WebMvcConfig;
import com.bingchunmoli.api.daily.bean.DailyLog;
import com.bingchunmoli.api.daily.bean.DailyQuery;
import com.bingchunmoli.api.daily.controller.DailyController;
import com.bingchunmoli.api.daily.service.DailyLogService;
import com.bingchunmoli.api.interceptor.IpInterceptor;
import com.bingchunmoli.api.interceptor.RequestTraceIdInterceptor;
import com.bingchunmoli.api.interceptor.SystemAuthenticationInterceptor;
import com.bingchunmoli.api.navigation.bean.Navigation;
import com.bingchunmoli.api.navigation.controller.NavigationController;
import com.bingchunmoli.api.navigation.servcie.NavigationService;
import com.bingchunmoli.api.utils.RedisUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WebMvcRegressionTest {
    private AnnotationConfigWebApplicationContext context;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.register(TestConfig.class);
        context.refresh();
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @AfterEach
    void tearDown() {
        context.close();
    }

    @ParameterizedTest
    @ValueSource(strings = {"moli", "new-group"})
    void addsASiteToDefaultAndNewGroups(final String group) {
        assertDoesNotThrow(() -> mockMvc.perform(put("/daily").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"" + group + "\",\"value\":\"https://example.com/added\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data[?(@ == 'https://example.com/added')]").isNotEmpty()));
    }

    @Test
    void addsMultipleSitesToTheDefaultGroup() {
        assertDoesNotThrow(() -> {
            mockMvc.perform(put("/daily/all").contentType(MediaType.APPLICATION_JSON).content("""
                    [{"key":"moli","value":"https://example.com/first"},
                     {"key":"moli","value":"https://example.com/second"}]
                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.length()").value(2));
            mockMvc.perform(get("/daily").param("key", "moli"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.length()").value(5));
        });
    }

    @Test
    void passesMultipleTagsAndThePathIdToNavigationUpdates() {
        final NavigationService service = context.getBean(NavigationService.class);
        when(service.updateNavigation(any())).thenReturn(true);

        assertDoesNotThrow(() -> mockMvc.perform(post("/navigation/42").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"updated","tagList":[{"id":7,"tagName":"old"},{"tagName":"new"}]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true)));
        verify(service).updateNavigation(argThat(navigation -> navigation.getId() == 42
                && navigation.getTagList().size() == 2 && navigation.getTagList().get(0).getId() == 7));
    }

    @Test
    void rejectsInvalidNavigationUrlsBeforeCallingTheService() {
        assertDoesNotThrow(() -> mockMvc.perform(put("/navigation").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"invalid-url\"}"))
                .andExpect(status().isBadRequest()));
        verify(context.getBean(NavigationService.class), never()).addNavigation(any(Navigation.class));
    }

    @Test
    void passesInclusiveCalendarDatesAndReturnsTheLastDay() {
        final LocalDate lastDay = LocalDate.of(2026, 10, 31);
        final DailyQuery query = DailyQuery.builder().startDate(LocalDate.of(2026, 10, 1)).endDate(lastDay).build();
        final DailyLog log = DailyLog.builder().url("https://example.com").createTime(lastDay).build();
        when(context.getBean(DailyLogService.class).querySign(query, 1)).thenReturn(Map.of(lastDay, List.of(log)));

        assertDoesNotThrow(() -> mockMvc.perform(get("/daily/query")
                        .param("startDate", "2026-10-01").param("endDate", "2026-10-31").header("tenant", "moli"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data['2026-10-31'][0].url").value("https://example.com")));
        verify(context.getBean(DailyLogService.class)).querySign(query, 1);
    }

    @Test
    void acceptsAuthenticatedConfigurationUpdates() {
        final MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", 1);

        assertDoesNotThrow(() -> mockMvc.perform(post("/system").session(session)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"weatherKey\":\"new-placeholder\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true)));
        verify(context.getBean(ConfigService.class)).updateApiConfig(
                argThat(config -> "new-placeholder".equals(config.getWeatherKey())));
    }

    @Test
    void preservesAuthenticationForConfigurationUpdates() {
        assertDoesNotThrow(() -> mockMvc.perform(post("/system").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"weatherKey\":\"new-placeholder\"}"))
                .andExpect(status().isUnauthorized()));
        verify(context.getBean(ConfigService.class), never()).updateApiConfig(any());
    }

    @Test
    void allowsTenantPreflightAndKeepsOtherHeadersRestricted() {
        assertDoesNotThrow(() -> {
            mockMvc.perform(options("/navigation").header("Origin", "https://admin.example")
                            .header("Access-Control-Request-Method", "GET")
                            .header("Access-Control-Request-Headers", "tenant,content-type"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Headers", "tenant, content-type"))
                    .andExpect(header().string("Access-Control-Allow-Origin", "https://admin.example"))
                    .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
            mockMvc.perform(options("/navigation").header("Origin", "https://admin.example")
                            .header("Access-Control-Request-Method", "GET")
                            .header("Access-Control-Request-Headers", "unsupported-header"))
                    .andExpect(status().isForbidden());
        });
    }

    @Configuration
    @EnableWebMvc
    static class TestConfig {
        @Bean
        WebMvcConfig webMvcConfig() {
            return new WebMvcConfig(new IpInterceptor(mock(RedisUtil.class)), new RequestTraceIdInterceptor(),
                    new SystemAuthenticationInterceptor());
        }

        @Bean
        NavigationService navigationService() {
            return mock(NavigationService.class);
        }

        @Bean
        NavigationController navigationController(final NavigationService service) {
            return new NavigationController(service);
        }

        @Bean
        DailyLogService dailyLogService() {
            return mock(DailyLogService.class);
        }

        @Bean
        DailyController dailyController(final DailyLogService service, final ApplicationEventPublisher publisher) {
            return new DailyController(service, mock(DeviceService.class), publisher);
        }

        @Bean
        ConfigService configService() {
            return mock(ConfigService.class);
        }

        @Bean
        SystemController systemController(final ConfigService service) {
            return new SystemController(new ApiConfig(), service);
        }
    }
}
