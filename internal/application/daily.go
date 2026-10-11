package application

import (
	"encoding/json"
	"log"
	"net/url"
	"sort"
	"strings"
	"time"

	"bingchunmoli.com/moliapi/internal/store"
	"github.com/gin-gonic/gin"
)

func decodeTags(st *store.State, value any) ([]store.Tag, error) {
	data, err := json.Marshal(value)
	if err != nil {
		return nil, err
	}
	var entries []json.RawMessage
	if err = json.Unmarshal(data, &entries); err != nil {
		return nil, errConflict
	}
	tags := []store.Tag{}
	for _, raw := range entries {
		tag := store.Tag{}
		if err = json.Unmarshal(raw, &tag); err != nil {
			return nil, errConflict
		}
		if tag.ID != 0 {
			found := false
			for _, n := range st.Navigations {
				for _, existing := range n.Tags {
					if existing.ID == tag.ID {
						tag = existing
						found = true
						break
					}
				}
			}
			if !found {
				return nil, errMissing
			}
			if err = json.Unmarshal(raw, &tag); err != nil {
				return nil, errConflict
			}
		}
		tags = append(tags, tag)
	}
	return tags, nil
}

type dailyInput struct {
	Key   string `json:"key"`
	Value string `json:"value"`
}

func (s *Server) addDaily(inputs []dailyInput) error {
	return s.Store.Update(func(st *store.State) error {
		for _, input := range inputs {
			st.Daily[input.Key] = append(st.Daily[input.Key], input.Value)
		}
		return nil
	})
}

func (s *Server) dailyAdd(c *gin.Context) {
	var input dailyInput
	if !bind(c, &input) {
		return
	}
	if strings.TrimSpace(input.Key) == "" || strings.TrimSpace(input.Value) == "" {
		fail(c, 400, "key和value不能为空")
		return
	}
	if err := s.addDaily([]dailyInput{input}); err != nil {
		internal(c, err)
		return
	}
	c.Request.URL.RawQuery = "key=" + url.QueryEscape(input.Key)
	s.dailyList(c)
}

func (s *Server) dailyAddAll(c *gin.Context) {
	var inputs []dailyInput
	if !bind(c, &inputs) {
		return
	}
	if len(inputs) == 0 || len(inputs) > 1000 {
		fail(c, 400, "请提供1至1000条记录")
		return
	}
	for _, input := range inputs {
		if strings.TrimSpace(input.Key) == "" || strings.TrimSpace(input.Value) == "" {
			fail(c, 400, "key和value不能为空")
			return
		}
	}
	if err := s.addDaily(inputs); err != nil {
		internal(c, err)
		return
	}
	ok(c, inputs)
}

func (s *Server) dailyList(c *gin.Context) {
	var list []string
	key := c.DefaultQuery("key", "moli")
	err := s.Store.View(func(st *store.State) error {
		list = st.Daily[key]
		if list == nil {
			list = []string{}
		}
		return nil
	})
	if err != nil {
		internal(c, err)
		return
	}
	ok(c, list)
}

func dailyTenant(c *gin.Context) int {
	if strings.EqualFold(tenant(c, "moli"), "moli") {
		return 1
	}
	return 0
}

func (s *Server) dailySigned(c *gin.Context) {
	var urls []string
	if !bind(c, &urls) {
		return
	}
	if len(urls) == 0 || len(urls) > 1000 {
		fail(c, 400, "请提供1至1000条签到记录")
		return
	}
	for _, u := range urls {
		if strings.TrimSpace(u) == "" {
			fail(c, 400, "签到网址不能为空")
			return
		}
	}
	var devices []store.Device
	err := s.Store.Update(func(st *store.State) error {
		for _, u := range urls {
			t := 0
			for _, value := range st.Daily["moli"] {
				if value == u {
					t = 1
					break
				}
			}
			st.Logs = append(st.Logs, store.DailyLog{ID: st.ID(), URL: u, Type: 1, Tenant: t, Created: s.now()})
		}
		devices = st.Devices
		return nil
	})
	if err != nil {
		internal(c, err)
		return
	}
	if s.Notifier != nil {
		target := store.Device{PushType: "fcm"}
		for _, d := range devices {
			if d.Token != "" || d.HMSToken != "" {
				target = d
				break
			}
		}
		err = s.Notifier.Send(c.Request.Context(), target, "签到成功", s.now().Format("2006-01-02 15:04:05"))
		if recordErr := s.logPush(target.PushType, target.Name, "签到成功", err); recordErr != nil {
			log.Printf("daily notification: %v", recordErr)
		}
	}
	ok(c, true)
}

func (s *Server) dailyCheck(c *gin.Context) {
	list := []string{}
	day := s.now().Format(time.DateOnly)
	err := s.Store.View(func(st *store.State) error {
		for _, entry := range st.Logs {
			if entry.Tenant == dailyTenant(c) && entry.Created.In(s.now().Location()).Format(time.DateOnly) == day {
				list = append(list, entry.URL)
			}
		}
		return nil
	})
	if err != nil {
		internal(c, err)
		return
	}
	ok(c, list)
}

