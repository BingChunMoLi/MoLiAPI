package com.bingchunmoli.api.config;

import com.bingchunmoli.api.bing.bean.BingImageVO;
import com.bingchunmoli.api.down.bean.Fav;
import com.bingchunmoli.api.down.bean.Media;
import com.bingchunmoli.api.netease.PlayListBO;
import com.bingchunmoli.api.push.bean.AppMessage;
import com.bingchunmoli.api.push.bean.HttpMessage;
import com.bingchunmoli.api.push.bean.MailMessage;
import com.bingchunmoli.api.weather.bean.WeatherDailyBean;
import com.bingchunmoli.api.weather.bean.WeatherVO;
import com.bingchunmoli.api.yiyan.bean.YiYan;
import com.jthinking.common.util.ip.IPv4Info;
import com.jthinking.common.util.ip.IPv6Info;
import db.migration.V3__synchronize_application_schema;
import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class NativeRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(final RuntimeHints hints, final ClassLoader classLoader) {
        hints.resources().registerPattern("mapper/*.xml");
        hints.resources().registerPattern("db/migration/*");
        hints.resources().registerPattern("db/migration-postgresql/*.sql");
        hints.resources().registerPattern("db/migration-sqlite/*.sql");
        hints.resources().registerPattern("init/yiyan/*.json");
        hints.resources().registerPattern("img/*.jpg");
        hints.resources().registerPattern("config/logback-spring.xml");
        hints.resources().registerPattern("emojis.json");
        hints.resources().registerPattern("ip.zip");
        hints.resources().registerPattern("ipv6.zip");

        hints.reflection().registerType(ApiConfig.class,
                MemberCategory.ACCESS_DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_METHODS);
        hints.reflection().registerType(V3__synchronize_application_schema.class,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);

        new BindingReflectionHintsRegistrar().registerReflectionHints(hints.reflection(),
                BingImageVO.class, Fav.class, Media.class, PlayListBO.class, WeatherDailyBean.class,
                WeatherVO.class, YiYan.class, AppMessage.class, HttpMessage.class, MailMessage.class,
                IPv4Info.class, IPv6Info.class);
    }
}
