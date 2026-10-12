package com.bingchunmoli.api.shici;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.bingchunmoli.api.shici.bean.ShiCi;
import com.bingchunmoli.api.shici.mapper.ShiCiMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@MybatisPlusTest
@ContextConfiguration(classes = ShiCiMapperTest.TestConfig.class)
@TestPropertySource(properties = {
        "spring.sql.init.schema-locations=classpath:init/db/ddl.sql",
        "mybatis-plus.global-config.db-config.column-format=%s",
        "spring.datasource.url=jdbc:h2:mem:shici-tests;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"
})
class ShiCiMapperTest {
    @Autowired
    private ShiCiMapper mapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Configuration
    @EnableAutoConfiguration
    @MapperScan("com.bingchunmoli.api.shici.mapper")
    static class TestConfig {
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM shi_ci");
    }

    @Test
    void selectsOrderedLiveRowsAcrossIdGapsAndMapsFields() {
        jdbcTemplate.update("""
                INSERT INTO shi_ci (id, content, origin, author, category, deleted)
                VALUES (100, 'last', 'book', 'author', 'category', 0),
                       (2, 'first', 'book', 'author', 'category', 0),
                       (50, 'removed', 'book', 'author', 'category', 1)
                """);

        assertEquals(2L, mapper.selectCount(null));
        assertEquals(2L, mapper.findAtOffset(0).getId());
        final ShiCi last = mapper.findAtOffset(1);
        assertEquals(100L, last.getId());
        assertEquals("last", last.getContent());
        assertEquals("book", last.getOrigin());
        assertEquals("author", last.getAuthor());
        assertEquals("category", last.getCategory());
        assertNull(mapper.findAtOffset(2));
    }

    @Test
    void returnsNullForAnEmptyTable() {
        assertNull(mapper.findAtOffset(0));
    }
}
