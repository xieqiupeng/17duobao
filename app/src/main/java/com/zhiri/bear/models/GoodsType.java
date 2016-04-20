package com.zhiri.bear.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MagicBean on 2016/03/03 00:0:49
 */
public class GoodsType implements Parcelable {

    public String deleteStatus;
    public String icon;
    public int id;
    public String name;
    public int sort;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.deleteStatus);
        dest.writeString(this.icon);
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.sort);
    }

    public GoodsType() {
    }

    protected GoodsType(Parcel in) {
        this.deleteStatus = in.readString();
        this.icon = in.readString();
        this.id = in.readInt();
        this.name = in.readString();
        this.sort = in.readInt();
    }

    public static final Parcelable.Creator<GoodsType> CREATOR = new Parcelable.Creator<GoodsType>() {
        @Override
        public GoodsType createFromParcel(Parcel source) {
            return new GoodsType(source);
        }

        @Override
        public GoodsType[] newArray(int size) {
            return new GoodsType[size];
        }
    };
}
