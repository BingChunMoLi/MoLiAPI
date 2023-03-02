package com.bingchunmoli.api.cert.task;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import com.bingchunmoli.api.properties.ApiConfig;
import com.tencentcloudapi.cdn.v20180606.CdnClient;
import com.tencentcloudapi.cdn.v20180606.models.*;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.provider.ProfileCredentialsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
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

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateCert() throws TencentCloudSDKException {
        Credential cred = new ProfileCredentialsProvider().getCredentials();
        CdnClient client = new CdnClient(cred, Strings.EMPTY);
        DescribeDomainsConfigRequest req = new DescribeDomainsConfigRequest();
        DescribeDomainsConfigResponse resp = client.DescribeDomainsConfig(req);
        for (DetailDomain domain : resp.getDomains()) {
            if (!domain.getDomain().endsWith(apiConfig.getDomain())) {
                break;
            }
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
            log.info("updateCert: {}, {}, {}", domain, https, UpdateDomainConfigResponse.toJsonString(response));
        }
    }

}
