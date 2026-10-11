package application

import (
	"bytes"
	"encoding/base64"
	"fmt"
	"image"
	"image/color"
	"image/draw"
	_ "image/gif"
	"image/jpeg"
	_ "image/png"
	"math/big"
	"math/rand/v2"
	"net/http"
	"os"
	"path/filepath"
	"strconv"
	"strings"

	"bingchunmoli.com/moliapi/internal/emoji"
	"github.com/gin-gonic/gin"
	"github.com/makiuchi-d/gozxing"
	qrreader "github.com/makiuchi-d/gozxing/qrcode"
	"github.com/mssola/user_agent"
	"github.com/skip2/go-qrcode"
)

func (s *Server) registerPublic(r gin.IRouter) {
	r.GET("/version", func(c *gin.Context) { ok(c, "go") })
	for _, name := range []string{"alise", "unicode", "html", "isEmoji", "contains", "tag",
		"getByAlise", "removeAllEmojis", "extractEmojis"} {
		r.GET("/emoji/"+name, s.emoji)
	}
	r.GET("/generate", s.generateQR)
	r.GET("/decode", s.decodeQR)
	r.POST("/decode", s.decodeQR)
	r.GET("/thunder/toThunder", s.thunder)
	r.GET("/thunder/toRaw", s.thunder)
	r.GET("/ua/userAgentInfo", s.userAgent)
	r.GET("/ua/userAgentInfoByParam", s.userAgent)
	r.GET("/ua/userAgent", s.userAgent)
	r.GET("/rent", s.rent)
	r.GET("/address", s.address)
	r.GET("/tencent/qq", s.avatar)
	r.GET("/tencent/qz", s.avatar)
	r.GET("/tencent/qz/json", s.avatar)
	r.GET("/img", s.image)
	r.GET("/img/pc", s.image)
	r.GET("/img/mobile", s.image)
	r.GET("/img/1080", s.image)
	r.GET("/yiyan/:id", s.quoteByID)
	r.GET("/shici/random", s.poem)
	r.GET("/shici/:id", s.poem)
	r.GET("/bing/cn", s.bingRegion)
	r.GET("/bing/en", s.bingRegion)
	r.GET("/bing/all", s.bingAll)
	r.GET("/bing/random", s.bingRandom)
	r.GET("/bing/date", s.bingDate)
	r.GET("/bing/:year/:month/:day", s.bingDate)
	r.GET("/host/json", s.hosts)
	r.GET("/host/raw", s.hosts)
	r.GET("/host/file", s.hosts)
	r.GET("/music/playlist", s.musicPlaylist)
	r.GET("/music/random", s.musicRandom)
	r.GET("/music/:id", s.musicSongs)
}

func (s *Server) emoji(c *gin.Context) {
	text := c.Query("emoji")
	switch filepath.Base(c.FullPath()) {
	case "alise":
		ok(c, emoji.Alias(text))
	case "unicode":
		ok(c, emoji.Unicode(text))
	case "html":
		ok(c, emoji.HTML(text))
	case "isEmoji":
		ok(c, emoji.IsEmoji(text))
	case "contains":
		ok(c, len(emoji.Extract(text)) > 0)
	case "tag":
		ok(c, emoji.ByTag(c.Query("tag")))
	case "getByAlise":
		ok(c, emoji.ByAlias(c.Query("alise")))
	case "removeAllEmojis":
		ok(c, emoji.Remove(c.Query("emojiStr")))
	case "extractEmojis":
		ok(c, emoji.Extract(c.Query("emojiStr")))
	}
}

