package com.zhiri.bear.models;

import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.Unique;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by MagicBean on 2016/03/08 17:17:38
 */
@Table("_serach_histroy")
public class HistoryKeyWords {
    public static final String KEY_UID = "uid";
    public static final String KEY_WORDS = "keyWords";
    public static final String KEY_ID = "id";
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    public int id;
    @NotNull
    @Unique
    public String keyWords;
    @NotNull
    public int uid;

    public HistoryKeyWords() {
    }

    public HistoryKeyWords(String keyWords, int uid) {
        this.keyWords = keyWords;
        this.uid = uid;
    }

    public HistoryKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    @Override
    public String toString() {
        return "HistoryKeyWords{" +
                "id=" + id +
                ", keyWords='" + keyWords + '\'' +
                ", uid=" + uid +
                '}';
    }
}