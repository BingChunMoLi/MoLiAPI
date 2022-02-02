package com.bingchunmoli.api.host.task;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HostTaskTest {

    @Autowired
    HostTask hostTask;

    @Test
    void getHost(){
        hostTask.getHost();
    }
}