package com.bingchunmoli.api.utils.push;


import com.bingchunmoli.api.bean.Message;

public interface Push {

    String send(Message message);

    boolean support(Message message);

}
