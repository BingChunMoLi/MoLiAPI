package com.bingchunmoli.api.down.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * @author BingChunMoLi
 */
@Slf4j
public class ShellUtil {

    public static Boolean exec(String... args) throws IOException, InterruptedException {
        Process exec = Runtime.getRuntime().exec(args);
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                String line;
                BufferedReader error = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
                while ((line = error.readLine()) != null) {
                    log.error(line);
                }
                error.close();
            }
        }).start();
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                BufferedReader input = new BufferedReader(new InputStreamReader(exec.getInputStream()));
                String line;
                while ((line = input.readLine()) != null) {
                    log.info(line);
                }
                input.close();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                OutputStream outputStream = exec.getOutputStream();
                PrintWriter printWriter = new PrintWriter(outputStream);
                printWriter.println();
                printWriter.flush();
                printWriter.close();
            }
        });
        exec.waitFor();
        exec.destroy();
        return true;
    }
}
