package com.bingchunmoli.api.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 日志注解
 * @author MoLi
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * info别名
     */
    @AliasFor("info")
    String value() default "";

    /**
     * 描述信息
     */
    @AliasFor("value")
    String info() default "";

    /**
     * 模块名称
     */
    String module() default "";
}
