package com.bingchunmoli.api.cert.task;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import com.bingchunmoli.api.config.ApiConfig;
import com.tencentcloudapi.cdn.v20180606.CdnClient;
import com.tencentcloudapi.cdn.v20180606.models.DescribeDomainsConfigRequest;
import com.tencentcloudapi.cdn.v20180606.models.DescribeDomainsConfigResponse;
import com.tencentcloudapi.cdn.v20180606.models.DetailDomain;
import com.tencentcloudapi.cdn.v20180606.models.Https;
import com.tencentcloudapi.cdn.v20180606.models.ServerCert;
import com.tencentcloudapi.cdn.v20180606.models.UpdateDomainConfigRequest;
import com.tencentcloudapi.cdn.v20180606.models.UpdateDomainConfigResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.provider.ProfileCredentialsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @author admin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateTencentCdnCert {
    private final ApiConfig apiConfig;

    @Async
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateCert() throws TencentCloudSDKException {
        Credential cred = new ProfileCredentialsProvider().getCredentials();
        CdnClient client = new CdnClient(cred, Strings.EMPTY);
        DescribeDomainsConfigRequest req = new DescribeDomainsConfigRequest();
        DescribeDomainsConfigResponse resp = client.DescribeDomainsConfig(req);
        for (DetailDomain domain : resp.getDomains()) {
            if (!domain.getDomain().endsWith(apiConfig.getDomain())) {
                log.info("排除更新的域名: {}", domain.getDomain());
                continue;
            }
            String status = domain.getStatus();
            if ("rejected".equalsIgnoreCase(status) || "closing".equalsIgnoreCase(status) || "offline".equalsIgnoreCase(status)) {
                log.info("关闭或状态异常的域名不更新;域名:{}, 状态: {}", domain.getDomain(), domain.getStatus());
                continue;
            }
            log.info("更新证书的域名: {}", domain.getDomain());
            UpdateDomainConfigRequest request = new UpdateDomainConfigRequest();
            Https https = domain.getHttps();
            ServerCert serverCert = new ServerCert();
            //证书文件不大一次性读入内存问题不大.
            serverCert.setCertificate(FileUtil.readString(apiConfig.getCertificatePath(), StandardCharsets.UTF_8));
            serverCert.setPrivateKey(FileReader.create(new File(apiConfig.getPrivateKeyPath())).readString());
            https.setCertInfo(serverCert);
            request.setHttps(https);
            request.setDomain(domain.getDomain());
            UpdateDomainConfigResponse response = client.UpdateDomainConfig(request);
            log.info("domain: {},updateCertResponse: {}", domain.getDomain(), UpdateDomainConfigResponse.toJsonString(response));
        }
    }

}
