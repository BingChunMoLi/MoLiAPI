package com.bingchunmoli.api.init;

import com.bingchunmoli.api.utils.InitUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author MoLi
 */
@Slf4j
@Order(1)
@Service
@RequiredArgsConstructor
public class InitOtherSchemaServiceImpl implements InitService {

    private final InitUtil initUtil;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void init() {
        initUtil.buildInit("other");
    }
}