func (s *Server) generateQR(c *gin.Context) {
	width, err := strconv.Atoi(c.DefaultQuery("width", "300"))
	height, heightErr := strconv.Atoi(c.DefaultQuery("height", "300"))
	if err != nil || heightErr != nil || width < 32 || height < 32 || width > 2048 || height > 2048 {
		fail(c, 400, "图片尺寸必须为32至2048")
		return
	}
	code, err := qrcode.New(c.DefaultQuery("text", "https://api.bingchunmoli.com"), qrcode.Medium)
	if err != nil {
		fail(c, 400, "二维码内容过长或为空")
		return
	}
	size := min(width, height)
	if size < len(code.Bitmap()) {
		fail(c, 400, "二维码尺寸不足以容纳内容")
		return
	}
	src := code.Image(size)
	dst := image.NewRGBA(image.Rect(0, 0, width, height))
	draw.Draw(dst, dst.Bounds(), &image.Uniform{C: color.White}, image.Point{}, draw.Src)
	rect := image.Rect((width-size)/2, (height-size)/2, (width+size)/2, (height+size)/2)
	draw.Draw(dst, rect, src, image.Point{}, draw.Src)
	var data bytes.Buffer
	if err = jpeg.Encode(&data, dst, &jpeg.Options{Quality: 100}); err != nil {
		internal(c, err)
		return
	}
	c.Data(200, "image/jpeg", data.Bytes())
}

func (s *Server) decodeQR(c *gin.Context) {
	c.Request.Body = http.MaxBytesReader(c.Writer, c.Request.Body, 8<<20)
	file, err := c.FormFile("file")
	if c.Request.MultipartForm != nil {
		defer c.Request.MultipartForm.RemoveAll()
	}
	if err != nil || file.Size == 0 {
		fail(c, 400, "请上传二维码图片")
		return
	}
	input, err := file.Open()
	if err != nil {
		fail(c, 400, "无法读取图片")
		return
	}
	defer input.Close()
	info, _, err := image.DecodeConfig(input)
	if err != nil || info.Width < 1 || info.Height < 1 || int64(info.Width)*int64(info.Height) > 16_000_000 {
		fail(c, 400, "图片无效或过大")
		return
	}
	if _, err = input.Seek(0, 0); err != nil {
		internal(c, err)
		return
	}
	img, _, err := image.Decode(input)
	if err != nil {
		fail(c, 400, "图片格式不支持")
		return
	}
	bitmap, err := gozxing.NewBinaryBitmapFromImage(img)
	if err != nil {
		fail(c, 400, "无法解析图片")
		return
	}
	result, err := qrreader.NewQRCodeReader().Decode(bitmap, nil)
	if err != nil {
		fail(c, 400, "图片中未找到有效二维码")
		return
	}
	ok(c, result.GetText())
}

func (s *Server) thunder(c *gin.Context) {
	if strings.HasSuffix(c.FullPath(), "toThunder") {
		raw := c.Query("rawURL")
		if raw == "" {
			fail(c, 400, "rawURL不能为空")
			return
		}
		ok(c, "thunder://"+base64.StdEncoding.EncodeToString([]byte("AA"+raw+"ZZ")))
		return
	}
	value := c.Query("thunderURL")
	if !strings.HasPrefix(value, "thunder://") {
		fail(c, 400, "无效的迅雷链接")
		return
	}
	data, err := base64.StdEncoding.DecodeString(strings.TrimPrefix(value, "thunder://"))
	if err != nil || !bytes.HasPrefix(data, []byte("AA")) || !bytes.HasSuffix(data, []byte("ZZ")) || len(data) < 4 {
		fail(c, 400, "无效的迅雷链接")
		return
	}
	ok(c, string(data[2:len(data)-2]))
}

func (s *Server) userAgent(c *gin.Context) {
	text := c.Query("userAgent")
	if strings.HasSuffix(c.FullPath(), "userAgentInfoByParam") && text == "" {
		fail(c, 400, "userAgent不能为空")
		return
	}
	if text == "" {
		text = c.Request.UserAgent()
	}
	ua := user_agent.New(text)
	browser, version := ua.Browser()
	engine, engineVersion := ua.Engine()
	ok(c, gin.H{"mobile": ua.Mobile(), "browser": gin.H{"name": browser}, "version": version,
		"engine":        gin.H{"name": engine},
		"engineVersion": engineVersion, "os": gin.H{"name": ua.OS()}, "platform": gin.H{"name": ua.Platform()}})
}

func decimal(text string) (*big.Rat, int, bool) {
	if len(text) > 64 {
		return nil, 0, false
	}
	if text == "" {
		text = "0"
	}
	scale := 0
	if dot := strings.IndexByte(text, '.'); dot >= 0 {
		scale = len(text) - dot - 1
	}
	if strings.ContainsAny(text, "/eE") || scale > 18 {
		return nil, 0, false
	}
	value, valid := new(big.Rat).SetString(text)
	return value, scale, valid
}

