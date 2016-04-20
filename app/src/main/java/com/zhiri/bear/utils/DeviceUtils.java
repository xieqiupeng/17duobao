package com.zhiri.bear.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import com.zhiri.bear.App;
import com.zhiri.bear.models.DeviceInfo;

/**
 * Created by MagicBean on 2016/03/10 00:0:03
 */
public class DeviceUtils {


    public static DeviceInfo getDeviceInfo() {
        String modelName = android.os.Build.MODEL;
        String modelVerson = android.os.Build.VERSION.RELEASE;
        TelephonyManager tm = (TelephonyManager) App.getInst().getSystemService(Context.TELEPHONY_SERVICE);
        int appVersion = 0;
        try {
            appVersion = App.getInst().getPackageManager().getPackageInfo(App.getInst().getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        DeviceInfo mDeviceInfo = new DeviceInfo(tm.getDeviceId(), modelVerson, appVersion, modelName);
        return mDeviceInfo;
    }
}
