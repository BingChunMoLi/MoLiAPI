package com.bingchunmoli.api.navigation;

import com.bingchunmoli.api.navigation.bean.Navigation;
import com.bingchunmoli.api.navigation.bean.TagPO;
import com.bingchunmoli.api.navigation.mapper.NavigationMapper;
import com.bingchunmoli.api.navigation.servcie.impl.NavigationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NavigationServiceTest {
    private final NavigationMapper mapper = mock(NavigationMapper.class);
    private final NavigationServiceImpl service = new NavigationServiceImpl();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "baseMapper", mapper);
    }

    @Test
    void leavesTagsUntouchedWhenNavigationDoesNotExist() {
        final Navigation navigation = new Navigation();
        navigation.setId(1);
        navigation.setTagList(List.of(new TagPO()));

        assertFalse(service.updateNavigation(navigation));

        verify(mapper, never()).saveBatchTags(anyList());
        verify(mapper, never()).removeNavigationTagsByNavigationId(anyInt());
        verify(mapper, never()).saveNavigationTags(anyInt(), anyList());
    }

    @Test
    void propagatesTagInsertFailureBeforeReplacingRelations() {
        final Navigation navigation = new Navigation();
        navigation.setId(1);
        navigation.setTagList(List.of(new TagPO()));
        when(mapper.updateById(any(Navigation.class))).thenReturn(1);
        when(mapper.saveBatchTags(anyList())).thenThrow(new IllegalStateException("insert failed"));

        assertThrows(IllegalStateException.class, () -> service.updateNavigation(navigation));

        verify(mapper, never()).removeNavigationTagsByNavigationId(anyInt());
        verify(mapper, never()).saveNavigationTags(anyInt(), anyList());
    }
}
