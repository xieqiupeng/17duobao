package com.zhiri.bear.ui.base;

import android.support.v4.app.Fragment;

/**
 * Created by MagicBean on 2016/01/27 13:13:28
 */
public class PagerModel {
    public String title;
    public Fragment fragment;

    public PagerModel(String title, Fragment fragment) {
        this.title = title;
        this.fragment = fragment;
    }

    public PagerModel(Fragment fragment) {
        this.fragment = fragment;
    }
}
