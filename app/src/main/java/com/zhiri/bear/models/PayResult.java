package com.zhiri.bear.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MagicBean on 2016/03/14 21:21:59
 */
public class PayResult implements Parcelable {
    public int robGoodsId;
    public int code;
    public int orderId;
    public String[] robNumbers;
    public String name;
    public String issueNumber;
    public String buyNumber;
    public int totalBuyNumber;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.robGoodsId);
        dest.writeInt(this.code);
        dest.writeInt(this.orderId);
        dest.writeStringArray(this.robNumbers);
        dest.writeString(this.name);
        dest.writeString(this.issueNumber);
        dest.writeString(this.buyNumber);
        dest.writeInt(this.totalBuyNumber);
    }

    public PayResult() {
    }

    protected PayResult(Parcel in) {
        this.robGoodsId = in.readInt();
        this.code = in.readInt();
        this.orderId = in.readInt();
        this.robNumbers = in.createStringArray();
        this.name = in.readString();
        this.issueNumber = in.readString();
        this.buyNumber = in.readString();
        this.totalBuyNumber = in.readInt();
    }

    public static final Parcelable.Creator<PayResult> CREATOR = new Parcelable.Creator<PayResult>() {
        @Override
        public PayResult createFromParcel(Parcel source) {
            return new PayResult(source);
        }

        @Override
        public PayResult[] newArray(int size) {
            return new PayResult[size];
        }
    };
}
