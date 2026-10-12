package com.bingchunmoli.api.config.web;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.stream.Stream;

public class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    public static final String VERSIONED_PATH_PREFIX = "/{apiVersion:v[0-9]+}";
    private static final String APPLICATION_PACKAGE = "com.bingchunmoli.api.";
    private static final String DEFAULT_API_VERSION = "1";

    @Override
    protected @Nullable RequestMappingInfo getMappingForMethod(final Method method, final Class<?> handlerType) {
        final RequestMappingInfo mapping = super.getMappingForMethod(method, handlerType);
        final boolean applicationApi = handlerType.getName().startsWith(APPLICATION_PACKAGE)
                && AnnotatedElementUtils.hasAnnotation(handlerType, RestController.class)
                && !ErrorController.class.isAssignableFrom(handlerType);
        if (mapping == null || !applicationApi) {
            return mapping;
        }

        final String[] paths = mapping.getPatternValues().stream()
                .flatMap(path -> Stream.of(path, VERSIONED_PATH_PREFIX + path))
                .toArray(String[]::new);
        final String declaredVersion = mapping.getVersionCondition().getVersion();
        final String version = declaredVersion == null ? DEFAULT_API_VERSION : declaredVersion;
        return mapping.mutate().paths(paths).version(version).build();
    }
}
