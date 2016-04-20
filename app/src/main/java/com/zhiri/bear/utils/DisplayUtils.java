package com.zhiri.bear.utils;

import android.content.Context;
import android.util.TypedValue;

import java.lang.reflect.Field;

public class DisplayUtils {
    public static int dip2px(Context context, float dpValue) {
//		final float scale = context.getResources().getDisplayMetrics().density;
//		return (int) (dpValue * scale + 0.5f);
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getApplicationContext().getResources().getDisplayMetrics());
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        return context.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getApplicationContext().getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 38;//默认为38，貌似大部分是这样的

        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }
}
