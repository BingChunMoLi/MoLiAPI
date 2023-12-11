package com.bingchunmoli.api.push;

import com.bingchunmoli.api.push.bean.Message;
import com.bingchunmoli.api.push.bean.SendParam;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public abstract class PushHttp implements Push {

    protected String secret;

    @Override
    public String send(Message message) {
        SendParam sendParam = buildSend(message);
        return doSend(sendParam);
    }

    /**
     * 构建请求参数以供请求
     * @param message url、method、body
     * @return 请求参数
     */
    protected abstract SendParam buildSend(Message message);

    /**
     * 默认使用get和nobody请求如需要post等其他一定需要重写该方法
     * @param sendParam 仅使用默认get及nobody
     * @return 请求成功结果
     */
    protected String doSend(SendParam sendParam) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()){
            HttpGet get = new HttpGet(sendParam.getUrl());
            try (CloseableHttpResponse response = httpClient.execute(get)){
                return EntityUtils.toString(response.getEntity());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
