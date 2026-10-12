package com.bingchunmoli.api.config;

import com.bingchunmoli.api.interceptor.IpInterceptor;
import com.bingchunmoli.api.interceptor.RequestTraceIdInterceptor;
import com.bingchunmoli.api.interceptor.SystemAuthenticationInterceptor;
import com.bingchunmoli.api.listener.MessageEvenListener;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AotValidationMetadataTest {

    @ParameterizedTest
    @ValueSource(classes = {IpInterceptor.class, RequestTraceIdInterceptor.class,
            SystemAuthenticationInterceptor.class, MessageEvenListener.class})
    void canInferValidationMetadataForFrameworkCallbacks(final Class<?> callbackType) {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            assertDoesNotThrow(() -> factory.getValidator().getConstraintsForClass(callbackType));
        }
    }
}
