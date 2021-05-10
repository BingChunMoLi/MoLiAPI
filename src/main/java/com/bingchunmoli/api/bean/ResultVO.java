package com.bingchunmoli.api.bean;


import com.bingchunmoli.api.bean.enums.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @copyright(c) 2017-2020 冰纯茉莉
 * @Description: TODO 响应模板
 * @Author 冰彦糖
 * @Data 2020/11/16 15:35
 * @ClassName ResonseBase
 * @PackageName: com.bingchunmoli.api.response
 * @Version 0.0.1-SNAPSHOT
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultVO {
    private String code;
    private String msg;
    private Object data;
    public ResultVO(CodeEnum code, Object data){
        this.code = code.getCode();
        this.msg = code.getMsg();
        this.data = data;
    }
    public ResultVO(Object data){
        this(CodeEnum.SUCCESS,data);
    }


}