package com.zhiri.bear.ui.grab;

import android.view.View;

import com.zhiri.bear.R;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.views.CustomWebView;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/03/07 20:20:05
 */
public class BannerWebViewActivity extends BaseActivity {
    @Bind(R.id.mCustomWebView)
    CustomWebView mCustomWebView;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_common_userinfo_edit);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setLeftText("取消");
        getCustomActionBar().setLeftTextColor(R.color.colorRed);
        getCustomActionBar().setLeftTextVisible(true);
    }

    @Override
    protected void initializeViews() {
        String url = getIntent().getStringExtra("url");
        mCustomWebView.loadUrl(url);
        String title = getIntent().getStringExtra("title");
        getCustomActionBar().setTitleText(title);
    }

    @Override
    protected void initializeData() {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mCustomWebView.reload();
    }

    @Override
    public void onActionBarClick(View view) {
        super.onActionBarClick(view);
        switch (view.getId()) {
            case R.id.actionbar_left_txt:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onClick(View v) {

    }
}