func (s *Server) rent(c *gin.Context) {
	names := []string{"month", "rentPrice", "waterConsumption", "waterCostRate", "electricityConsumption", "powerRate"}
	values := make([]*big.Rat, len(names))
	scales := make([]int, len(names))
	for i, name := range names {
		value, scale, valid := decimal(c.Query(name))
		if !valid {
			fail(c, 400, "无效的金额参数: "+name)
			return
		}
		values[i] = value
		scales[i] = scale
	}
	result := gin.H{}
	total := new(big.Rat)
	totalScale := 0
	for i, name := range []string{"rentPriceCalc", "waterPriceCalc", "powerPriceCalc"} {
		index := 2 * i
		product := new(big.Rat).Mul(values[index], values[index+1])
		scale := scales[index] + scales[index+1]
		result[name] = product.FloatString(scale)
		total.Add(total, product)
		if scale > totalScale {
			totalScale = scale
		}
	}
	result["totalPrice"] = total.FloatString(totalScale)
	ok(c, result)
}

func (s *Server) image(c *gin.Context) {
	cfg, err := s.config()
	if err != nil {
		internal(c, err)
		return
	}
	key := "pcPath"
	path := strings.TrimRight(c.Request.URL.Path, "/")
	if strings.HasSuffix(path, "/mobile") || (strings.HasSuffix(path, "/img") &&
		user_agent.New(c.Request.UserAgent()).Mobile()) {
		key = "mobilePath"
	}
	if strings.HasSuffix(path, "/1080") {
		key = "path1080"
	}
	entries, err := os.ReadDir(cfg[key])
	if err != nil {
		fail(c, 404, "图库目录不可用")
		return
	}
	files := []string{}
	for _, entry := range entries {
		ext := strings.ToLower(filepath.Ext(entry.Name()))
		if entry.Type().IsRegular() && (ext == ".png" || ext == ".jpg" || ext == ".jpeg" || ext == ".webp" || ext == ".gif") {
			files = append(files, entry.Name())
		}
	}
	if len(files) == 0 {
		fail(c, 404, "图库中没有图片")
		return
	}
	c.Header("Cache-Control", "no-store")
	c.File(filepath.Join(cfg[key], files[rand.IntN(len(files))]))
}

func (s *Server) avatar(c *gin.Context) {
	qq := c.Query("qq")
	if _, err := strconv.ParseUint(qq, 10, 64); err != nil || qq == "0" {
		fail(c, 400, "qq必须是正整数")
		return
	}
	qzone := !strings.HasSuffix(c.FullPath(), "/qq")
	fallback := "140"
	if qzone {
		fallback = "100"
	}
	size := c.DefaultQuery("size", fallback)
	valid := false
	for _, allowed := range []string{"40", "100", "140", "160", "640"} {
		if size == allowed {
			valid = true
		}
	}
	if qzone {
		valid = false
		for _, allowed := range []string{"30", "50", "100", "200", "640"} {
			if size == allowed {
				valid = true
			}
		}
	}
	if !valid {
		fail(c, 400, "不支持的头像尺寸")
		return
	}
	target := fmt.Sprintf("https://q1.qlogo.cn/g?b=qq&nk=%s&s=%s", qq, size)
	if qzone {
		target = fmt.Sprintf("https://qlogo.store.qq.com/qzone/%s/%s/%s", qq, qq, size)
	}
	if strings.HasSuffix(c.FullPath(), "/json") {
		ok(c, target)
		return
	}
	data, contentType, err := s.fetch(c.Request.Context(), target, nil)
	if err != nil {
		fail(c, 502, "获取头像失败")
		return
	}
	if !strings.HasPrefix(contentType, "image/") {
		fail(c, 502, "上游未返回图片")
		return
	}
	c.Data(200, contentType, data)
}

func (s *Server) address(c *gin.Context) {
	cfg, err := s.config()
	if err != nil {
		internal(c, err)
		return
	}
	value, err := s.lookupAddress(c.Request.Context(), c.ClientIP(), cfg)
	if err != nil {
		fail(c, 502, "IP地址查询失败")
		return
	}
	ok(c, value)
}
