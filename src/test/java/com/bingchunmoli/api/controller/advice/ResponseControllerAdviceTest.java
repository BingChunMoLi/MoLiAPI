package com.bingchunmoli.api.controller.advice;

import com.bingchunmoli.api.bean.ResultVO;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ResponseControllerAdviceTest {
    private final ResponseControllerAdvice advice = new ResponseControllerAdvice();

    @Test
    void supportsJacksonJsonResponses() throws NoSuchMethodException {
        final MethodParameter returnType = returnType(TestController.class, "json");

        assertTrue(advice.supports(returnType, JacksonJsonHttpMessageConverter.class));
        assertFalse(advice.supports(returnType, StringHttpMessageConverter.class));
    }

    @Test
    void skipsRawResponseMethodsAndControllers() throws NoSuchMethodException {
        final MethodParameter rawMethod = returnType(TestController.class, "raw");
        final MethodParameter rawController = returnType(RawController.class, "json");

        assertFalse(advice.supports(rawMethod, JacksonJsonHttpMessageConverter.class));
        assertFalse(advice.supports(rawController, JacksonJsonHttpMessageConverter.class));
    }

    @Test
    void wrapsUnwrappedJsonBody() throws NoSuchMethodException {
        final MethodParameter returnType = returnType(TestController.class, "json");
        final Object result = advice.beforeBodyWrite(
                "value",
                returnType,
                MediaType.APPLICATION_JSON,
                JacksonJsonHttpMessageConverter.class,
                mock(ServerHttpRequest.class),
                mock(ServerHttpResponse.class)
        );

        final ResultVO<?> response = (ResultVO<?>) result;
        assertEquals("value", response.getData());
    }

    @Test
    void preservesExistingResult() throws NoSuchMethodException {
        final MethodParameter returnType = returnType(TestController.class, "json");
        final ResultVO<String> expected = ResultVO.ok("value");
        final Object result = advice.beforeBodyWrite(
                expected,
                returnType,
                MediaType.APPLICATION_JSON,
                JacksonJsonHttpMessageConverter.class,
                mock(ServerHttpRequest.class),
                mock(ServerHttpResponse.class)
        );

        assertSame(expected, result);
    }

    private MethodParameter returnType(final Class<?> controllerType, final String methodName)
            throws NoSuchMethodException {
        final Method method = controllerType.getDeclaredMethod(methodName);
        return new MethodParameter(method, -1);
    }

    private static class TestController {
        Object json() {
            return null;
        }

        @RawResponse
        Object raw() {
            return null;
        }
    }

    @RawResponse
    private static class RawController {
        Object json() {
            return null;
        }
    }
}
