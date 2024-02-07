package com.bingchunmoli.api.down.util;

import com.bingchunmoli.api.down.bean.Fav;
import com.bingchunmoli.api.down.bean.FavPO;
import com.bingchunmoli.api.down.bean.Media;
import com.bingchunmoli.api.down.bean.MediaPO;
import com.bingchunmoli.api.down.bean.UserPO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.bingchunmoli.api.down.bean.constant.Constants.MAX_PAGE_SIZE;
import static com.bingchunmoli.api.down.bean.constant.Constants.START_PAGE_SIZE;

/**
 * @author MoLi
 */
@Slf4j
public class BiliUtil {

    private static final CloseableHttpClient CLIENT = HttpClientBuilder.create().build();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Fav getBiliFavBy(String mediaId) throws IOException {
        //获取收藏
        HttpGet get = new HttpGet("https://api.bilibili.com/x/v3/fav/resource/list?media_id=" + mediaId + "&pn=1&ps=20&keyword=&order=mtime&type=0&tid=0&platform=web&jsonp=jsonp");
        String s = EntityUtils.toString(CLIENT.execute(get).getEntity());
        if (log.isDebugEnabled()) {
            log.debug("接口请求结果: " + s);
        }
        Fav fav = objectMapper.readValue(s, Fav.class);
        Integer mediaCount = fav.getData().getInfo().getMediaCount();
        if (mediaCount > MAX_PAGE_SIZE) {
            //收藏夹大于一页需要分页查询，接口不支持查询全部，限制一页20
            for (int i = START_PAGE_SIZE; i <= (mediaCount / MAX_PAGE_SIZE) + 1; i++) {
                //收藏夹带视频清晰度信息
                //https://api.bilibili.com/medialist/gateway/base/detail?media_id=56939296&pn=1&ps=20
                //https://api.bilibili.com/medialist/gateway/base/spaceDetail?media_id=56939296&pn=1&ps=20&keyword=&order=mtime&type=0&tid=0&jsonp=jsonp
                get = new HttpGet("https://api.bilibili.com/x/v3/fav/resource/list?media_id=56939296&pn=" + i + "&ps=20&keyword=&order=mtime&type=0&tid=0&platform=web&jsonp=jsonp");
                s = EntityUtils.toString(CLIENT.execute(get).getEntity());
                if (log.isDebugEnabled()) {
                    log.debug("接口请求结果( " + i + "次数): " + s);
                }
                fav.getData().getMedias().addAll(objectMapper.readValue(s, Fav.class).getData().getMedias());
            }
        }
        return fav;
    }

    public static FavPO favToFavParse(Fav fav) {
        return CovertUtil.favParseFav(fav);
    }

    public static Collection<UserPO> favToUserListParse(Fav fav) {
        List<UserPO> list = new ArrayList<>(fav.getData().getMedias().size() + 1);
        UserPO favUser = CovertUtil.favParseUser(fav);
        list.add(favUser);
        List<UserPO> mediaUserList = fav.getData().getMedias().stream().map(CovertUtil::mediaParseUser).toList();
        list.addAll(mediaUserList);
        return list;
    }

    public static Collection<MediaPO> favToMediaListParse(Fav fav) {
        return CovertUtil.favParseMedia(fav.getData().getMedias());
    }

    public static Media getMediaDetail(String av) throws IOException {
        log.debug("单个mediaDetail av: {}", av);
        HttpGet get = new HttpGet("https://api.bilibili.com/x/web-interface/view?aid=" + av);
        String result = EntityUtils.toString(CLIENT.execute(get).getEntity());
        log.debug("单个mediaDetail接口返回： {}", result);
        return objectMapper.readValue(result, Media.class);
    }

}
