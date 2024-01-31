package com.bingchunmoli.api.bing;

import com.bingchunmoli.api.bing.bean.BingImage;
import com.bingchunmoli.api.bing.bean.BingImageVO;
import com.bingchunmoli.api.bing.bean.enums.BingEnum;
import com.bingchunmoli.api.bing.mapper.BingImageMapper;
import com.bingchunmoli.api.bing.service.BingService;
import com.bingchunmoli.api.bing.service.impl.BingServiceImpl;
import com.bingchunmoli.api.utils.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@JsonTest
@ContextConfiguration(classes = {BingService.class, BingServiceImpl.class})
public class BingServiceTest {

    @Autowired
    private ObjectMapper om;
    @Autowired
    private BingService bingService;
    @MockBean
    private RedisUtil redisUtil;
    @MockBean
    private BingImageMapper bingImageMapper;
    private BingImageVO cnBingImage;
    private BingImageVO enBingImage;

    @Test
    void getBingCnTest() {
        cnBingImage = bingService.getBingImageByRemote(BingEnum.CN_BING);
        Assert.notNull(cnBingImage, "cn.bing.com not get random image");
    }

    @Test
    void getBingEnTest() {
        enBingImage = bingService.getBingImageByRemote(BingEnum.EN_BING);
        Assert.notNull(enBingImage, "bing.com not get random image");
    }

    @Test
    void getBingAllTest() {
        cnBingImage = bingService.getBingImageByRemote(BingEnum.CN_BING);
        enBingImage = bingService.getBingImageByRemote(BingEnum.EN_BING);
        BingImage bingImage = new BingImage(cnBingImage, enBingImage);
        Assert.isTrue(LocalDate.parse(bingImage.getEndDate(), DateTimeFormatter.ofPattern("yyyyMMdd")).isEqual(LocalDate.now()), "获取的bingImage.EndDate已过时");
        Assert.isTrue(bingImage.getCreateTime().toLocalDate().isEqual(LocalDate.now()), "获取的bingImage.creatTime已过时");
    }

    @Test
    void jsonParse() throws JsonProcessingException {
        String s = "{\"images\":[{\"startdate\":\"20211007\",\"fullstartdate\":\"202110071600\",\"enddate\":\"20211008\",\"url\":\"/th?id=OHR.FriendlyOctopus_ZH-CN2519447724_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp\",\"urlbase\":\"/th?id=OHR.FriendlyOctopus_ZH-CN2519447724\",\"copyright\":\"海草中的章鱼，法国利翁湾 (© BIOSPHOTO/Alamy)\",\"copyrightlink\":\"https://www.bing.com/search?q=%E7%AB%A0%E9%B1%BC&form=hpcapt&mkt=zh-cn\",\"title\":\"\",\"quiz\":\"/search?q=Bing+homepage+quiz&filters=WQOskey:%22HPQuiz_20211007_FriendlyOctopus%22&FORM=HPQUIZ\",\"wp\":true,\"hsh\":\"2e4cdcc79efcd8dd814ce96c12f19125\",\"drk\":1,\"top\":1,\"bot\":1,\"hs\":[]}],\"tooltips\":{\"loading\":\"正在加载...\",\"previous\":\"上一个图像\",\"next\":\"下一个图像\",\"walle\":\"此图片不能下载用作壁纸。\",\"walls\":\"下载今日美图。仅限用作桌面壁纸。\"}}\n";
        BingImageVO vo = om.readValue(s, BingImageVO.class);
        Assert.notNull(vo, "bingImageJson解析失败");
    }
}
