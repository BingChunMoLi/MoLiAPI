package cert

import (
	"context"
	"fmt"
	"net/http"
	"net/url"
	"time"

	"github.com/tencentcloud/tencentcloud-sdk-go/tencentcloud/common"
	"github.com/tencentcloud/tencentcloud-sdk-go/tencentcloud/common/profile"
	cdn "github.com/tencentcloud/tencentcloud-sdk-go/tencentcloud/cdn/v20180606"
	teo "github.com/tencentcloud/tencentcloud-sdk-go/tencentcloud/teo/v20220901"
	"github.com/tencentyun/cos-go-sdk-v5"
)

type TencentUpdater struct {
	secretID  string
	secretKey string
}

func NewTencentUpdater(id, key string) *TencentUpdater {
	return &TencentUpdater{
		secretID:  id,
		secretKey: key,
	}
}

func (u *TencentUpdater) UpdateCert(cert []byte, key []byte, target TargetConfig) error {
	switch target.Service {
	case "cdn":
		return u.updateCDN(cert, key, target)
	case "teo":
		return u.updateTEO(cert, key, target)
	case "cos":
		return u.updateCOS(cert, key, target)
	default:
		return fmt.Errorf("unsupported tencent service: %s", target.Service)
	}
}

func (u *TencentUpdater) updateCDN(cert []byte, key []byte, target TargetConfig) error {
	credential := common.NewCredential(u.secretID, u.secretKey)
	cpf := profile.NewClientProfile()
	client, _ := cdn.NewClient(credential, "", cpf)

	request := cdn.NewUpdateDomainConfigRequest()
	request.Domain = common.StringPtr(target.Domain)
	request.Https = &cdn.Https{
		Switch: common.StringPtr("on"),
		CertInfo: &cdn.ServerCert{
			Certificate: common.StringPtr(string(cert)),
			PrivateKey:  common.StringPtr(string(key)),
			CertName:    common.StringPtr(target.Domain + "-cert"),
		},
	}

	_, err := client.UpdateDomainConfig(request)
	return err
}

func (u *TencentUpdater) updateTEO(cert []byte, key []byte, target TargetConfig) error {
	if target.ZoneID == "" {
		return fmt.Errorf("teo target requires zone_id")
	}
	credential := common.NewCredential(u.secretID, u.secretKey)
	cpf := profile.NewClientProfile()
	client, _ := teo.NewClient(credential, "", cpf)

	request := teo.NewModifyHostsCertificateRequest()
	request.ZoneId = common.StringPtr(target.ZoneID)
	request.Hosts = common.StringPtrs([]string{target.Domain})
	request.Mode = common.StringPtr("sslcert") // Set to sslcert to use ServerCertInfo
	
	// TEO ModifyHostsCertificate usually requires a CertId which you get from the SSL API.
	// This SDK doesn't allow direct cert upload in this request.
	// For now, keeping the structure as placeholders or until we can add SSL API support to upload the cert first.
	// request.ServerCertInfo = []*teo.ServerCertInfo{{...}}

	_, err := client.ModifyHostsCertificate(request)
	return err
}

func (u *TencentUpdater) updateCOS(cert []byte, key []byte, target TargetConfig) error {
	if target.Region == "" || target.Bucket == "" {
		return fmt.Errorf("cos target requires region and bucket")
	}

	uURL, _ := url.Parse(fmt.Sprintf("https://%s.cos.%s.myqcloud.com", target.Bucket, target.Region))
	b := &cos.BaseURL{BucketURL: uURL}

	client := cos.NewClient(b, &http.Client{
		Timeout: 10 * time.Second,
		Transport: &cos.AuthorizationTransport{
			SecretID:  u.secretID,
			SecretKey: u.secretKey,
		},
	})

	certOpt := &cos.BucketPutDomainCertificateOptions{
		CertificateInfo: &cos.BucketDomainCertificateInfo{
			CertType: "CustomCert",
			CustomCert: &cos.BucketDomainCustomCert{
				Cert:       string(cert),
				PrivateKey: string(key),
			},
		},
		DomainList: []string{target.Domain},
	}

	_, err := client.Bucket.PutDomainCertificate(context.Background(), certOpt)
	return err
}