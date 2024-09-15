package com.bingchunmoli.api;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author moli
 */
@MybatisPlusTest
public class DbBaseTests {

    @Autowired
    private DataSource dataSource;

    @Test
    void conn() throws SQLException {
        try (Connection con = dataSource.getConnection();
        PreparedStatement st = con.prepareStatement("select 1");
        ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                assertEquals(1, rs.getInt(1));
            }
        }
    }
}
