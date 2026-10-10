package com.bingchunmoli.api.navigation;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.bingchunmoli.api.navigation.bean.Navigation;
import com.bingchunmoli.api.navigation.bean.TagPO;
import com.bingchunmoli.api.navigation.mapper.NavigationMapper;
import com.bingchunmoli.api.navigation.servcie.NavigationService;
import com.bingchunmoli.api.navigation.servcie.impl.NavigationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MybatisPlusTest
@ContextConfiguration(classes = NavigationMapperTest.TestConfig.class)
@TestPropertySource(properties = {
        "spring.sql.init.schema-locations=classpath:init/db/ddl.sql",
        "mybatis-plus.global-config.db-config.column-format=%s",
        "spring.datasource.url=jdbc:h2:mem:navigation-tests;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"
})
class NavigationMapperTest {
    @Autowired
    private NavigationMapper navigationMapper;
    @Autowired
    private NavigationService navigationService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Configuration
    @EnableAutoConfiguration
    @MapperScan("com.bingchunmoli.api.navigation.mapper")
    @Import(NavigationServiceImpl.class)
    static class TestConfig {
    }

    @Test
    void insertsMultipleTagsAndMapsTheirGeneratedIds() {
        final List<TagPO> tags = List.of(tag("first"), tag("second"));
        final Navigation navigation = navigation(tags);

        assertTrue(navigationService.addNavigation(navigation));

        assertNotNull(tags.get(0).getId());
        assertNotNull(tags.get(1).getId());
        assertNotEquals(tags.get(0).getId(), tags.get(1).getId());
        final Navigation stored = navigationMapper.getNavigationsList("test").get(0);
        assertEquals(navigation.getId(), stored.getId());
        assertEquals(tagIds(tags), tagIds(stored.getTagList()));
    }

    @Test
    void updatesMultipleTagsWithoutChangingOtherRowsOrUnspecifiedFields() {
        final List<TagPO> tags = List.of(tag("first"), tag("second"), tag("untouched"));
        navigationMapper.saveBatchTags(tags);
        final TagPO firstUpdate = new TagPO();
        firstUpdate.setId(tags.get(0).getId());
        firstUpdate.setTagName("renamed");
        firstUpdate.setIsOpen(false);
        final TagPO secondUpdate = new TagPO();
        secondUpdate.setId(tags.get(1).getId());
        secondUpdate.setIsPrivate(true);
        secondUpdate.setPwd("new-placeholder");

        assertEquals(2, navigationMapper.updateBatchTags(List.of(firstUpdate, secondUpdate)));

        assertEquals("renamed", tagColumn("tag_name", firstUpdate.getId(), String.class));
        assertEquals(false, tagColumn("is_open", firstUpdate.getId(), Boolean.class));
        assertEquals("placeholder", tagColumn("pwd", firstUpdate.getId(), String.class));
        assertEquals("second", tagColumn("tag_name", secondUpdate.getId(), String.class));
        assertEquals(true, tagColumn("is_private", secondUpdate.getId(), Boolean.class));
        assertEquals("new-placeholder", tagColumn("pwd", secondUpdate.getId(), String.class));
        assertEquals("untouched", tagColumn("tag_name", tags.get(2).getId(), String.class));
        assertEquals(3, jdbcTemplate.queryForObject("select count(*) from tag", Integer.class));
    }

    @Test
    void skipsEmptyBatchUpdatesAndAcceptsAnUnchangedTag() {
        final TagPO original = tag("unchanged");
        navigationMapper.saveBatchTags(List.of(original));
        final TagPO unchanged = new TagPO();
        unchanged.setId(original.getId());

        assertEquals(0, navigationMapper.updateBatchTags(List.of()));
        assertEquals(0, navigationMapper.updateBatchTags(null));
        assertEquals(1, navigationMapper.updateBatchTags(List.of(unchanged)));
        assertEquals("unchanged", tagColumn("tag_name", original.getId(), String.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"existing", "new", "mixed", "empty", "omitted"})
    void rebuildsOnlyTheUpdatedNavigationsRelations(final String scenario) {
        final List<TagPO> originalTags = List.of(tag("original-first"), tag("original-second"));
        final Navigation original = navigation(originalTags);
        assertTrue(navigationService.addNavigation(original));
        final Navigation other = navigation(List.of());
        navigationMapper.insert(other);
        navigationMapper.saveNavigationTags(other.getId(), List.of(originalTags.get(1).getId()));
        final TagPO newTag = tag("new-tag");
        final List<TagPO> requestedTags = switch (scenario) {
            case "existing" -> List.of(originalTags.get(0));
            case "new" -> List.of(newTag);
            case "mixed" -> List.of(originalTags.get(0), newTag);
            case "empty" -> List.of();
            default -> null;
        };
        final Navigation update = navigation(requestedTags);
        update.setId(original.getId());
        update.setTitle("updated");

        assertTrue(navigationService.updateNavigation(update));

        final List<TagPO> expectedTags = requestedTags == null ? originalTags : requestedTags;
        assertEquals(tagIds(expectedTags), relationIds(original.getId()));
        assertEquals(List.of(originalTags.get(1).getId()), relationIds(other.getId()));
        final boolean insertsNewTag = "new".equals(scenario) || "mixed".equals(scenario);
        assertEquals(insertsNewTag ? 3 : 2, jdbcTemplate.queryForObject("select count(*) from tag", Integer.class));
        assertEquals("updated", navigationMapper.selectById(original.getId()).getTitle());
    }

    private List<Integer> relationIds(final Integer navigationId) {
        return jdbcTemplate.queryForList(
                "select tag_id from navigation_tag where navigation_id = ? order by tag_id",
                Integer.class, navigationId);
    }

    private <T> T tagColumn(final String column, final Integer tagId, final Class<T> type) {
        return jdbcTemplate.queryForObject("select " + column + " from tag where id = ?", type, tagId);
    }

    private static List<Integer> tagIds(final List<TagPO> tags) {
        return tags.stream().map(TagPO::getId).sorted().toList();
    }

    private static TagPO tag(final String name) {
        final TagPO tag = new TagPO();
        tag.setTagName(name);
        tag.setIsOpen(true);
        tag.setIsPrivate(false);
        tag.setPwd("placeholder");
        return tag;
    }

    private static Navigation navigation(final List<TagPO> tags) {
        final Navigation navigation = new Navigation();
        navigation.setTitle("title");
        navigation.setUrl("https://example.com");
        navigation.setTenant("test");
        navigation.setTagList(tags);
        return navigation;
    }
}
