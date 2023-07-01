package com.bingchunmoli.api;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@SpringBootTest
public class DownloadHttpCatTempTest {

    /**
     * 仅用于下载状态码猫图片的代码
     * @throws IllegalAccessException if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof).
     */
    @Test
    @Disabled
    public void downloadHttpGet() throws IllegalAccessException {
        Set<Integer> set = new HashSet<>(100);
        for (HttpStatus value : HttpStatus.values()) {
            set.add(value.value());
        }
        System.out.println(set.size());
        Field[] fields = cn.hutool.http.HttpStatus.class.getFields();
        cn.hutool.http.HttpStatus obj = new cn.hutool.http.HttpStatus();
        for (Field field : fields) {
            Object o = field.get(obj);
            set.add((Integer) o);
        }
        System.out.println(set.size());
        Field[] fields1 = org.apache.http.HttpStatus.class.getFields();
        org.apache.http.HttpStatus obj2 = new org.apache.http.HttpStatus() {};
        for (Field field : fields1) {
            Object o = field.get(obj2);
            set.add((Integer) o);
        }
        System.out.println(set.size());
        for (Integer integer : set) {
            System.out.println("正在下载状态码: " + integer);
            try {
                HttpUtil.downloadFile("https://http.cat/" + integer + ".jpg", "img/" + integer + ".jpg");
            } catch (Exception e) {
                System.out.println("下载状态吗出错" + integer);
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("下载成功");

    }

}
