package com.zhiri.bear.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sky.chen on 2016/2/23.
 */
public class CustomTabContainer extends LinearLayout implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private Context mContext;
    private LayoutParams mLayoutParams;
    private OnTabChangeListener mTabChangeListener;
    private List<TabEntity> mTabs;
    private int mChildCount;
    private ViewPager mPager;

    public CustomTabContainer(Context context) {
        super(context);
        initializeView();
    }

    public CustomTabContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CustomTabContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView();
    }

    private void initializeView() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        mContext = getContext();
        mLayoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
    }

    public void setViewPager(ViewPager pager) {
        this.mPager = pager;
        if (mPager != null) {
            mPager.addOnPageChangeListener(this);
        }
    }

    public CustomTabContainer addTab(TabEntity tab) {
        if (mTabs == null) {
            mTabs = new ArrayList<>();
        }
        mTabs.add(tab);
        return this;
    }

    public void notifyDataChanged() {
        createTabView();
    }

    public void notifyDataChanged(List<TabEntity> tabs) {
        this.mTabs = tabs;
        createTabView();
    }

    private void createTabView() {
        if (mTabs == null) {
            throw new NullPointerException("mTabs is null");
        }
        for (int i = 0; i < mTabs.size(); i++) {
            TabEntity tabEntity = mTabs.get(i);
            CustomTabView tab = new CustomTabView(mContext);
            if (i == 0) {
                tab.setChecked(true);
            }
            tab.notifyDataChanged(tabEntity);
            tab.setLayoutParams(mLayoutParams);
            tab.setOnClickListener(this);
            tab.mIndex = i;
            addView(tab);
        }
        mChildCount = getChildCount();
    }

    int lastIndex = 0;

    @Override
    public void onClick(View v) {
        CustomTabView tab = (CustomTabView) v;
        if (tab.mIndex == lastIndex) {
            return;
        }
        selectTab(tab.mIndex);
        lastIndex = tab.mIndex;
    }


    public void selectTab(int tabIndex) {
        if (mChildCount > 0 && tabIndex >= 0 && tabIndex < mChildCount) {
            for (int i = 0; i < mChildCount; i++) {
                if (tabIndex == i) {
                    ((CustomTabView) getChildAt(tabIndex)).setChecked(true);
                    if (mTabChangeListener != null) {
                        mTabChangeListener.onTabChange(tabIndex, mTabs.get(tabIndex));
                    }
                    lastIndex = tabIndex;
                } else {
                    ((CustomTabView) getChildAt(i)).setChecked(false);
                }
            }
        }
    }

    public void setTabNewMsgCount(int tabIndex, int count) {
        int childCount = getChildCount();
        if (childCount > 0 && tabIndex >= 0 && tabIndex < childCount) {
            ((CustomTabView) getChildAt(tabIndex)).setNewMsgCount(count);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selectTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static class TabEntity {
        public String title;
        public int selectorResId;
        public boolean msgCount = false;
        public int tabType;

        public TabEntity(String title, int selectorResId) {
            this.title = title;
            this.selectorResId = selectorResId;
        }

        public TabEntity(String title, int selectorResId, boolean msgCount) {
            this.title = title;
            this.selectorResId = selectorResId;
            this.msgCount = msgCount;
        }
    }

    public static interface OnTabChangeListener {
        void onTabChange(int position, TabEntity tab);
    }

    public void setOnTabChangeListener(OnTabChangeListener listener) {
        this.mTabChangeListener = listener;
    }

}
