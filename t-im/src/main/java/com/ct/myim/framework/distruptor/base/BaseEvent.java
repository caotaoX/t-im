package com.ct.myim.framework.distruptor.base;

import java.io.Serializable;

/**
 * 生产、消费者之间传递消息用的event
 *
 * @author CAOTAO
 */
public class BaseEvent implements Serializable {

    /**
     * 业务对象
     */
    private String msg;

    public BaseEvent(String msg) {
        this.msg = msg;
    }

    public BaseEvent() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
