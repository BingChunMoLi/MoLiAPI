package application

import (
	"context"
	"fmt"
	"math/rand/v2"
	"strconv"
	"strings"
	"time"

	"bingchunmoli.com/moliapi/internal/store"
	"github.com/gin-gonic/gin"
)

func (s *Server) fetchBing(ctx context.Context, region string) (map[string]any, error) {
	cfg, err := s.config()
	if err != nil {
		return nil, err
	}
	market := "zh-CN"
	if region == "en" {
		market = "en-US"
	}
	result, err := s.getJSON(ctx, endpoint(cfg["bingURL"])+"/HPImageArchive.aspx?format=js&idx=0&n=8&mkt="+market, nil)
	if err != nil {
		return nil, err
	}
	images, valid := result["images"].([]any)
	if !valid || len(images) == 0 {
		return nil, fmt.Errorf("Bing returned no images")
	}
	for _, item := range images {
		image, valid := item.(map[string]any)
		if !valid || image["url"] == nil || image["startdate"] == nil {
			return nil, fmt.Errorf("invalid Bing image")
		}
	}
	return result, nil
}

func (s *Server) bingRegion(c *gin.Context) {
	region := "cn"
	if strings.HasSuffix(c.FullPath(), "/en") {
		region = "en"
	}
	data, err := s.fetchBing(c.Request.Context(), region)
	if err != nil {
		fail(c, 502, "获取Bing图片失败")
		return
	}
	ok(c, data)
}

func publicBing(image map[string]any) map[string]any {
	result := map[string]any{}
	for key, value := range image {
		if !strings.HasPrefix(key, "_") {
			result[key] = value
		}
	}
	return result
}

func (s *Server) refreshBing(ctx context.Context) error {
	cn, err := s.fetchBing(ctx, "cn")
	if err != nil {
		return err
	}
	en, err := s.fetchBing(ctx, "en")
	if err != nil {
		return err
	}
	foreign := map[string]map[string]any{}
	for _, value := range en["images"].([]any) {
		item := value.(map[string]any)
		foreign[fmt.Sprint(item["startdate"])] = item
	}
	return s.Store.Update(func(st *store.State) error {
		for _, value := range cn["images"].([]any) {
			item := value.(map[string]any)
			day, err := time.Parse("20060102", fmt.Sprint(item["startdate"]))
			if err != nil {
				return err
			}
			image := map[string]any{"createTime": day.Format("2006-01-02T15:04:05")}
			if old := st.Bing[day.Format(time.DateOnly)]; old != nil {
				image["id"] = old["id"]
			} else {
				image["id"] = st.ID()
			}
			for jsonKey, field := range map[string]string{"startdate": "startDate",
				"fullstartdate": "fullStartDate", "enddate": "endDate", "url": "url", "urlbase": "urlBase",
				"copyright": "copyright", "copyrightlink": "copyrightLink"} {
				image[field] = item[jsonKey]
				if other := foreign[fmt.Sprint(item["startdate"])]; other != nil {
					image[field+"En"] = other[jsonKey]
				}
			}
			image["urlUhd"] = fmt.Sprint(item["urlbase"]) + "_UHD.jpg"
			if other := foreign[fmt.Sprint(item["startdate"])]; other != nil {
				image["urlUhdEn"] = fmt.Sprint(other["urlbase"]) + "_UHD.jpg"
				image["headlineEn"] = other["title"]
			}
			st.Bing[day.Format(time.DateOnly)] = image
		}
		return nil
	})
}

func (s *Server) bingImage(ctx context.Context, day string) (map[string]any, error) {
	var image map[string]any
	read := func(st *store.State) error { image = st.Bing[day]; return nil }
	if err := s.Store.View(read); err != nil {
		return nil, err
	}
	if image != nil {
		return publicBing(image), nil
	}
	if err := s.refreshBing(ctx); err != nil {
		return nil, err
	}
	if err := s.Store.View(read); err != nil {
		return nil, err
	}
	if image == nil {
		return nil, nil
	}
	return publicBing(image), nil
}

func (s *Server) bingAll(c *gin.Context) {
	image, err := s.bingImage(c.Request.Context(), s.now().Format(time.DateOnly))
	if err != nil {
		fail(c, 502, "获取Bing图片失败")
		return
	}
	ok(c, image)
}

func (s *Server) bingDate(c *gin.Context) {
	day := c.Query("date")
	if c.Param("year") != "" {
		year, e1 := strconv.Atoi(c.Param("year"))
		month, e2 := strconv.Atoi(c.Param("month"))
		date, e3 := strconv.Atoi(c.Param("day"))
		if e1 != nil || e2 != nil || e3 != nil {
			fail(c, 400, "无效的日期")
			return
		}
		day = fmt.Sprintf("%04d-%02d-%02d", year, month, date)
	}
	if _, err := time.Parse(time.DateOnly, day); err != nil {
		fail(c, 400, "日期格式应为 yyyy-MM-dd")
		return
	}
	image, err := s.bingImage(c.Request.Context(), day)
	if err != nil {
		fail(c, 502, "获取Bing图片失败")
		return
	}
	ok(c, image)
}

func (s *Server) bingRandom(c *gin.Context) {
	images := []map[string]any{}
	read := func(st *store.State) error {
		for _, image := range st.Bing {
			images = append(images, image)
		}
		return nil
	}
	if err := s.Store.View(read); err != nil {
		internal(c, err)
		return
	}
	if len(images) == 0 {
		if err := s.refreshBing(c.Request.Context()); err != nil {
			fail(c, 502, "获取Bing图片失败")
			return
		}
		if err := s.Store.View(read); err != nil {
			internal(c, err)
			return
		}
	}
	if len(images) == 0 {
		fail(c, 404, "暂无Bing图片")
		return
	}
	target := fmt.Sprint(images[rand.IntN(len(images))]["url"])
	if !validHTTP(target) {
		target = "https://www.bing.com" + target
	}
	ok(c, target)
}
