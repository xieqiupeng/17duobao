package com.zhiri.bear.models;

import java.util.ArrayList;

/**
 * Created by MagicBean on 2016/04/06 23:23:03
 */
public class AtestAnnounce {

    public static class AtestAnnounceEntity {
        public ArrayList<AtestAnnounce> list = new ArrayList<>();
        public Page page;
    }

    public AppUserInfoDtoEntity appUserInfoDto;
    public int goodsId;
    public String goodsImageFileKey;
    public int id;
    public String issueNumber;
    public String luckyNo;
    public String name;
    public OpenTimeEntity openTime;
    public String openTimeStr;
    public int status;
    public SystemTimeEntity systemTime;

    /**
     * 倒计时总时间
     */
    public long countdown;
    public int countId;

    public void setCountId(int countId) {
        this.countId = countId;
    }

    public int getCountId() {
        return countId;
    }

    public void setCountdown(long countdown) {
        this.countdown = countdown;
    }

    public long getCountdown() {
        return countdown;
    }

    public static class AppUserInfoDtoEntity {
        public int buyNumber;
        public int id;
        public String image;
        public String name;
        public String no;

        @Override
        public String toString() {
            return "AppUserInfoDtoEntity{" +
                    "buyNumber=" + buyNumber +
                    ", id=" + id +
                    ", image='" + image + '\'' +
                    ", name='" + name + '\'' +
                    ", no='" + no + '\'' +
                    '}';
        }
    }

    public static class OpenTimeEntity {
        public long time;
    }

    public static class SystemTimeEntity {
        public long time;
    }
}
