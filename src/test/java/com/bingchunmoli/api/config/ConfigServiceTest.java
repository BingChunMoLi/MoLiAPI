package com.bingchunmoli.api.config;

import com.bingchunmoli.api.config.bean.Config;
import com.bingchunmoli.api.config.service.impl.ConfigServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class ConfigServiceTest {
    @Test
    void savesTheNewValueOnAnExistingConfiguration() {
        final ApiConfig live = new ApiConfig();
        live.setWeatherKey("old-placeholder");
        final ApiConfig requested = new ApiConfig();
        requested.setWeatherKey("new-placeholder");
        final Config existing = Config.builder().id(1).key("weatherKey").value("old-placeholder").build();
        final ConfigServiceImpl service = spy(new ConfigServiceImpl(live));
        doReturn(Optional.of(existing)).when(service).getOneOpt(any());
        doReturn(true).when(service).saveOrUpdate(any(Config.class));

        service.updateApiConfig(requested);

        verify(service).saveOrUpdate(existing);
        assertEquals(1, existing.getId());
        assertEquals("new-placeholder", existing.getValue());
        assertEquals("new-placeholder", live.getWeatherKey());
    }

    @Test
    void skipsPersistenceWhenConfigurationHasNotChanged() {
        final ConfigServiceImpl service = spy(new ConfigServiceImpl(new ApiConfig()));

        service.updateApiConfig(new ApiConfig());

        verify(service, never()).getOneOpt(any());
        verify(service, never()).saveOrUpdate(any(Config.class));
    }
}
