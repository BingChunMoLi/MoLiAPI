package cert

import (
	"fmt"
	"log"

	"github.com/aliyun/alibaba-cloud-sdk-go/services/cdn"
	"github.com/aliyun/alibaba-cloud-sdk-go/services/dcdn"
	"github.com/aliyun/aliyun-oss-go-sdk/oss"
)

type AliyunUpdater struct {
	accessKeyID     string
	accessKeySecret string
}

func NewAliyunUpdater(ak, sk string) *AliyunUpdater {
	return &AliyunUpdater{
		accessKeyID:     ak,
		accessKeySecret: sk,
	}
}

func (u *AliyunUpdater) UpdateCert(cert []byte, key []byte, target TargetConfig) error {
	switch target.Service {
	case "cdn":
		return u.updateCDN(cert, key, target)
	case "dcdn":
		return u.updateDCDN(cert, key, target)
	case "oss":
		return u.updateOSS(cert, key, target)
	default:
		return fmt.Errorf("unsupported aliyun service: %s", target.Service)
	}
}

func (u *AliyunUpdater) updateCDN(cert []byte, key []byte, target TargetConfig) error {
	client, err := cdn.NewClientWithAccessKey("cn-hangzhou", u.accessKeyID, u.accessKeySecret)
	if err != nil {
		return err
	}

	request := cdn.CreateSetCdnDomainSSLCertificateRequest()
	request.Scheme = "https"
	request.DomainName = target.Domain
	request.CertName = target.Domain + "-cert"
	request.CertType = "upload"
	request.SSLProtocol = "on"
	request.SSLPri = string(key)
	request.SSLPub = string(cert)

	_, err = client.SetCdnDomainSSLCertificate(request)
	return err
}

func (u *AliyunUpdater) updateDCDN(cert []byte, key []byte, target TargetConfig) error {
	client, err := dcdn.NewClientWithAccessKey("cn-hangzhou", u.accessKeyID, u.accessKeySecret)
	if err != nil {
		return err
	}

	request := dcdn.CreateSetDcdnDomainSSLCertificateRequest()
	request.Scheme = "https"
	request.DomainName = target.Domain
	request.CertName = target.Domain + "-cert"
	request.CertType = "upload"
	request.SSLProtocol = "on"
	request.SSLPri = string(key)
	request.SSLPub = string(cert)

	_, err = client.SetDcdnDomainSSLCertificate(request)
	return err
}

func (u *AliyunUpdater) updateOSS(cert []byte, key []byte, target TargetConfig) error {
	if target.Endpoint == "" || target.Bucket == "" {
		return fmt.Errorf("oss target requires endpoint and bucket")
	}
	client, err := oss.New(target.Endpoint, u.accessKeyID, u.accessKeySecret)
	if err != nil {
		return err
	}

	bucket, err := client.Bucket(target.Bucket)
	if err != nil {
		return err
	}

	// For OSS, we use PutBucketCname with the certificate info.
	certConf := oss.CertificateConfiguration{
		Certificate: string(cert),
		PrivateKey:  string(key),
		PreviousCertId: "",
		Force: true,
	}
	err = bucket.PutBucketCnameWithCertificate(target.Domain, certConf)
	if err != nil {
		log.Printf("PutBucketCnameWithCertificate error: %v", err)
		return err
	}
	return nil
}