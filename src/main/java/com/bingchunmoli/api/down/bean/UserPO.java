package com.bingchunmoli.api.down.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author BingChunMoLi
 */
@Data
@Builder
@TableName("bili_user")
@NoArgsConstructor
@AllArgsConstructor
public class UserPO {

    private Integer id;
    /**
     * 用户昵称
     */
    private String name;
    /**
     * 用户头像
     */
    private String face;
}