func (s *Server) dailyParam(c *gin.Context) {
	var start, end *time.Time
	urls := map[string]bool{}
	err := s.Store.View(func(st *store.State) error {
		for _, entry := range st.Logs {
			if entry.Tenant != dailyTenant(c) {
				continue
			}
			urls[entry.URL] = true
			if u := c.Query("url"); u != "" && u != entry.URL {
				continue
			}
			t := entry.Created
			if start == nil || t.Before(*start) {
				start = &t
			}
			if end == nil || t.After(*end) {
				end = &t
			}
		}
		return nil
	})
	if err != nil {
		internal(c, err)
		return
	}
	list := []string{}
	for u := range urls {
		list = append(list, u)
	}
	sort.Strings(list)
	var first, last any
	if start != nil {
		first = start.In(s.now().Location()).Format(time.DateOnly)
		last = end.In(s.now().Location()).Format(time.DateOnly)
	}
	ok(c, gin.H{"startDate": first, "endDate": last, "urls": list})
}

func (s *Server) dailyQuery(c *gin.Context) {
	start, err := time.ParseInLocation(time.DateOnly, c.Query("startDate"), s.now().Location())
	end, endErr := time.ParseInLocation(time.DateOnly, c.Query("endDate"), s.now().Location())
	if err != nil || endErr != nil || end.Before(start) {
		fail(c, 400, "请提供有效的起止日期 yyyy-MM-dd")
		return
	}
	end = end.AddDate(0, 0, 1)
	urls := map[string]bool{}
	for _, value := range c.QueryArray("urls") {
		if value != "" {
			urls[value] = true
		}
	}
	if value := c.Query("url"); value != "" {
		urls[value] = true
	}
	result := map[string][]gin.H{}
	err = s.Store.View(func(st *store.State) error {
		for _, entry := range st.Logs {
			if entry.Tenant != dailyTenant(c) || entry.Created.Before(start) || !entry.Created.Before(end) {
				continue
			}
			if len(urls) > 0 && !urls[entry.URL] {
				continue
			}
			day := entry.Created.In(s.now().Location()).Format(time.DateOnly)
			result[day] = append(result[day], gin.H{"id": entry.ID, "url": entry.URL, "type": entry.Type,
				"tenant": entry.Tenant, "createTime": day})
		}
		return nil
	})
	if err != nil {
		internal(c, err)
		return
	}
	ok(c, result)
}

func mergeDevice(existing, input store.Device) store.Device {
	if input.Token != "" {
		existing.Token = input.Token
	}
	if input.HMSToken != "" {
		existing.HMSToken = input.HMSToken
	}
	if input.PushType != "" {
		existing.PushType = input.PushType
	}
	if input.Name != "" {
		existing.Name = input.Name
	}
	if input.Model != "" {
		existing.Model = input.Model
	}
	if input.AndroidID != "" {
		existing.AndroidID = input.AndroidID
	}
	return existing
}

func (s *Server) saveDevice(input store.Device) (int, error) {
	id := 0
	err := s.Store.Update(func(st *store.State) error {
		index := -1
		for i, d := range st.Devices {
			sameIdentity := d.Name == input.Name && d.Model == input.Model && d.AndroidID == input.AndroidID
			if (input.ID > 0 && d.ID == input.ID) || (input.ID == 0 && sameIdentity) {
				index = i
				input = mergeDevice(d, input)
				break
			}
		}
		if input.PushType == "" {
			input.PushType = "fcm"
			if input.HMSToken != "" {
				input.PushType = "hms"
			}
		}
		if input.PushType != "fcm" && input.PushType != "hms" {
			return errConflict
		}
		if index < 0 {
			input.ID = st.ID()
			st.Devices = append(st.Devices, input)
		} else {
			st.Devices[index] = input
		}
		id = input.ID
		return nil
	})
	return id, err
}

func (s *Server) deviceSave(c *gin.Context) {
	var d store.Device
	if !bind(c, &d) {
		return
	}
	if d.ID == 0 && (d.Name == "" || d.Model == "" || d.AndroidID == "") {
		fail(c, 400, "设备标识不能为空")
		return
	}
	if _, err := s.saveDevice(d); err != nil {
		if err == errConflict {
			fail(c, 400, "不支持的推送类型")
			return
		}
		internal(c, err)
		return
	}
	c.JSON(201, gin.H{"code": "00000", "msg": "一切OK", "data": true})
}

func (s *Server) deviceFind(c *gin.Context) {
	id, err := s.saveDevice(store.Device{Name: c.Param("name"), Model: c.Param("model"), AndroidID: c.Param("androidId")})
	if err != nil {
		internal(c, err)
		return
	}
	ok(c, id)
}
