package com.zhiri.bear.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by MagicBean on 2016/03/29 20:20:38
 */
public class SystemNotificationEntity implements Parcelable {
    public int id;
    public String content;
    public String dateTime;
    public String title;



    public static class SystemNotification {
        public ArrayList<SystemNotificationEntity> list = new ArrayList<>();
        public Page page;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.content);
        dest.writeString(this.dateTime);
        dest.writeString(this.title);
    }

    public SystemNotificationEntity() {
    }

    protected SystemNotificationEntity(Parcel in) {
        this.id = in.readInt();
        this.content = in.readString();
        this.dateTime = in.readString();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<SystemNotificationEntity> CREATOR = new Parcelable.Creator<SystemNotificationEntity>() {
        @Override
        public SystemNotificationEntity createFromParcel(Parcel source) {
            return new SystemNotificationEntity(source);
        }

        @Override
        public SystemNotificationEntity[] newArray(int size) {
            return new SystemNotificationEntity[size];
        }
    };
}
