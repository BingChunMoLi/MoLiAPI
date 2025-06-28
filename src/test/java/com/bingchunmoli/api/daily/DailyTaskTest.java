package com.bingchunmoli.api.daily;

import com.bingchunmoli.api.daily.task.DailyTask;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author moli
 */

@Disabled
@SpringBootTest
//@ContextConfiguration(classes = {DailyTask.class, AccountServiceImpl.class})
public class DailyTaskTest {
    @Autowired
    private DailyTask dailyTask;

    @Test
    void dailyTest() throws Exception {
        dailyTask.daily();
    }
}
