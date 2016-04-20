package com.zhiri.bear.models;

/**
 * Created by MagicBean on 2016/04/06 16:16:13
 */
public class ProfileViewItem {
    public int itemIconRes;
    public String itemName;
    public boolean hasDivider;
    public boolean hasNorDivider = true;

    public ProfileViewItem(int itemIconRes, String itemName) {
        this.itemIconRes = itemIconRes;
        this.itemName = itemName;
    }

    public ProfileViewItem(int itemIconRes, String itemName, boolean hasDivider) {
        this.hasDivider = hasDivider;
        this.itemIconRes = itemIconRes;
        this.itemName = itemName;
    }

    public ProfileViewItem(boolean hasNorDivider, int itemIconRes, String itemName) {
        this.hasNorDivider = hasNorDivider;
        this.itemIconRes = itemIconRes;
        this.itemName = itemName;
    }
}
