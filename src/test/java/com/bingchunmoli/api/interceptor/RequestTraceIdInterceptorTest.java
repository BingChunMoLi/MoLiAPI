package com.bingchunmoli.api.interceptor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class RequestTraceIdInterceptorTest {

    private static final String TRACE_ID = "traceId";
    private static final String SPAN_ID = "spanId";
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    private final RequestTraceIdInterceptor interceptor = new RequestTraceIdInterceptor();

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void preservesTracingContextAfterRequestCompletion() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final Object handler = new Object();
        MDC.put(TRACE_ID, "trace");
        MDC.put(SPAN_ID, "span");

        interceptor.preHandle(request, response, handler);

        final String requestId = MDC.get(RequestTraceIdInterceptor.REQUEST_ID);
        assertThat(requestId).isNotBlank();
        assertThat(response.getHeader(REQUEST_ID_HEADER)).isEqualTo(requestId);

        interceptor.afterCompletion(request, response, handler, null);

        assertThat(MDC.get(RequestTraceIdInterceptor.REQUEST_ID)).isNull();
        assertThat(MDC.get(TRACE_ID)).isEqualTo("trace");
        assertThat(MDC.get(SPAN_ID)).isEqualTo("span");
    }
}
