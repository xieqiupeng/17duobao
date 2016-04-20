package com.zhiri.bear.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MagicBean on 2016/03/03 00:0:51
 */
public class RobGoodsList implements Parcelable {
    public String content;
    public String goodsImageFileKey;
    public int id;
    public String issueNumber;
    public String name;
    public int surplus;
    public int total;
    public int unit;//1 就是1元，10就是10元
    public int defaultUnit;
    public int goodsId;
    public String title;

    public int buyCount = defaultUnit;

    public boolean isCheck;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeString(this.goodsImageFileKey);
        dest.writeInt(this.id);
        dest.writeString(this.issueNumber);
        dest.writeString(this.name);
        dest.writeInt(this.surplus);
        dest.writeInt(this.total);
        dest.writeInt(this.unit);
        dest.writeInt(this.defaultUnit);
        dest.writeInt(this.goodsId);
        dest.writeString(this.title);
        dest.writeInt(this.buyCount);
    }

    public RobGoodsList() {
    }

    protected RobGoodsList(Parcel in) {
        this.content = in.readString();
        this.goodsImageFileKey = in.readString();
        this.id = in.readInt();
        this.issueNumber = in.readString();
        this.name = in.readString();
        this.surplus = in.readInt();
        this.total = in.readInt();
        this.unit = in.readInt();
        this.defaultUnit = in.readInt();
        this.goodsId = in.readInt();
        this.title = in.readString();
        this.buyCount = in.readInt();
    }

    public static final Parcelable.Creator<RobGoodsList> CREATOR = new Parcelable.Creator<RobGoodsList>() {
        public RobGoodsList createFromParcel(Parcel source) {
            return new RobGoodsList(source);
        }

        public RobGoodsList[] newArray(int size) {
            return new RobGoodsList[size];
        }
    };
}
