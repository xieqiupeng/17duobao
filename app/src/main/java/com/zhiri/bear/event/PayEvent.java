package com.zhiri.bear.event;

/**
 * Created by MagicBean on 2016/03/12 21:21:43
 */
public class PayEvent {

    public int code;
    public String msg;

    public PayEvent(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
