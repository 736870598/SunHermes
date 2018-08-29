package com.sunxy.hermes.core.response;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/28 0028.
 */
public class ResponseBean {

    private Object data;

    public ResponseBean(Object data){
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
