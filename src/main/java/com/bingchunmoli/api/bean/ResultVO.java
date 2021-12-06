package com.bingchunmoli.api.bean;


import com.bingchunmoli.api.bean.enums.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bingchunmoli
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultVO <T>{

    /**
     * 业务状态码
     */
    private String code;

    /**
     * 业务友好消息
     */
    private String msg;

    /**
     * 业务承载数据
     */
    private T data;
    public ResultVO(CodeEnum code, T data){
        this.code = code.getCode();
        this.msg = code.getMsg();
        this.data = data;
    }
    public ResultVO(T data){
        this(CodeEnum.SUCCESS,data);
    }

    public static <T> ResultVO<T> ok(T data){
        return new ResultVO<>(data);
    }


}