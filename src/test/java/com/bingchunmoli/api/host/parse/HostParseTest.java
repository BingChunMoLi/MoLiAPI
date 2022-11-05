package com.bingchunmoli.api.host.parse;

import com.bingchunmoli.api.host.bean.Host;
import com.bingchunmoli.api.host.service.HostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public class HostParseTest {

    @Autowired
    private HostService hostService;
    @Test
    void HostParseFromFile(){
        try {
            List<Host> list = HostParse.parseFromFile("D:\\hosts.local", "local");
            System.out.println(hostService.saveBatch(list));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    void HostParseFromFileAD(){
        try {
            List<Host> list = HostParse.parseFromFile("D:\\hosts.ad", "myAd");
            System.out.println(hostService.saveBatch(list));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
