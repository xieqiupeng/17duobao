package com.zhiri.bear.models;

/**
 * Created by MagicBean on 2016/03/10 00:0:58
 */
public class DeviceInfo {
    public String deviceId;
    public String deviceOsVersion;
    public String deviceType = "android";
    public int deviceAppVersion;
    public String deviceModel;

    public DeviceInfo(String deviceId, String deviceOsVersion, int deviceAppVersion, String deviceModel) {
        this.deviceId = deviceId;
        this.deviceOsVersion = deviceOsVersion;
        this.deviceAppVersion = deviceAppVersion;
        this.deviceModel = deviceModel;
    }

}
