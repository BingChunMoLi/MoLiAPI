package com.bingchunmoli.api.shici;

import com.bingchunmoli.api.shici.bean.ShiCi;
import com.bingchunmoli.api.shici.mapper.ShiCiMapper;
import com.bingchunmoli.api.shici.service.impl.ShiCiServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShiCiServiceTest {
    private final ShiCiMapper mapper = mock(ShiCiMapper.class);
    private final ShiCiServiceImpl service = new ShiCiServiceImpl();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "baseMapper", mapper);
    }

    @Test
    void returnsNullWithoutSelectingFromAnEmptyTable() {
        when(mapper.selectCount(any())).thenReturn(0L);

        assertNull(service.findRandomShiCi());
        verify(mapper, never()).findAtOffset(anyLong());
        verify(mapper, never()).selectList(any());
    }

    @Test
    void readsOneRowWithoutLoadingTheTable() {
        final ShiCi poem = ShiCi.builder().id(42L).content("poem").build();
        when(mapper.selectCount(any())).thenReturn(1L);
        when(mapper.findAtOffset(0)).thenReturn(poem);

        assertSame(poem, service.findRandomShiCi());
        verify(mapper, never()).selectList(any());
    }

    @Test
    void observesDatabaseChangesOnLaterRequests() {
        final ShiCi first = ShiCi.builder().id(1L).build();
        final ShiCi replacement = ShiCi.builder().id(2L).build();
        when(mapper.selectCount(any())).thenReturn(1L, 0L, 1L);
        when(mapper.findAtOffset(0)).thenReturn(first, replacement);

        assertSame(first, service.findRandomShiCi());
        assertNull(service.findRandomShiCi());
        assertSame(replacement, service.findRandomShiCi());
    }

    @Test
    void boundsRandomOffsetForLargeTables() {
        final long count = (long) Integer.MAX_VALUE + 1;
        when(mapper.selectCount(any())).thenReturn(count);

        service.findRandomShiCi();

        final ArgumentCaptor<Long> offset = ArgumentCaptor.forClass(Long.class);
        verify(mapper).findAtOffset(offset.capture());
        assertTrue(offset.getValue() >= 0 && offset.getValue() < count);
    }
}
