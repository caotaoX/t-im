package com.ct.myim.im.entity;

import cn.hutool.core.lang.Dict;

import java.util.List;

public class SocketReturnMsg {

    /**
     * 请求类型
     */
    private int httpType;

    /**
     *请求返回数据
     *
     */
    private List<Dict> data;

    public int getHttpType() {
        return httpType;
    }

    public void setHttpType(int httpType) {
        this.httpType = httpType;
    }

    public List<Dict> getData() {
        return data;
    }

    public void setData(List<Dict> data) {
        this.data = data;
    }
}
