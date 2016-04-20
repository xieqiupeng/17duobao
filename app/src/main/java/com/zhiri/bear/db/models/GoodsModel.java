package com.zhiri.bear.db.models;

import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.Unique;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by MagicBean on 2016/03/10 18:18:15
 */
@Table("_grab_local_cache")
public class GoodsModel {
    public GoodsModel() {
    }

    public GoodsModel(int id) {
        this.id = id;
    }

    public static final String KEY_GRABID = "grabId";
    public static final String KEY_QUANTITY = "quantity";
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    public int id;
    @NotNull
    @Unique
    public int grabId;
    @NotNull
    public int quantity;
    public int uid;

    @Override
    public String toString() {
        return "GoodsModel{" +
                "grabId=" + grabId +
                ", id=" + id +
                ", quantity=" + quantity +
                ", uid=" + uid +
                '}';
    }
}
