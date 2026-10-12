package com.bingchunmoli.api.config.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.autoconfigure.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.RequestPath;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration(proxyBeanMethods = false)
public class ApiVersionWebMvcRegistrations implements WebMvcRegistrations, WebMvcConfigurer {

    private static final PathPattern VERSIONED_PATH = PathPatternParser.defaultInstance.parse(
            ApiVersionRequestMappingHandlerMapping.VERSIONED_PATH_PREFIX + "/**");
    private final String apiDocsPath;

    public ApiVersionWebMvcRegistrations(@Value("${springdoc.api-docs.path:/v3/api-docs}") final String apiDocsPath) {
        this.apiDocsPath = apiDocsPath;
    }

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new ApiVersionRequestMappingHandlerMapping();
    }

    @Override
    public void configureApiVersioning(final ApiVersionConfigurer configurer) {
        configurer.usePathSegment(0, this::isVersionedPath);
    }

    private boolean isVersionedPath(final RequestPath requestPath) {
        final String path = requestPath.pathWithinApplication().value();
        final boolean apiDocumentation = path.equals(apiDocsPath) || path.startsWith(apiDocsPath + "/")
                || path.equals(apiDocsPath + ".yaml");
        return !apiDocumentation && VERSIONED_PATH.matches(requestPath.pathWithinApplication());
    }
}
