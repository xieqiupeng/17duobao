package com.zhiri.bear.net;


import com.zhiri.bear.App;
import com.zhiri.bear.utils.TextUtil;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by MagicBean on 2015/10/16 10:10:32
 */
public class HttpResponse<T> {
    public int code;
    public int flag;
    public String[] msg;
    public ArrayList<T> data = new ArrayList<>();
    public boolean state;
    public static final int SUCCESS = 0;
    public static final int NO_LOGIN = 401;//未登录
    public static final int DISABLE_LOGIN = 403;//禁用

    public int getCode() {
        return code;
    }

    public ArrayList<T> getData() {
        return data;
    }

    public T getDataFrist() {
        if (TextUtil.isValidate(data))
            return data.get(0);
        return null;
    }

    public boolean isSuccess() {
        if (code != SUCCESS) {
            if (code == NO_LOGIN) {
                com.zhiri.bear.utils.T.showShort(App.getInst(), "登录信息过期,请重新登录！");
            } else if (code == DISABLE_LOGIN) {
                com.zhiri.bear.utils.T.showShort(App.getInst(), "该用户已被禁用");
            } else {
                if (code != 90001 || code != 90002)
                    com.zhiri.bear.utils.T.showShort(App.getInst(), getMessage());
            }
        }
        return code == 0 ? true : false;
    }

    public String getMessage() {
        return TextUtil.isValidate(msg) ? msg[0] : "";
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "data=" + data +
                ", msg=" + Arrays.toString(msg) +
                ", flag=" + flag +
                ", code=" + code +
                '}';
    }
}
