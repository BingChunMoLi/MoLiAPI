package com.bingchunmoli.api.down.util;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.bingchunmoli.api.down.bean.Fav;
import com.bingchunmoli.api.down.bean.FavPO;
import com.bingchunmoli.api.down.bean.MediaPO;
import com.bingchunmoli.api.down.bean.UserPO;

import java.util.Collection;
import java.util.List;

/**
 * @author BingChunMoLi
 */
public class CovertUtil {

    /**
     * 将收藏转换为fav数据库对象
     *
     * @param fav 收藏对象
     * @return 数据库收藏对象
     */
    public static FavPO favParseFav(Fav fav) {
        return Convert.convert(FavPO.class, fav.getData().getInfo());
    }

    /**
     * 将收藏fav转换为user对象
     *
     * @param fav 收藏fav
     * @return 该收藏的user
     */
    public static UserPO favParseUser(Fav fav) {
        UserPO user = new UserPO();
        Fav.DataDTO.InfoDTO.UpperDTO upper = fav.getData().getInfo().getUpper();
        user.setId(upper.getMid());
        user.setName(upper.getName());
        user.setFace(upper.getFace());
        return user;
    }

    /**
     * 将原生media列表 covert为数据库media列表
     *
     * @param originalMedias 原始media列表
     * @return 数据库media列表
     */
    public static List<MediaPO> favParseMedia(Collection<Fav.DataDTO.MediasDTO> originalMedias) {
        return Convert.convert(new TypeReference<List<MediaPO>>() {
        }, originalMedias);
    }

    /**
     * 媒体的用户
     *
     * @param media
     * @return 媒体中存储的用户
     */
    public static UserPO mediaParseUser(Fav.DataDTO.MediasDTO media) {
        UserPO userPO = new UserPO();
        Fav.DataDTO.MediasDTO.UpperDTO upper = media.getUpper();
        userPO.setId(upper.getMid());
        userPO.setName(upper.getName());
        userPO.setFace(upper.getFace());
        return userPO;
    }

}

