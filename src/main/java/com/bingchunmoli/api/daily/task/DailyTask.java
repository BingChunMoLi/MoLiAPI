package com.bingchunmoli.api.daily.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bingchunmoli.api.daily.bean.Account;
import com.bingchunmoli.api.daily.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author moli
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyTask {
    private final AccountService accountService;

    @Scheduled(cron = "0 0 8 * * *")
    public void daily() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        Account account = doGetAccount("bili");
        doSign(client, account);
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void sign52PoJie() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        Account account = doGetAccount("52pojie");
        doSign(client, account);
    }


    @Scheduled(cron = "0 0 8 * * *")
    public void signupyun() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        Account account = doGetAccount("upyun");
        doSign(client, account);
    }

    private Account doGetAccount(String type){
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getType, type);
        return accountService.getOne(queryWrapper);
    }

    private void doSign(HttpClient client, Account account) throws java.io.IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(account.getUrl()))
                .method(account.getMethod(), HttpRequest.BodyPublishers.noBody())
                .setHeader("sec-ch-ua-platform", "\"Windows\"")
                .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36")
                .setHeader("sec-ch-ua", "\"Chromium\";v=\"130\", \"Google Chrome\";v=\"130\", \"Not?A_Brand\";v=\"99\"")
                .setHeader("DNT", "1")
                .setHeader("sec-ch-ua-mobile", "?0")
                .setHeader("cookie", account.getCookies())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("response: {}", response.body());
    }
}
