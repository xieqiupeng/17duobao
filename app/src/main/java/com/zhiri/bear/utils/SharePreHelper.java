package com.zhiri.bear.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by MagicBean on 2016/01/13 15:15:28
 */
public class SharePreHelper {
    private SharedPreferences sp;
    private SharedPreferences.Editor edit;
    private long adInterval;

    private SharePreHelper() {
    }

    private static SharePreHelper helper;

    public static SharePreHelper getIns() {
        if (helper == null) {
            helper = new SharePreHelper();
        }
        return helper;
    }

    public void initialize(Context context, String name) {
        if (TextUtil.isValidate(name)) {
            sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        } else {
            sp = PreferenceManager.getDefaultSharedPreferences(context);
        }
        edit = sp.edit();
    }

    public void saveHotWords(String hotWords) {
        edit.putString("_hot_words", hotWords).commit();
    }

    public String getHotWords() {
        return sp.getString("_hot_words", "");
    }

    public boolean isFromPay() {
        return sp.getBoolean("_isPay", false);
    }

    public void setFromPay(boolean pay) {
        edit.putBoolean("_isPay", pay).commit();
    }

    public void setServerUrl(String url) {
        edit.putString("_ip", url).commit();
    }

    public String getServerUrl() {
        return sp.getString("_ip", "");
    }

    public void setAdTime(long time) {
        edit.putLong("_ad_time", time).commit();
    }

    public long getAdTime() {
        return sp.getLong("_ad_time", System.currentTimeMillis());
    }

    public void setCurrentUpdateVersion(int appVersion) {
        edit.putInt("current_update_version", appVersion).commit();
    }

    public int getCurrentUpdateViewsion() {
        return sp.getInt("current_update_version", 1);
    }

    public void setAdInterval(long adInterval) {
        edit.putLong("adInterval", adInterval).commit();
    }

    public long getAdInterval() {
        return sp.getLong("adInterval", 60 * 60 * 1000);
    }

    public void setShouldShowNotification(boolean show) {
        edit.putBoolean("_can_show", show).commit();
    }

    public boolean shouldShowNotification() {
        return sp.getBoolean("_can_show", false);
    }

    public void savePayResultInfo(String string) {
        edit.putString("_pay_result_info", string).commit();
    }

    public String getPayResultInfo() {
        return sp.getString("_pay_result_info", "");
    }
}
