package com.zhiri.bear.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhiri.bear.R;


/**
 * Created by sky.chen on 2016/2/23.
 */
public class CustomTabView extends LinearLayout {
    private TextView mTabTextView;
    public int mIndex;
    private TextView mRedPoint;

    public CustomTabView(Context context) {
        super(context);
        initializeView();
    }

    public CustomTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CustomTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView();
    }

    private void initializeView() {
        inflate(getContext(), R.layout.custom_tab_view, this);
        setGravity(Gravity.CENTER);
        mTabTextView = (TextView) findViewById(R.id.tabTextView);
        mRedPoint = (TextView) findViewById(R.id.red_point);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void notifyDataChanged(CustomTabContainer.TabEntity tabEntity) {
        if (tabEntity == null) throw new NullPointerException("tabEntity is null");
        if (tabEntity.msgCount) {
            setNewMsgCount(0);
        }
        mTabTextView.setCompoundDrawablesWithIntrinsicBounds(0, tabEntity.selectorResId, 0, 0);
        mTabTextView.setText(tabEntity.title);
    }

    public void setChecked(boolean checked) {
        mTabTextView.setSelected(checked);
    }

    public void setNewMsgCount(int count) {
        if (count > 0) {
            mRedPoint.setVisibility(VISIBLE);
            if (count > 99) {
                mRedPoint.setText(99 + "+");
            } else {
                mRedPoint.setText(count + "");
            }
        } else {
            mRedPoint.setVisibility(GONE);
        }
    }
}
