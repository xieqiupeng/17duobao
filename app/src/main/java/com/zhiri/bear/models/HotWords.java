package com.zhiri.bear.models;

import java.util.ArrayList;

/**
 * Created by MagicBean on 2016/03/07 21:21:18
 */
public class HotWords {
    public String name;
    public int id;
    public int sort;

    public static class BaseHotWords {
        public ArrayList<HotWords> list = new ArrayList<>();
        public Page page;
    }

}
