package com.zhiri.bear.ui.grab;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.zhiri.bear.R;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.views.CustomWebView;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/02/29 13:13:20
 */
public class GeneralProblemActivity extends BaseActivity {
    @Bind(R.id.mCustomWebView)
    CustomWebView mCustomWebView;
    /**
     * 常见问题
     */
    public static final int SHOW_GENERAL_PROBLEM = 0;
    /**
     * 什么是一起夺宝
     */
    public static final int SHOW_17_GRAB = 1;
    /**
     * 隐私
     */
    public static final int SHOW_PRIVACY = 2;
    private int showType;
    private String backTitle;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_general_problem);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setTitleText("常见问题");
        getCustomActionBar().setLeftText("个人设置");
        getCustomActionBar().setLeftTextVisible(true);
    }


    @Override
    protected void initializeViews() {
        showType = getIntent().getIntExtra("showType", SHOW_GENERAL_PROBLEM);
        backTitle = getIntent().getStringExtra("backTitle");
        getCustomActionBar().setLeftText(backTitle);
        String url = null;
        switch (showType) {
            case SHOW_GENERAL_PROBLEM:
                url = Api.getWebBaseUrl() + "pages/FAQ.html";
                getCustomActionBar().setTitleText("常见问题");
                break;
            case SHOW_17_GRAB:
                url = Api.getWebBaseUrl() + "pages/17db.html";
                getCustomActionBar().setTitleText("什么是一起夺宝");
                break;
            case SHOW_PRIVACY:
                url = Api.getWebBaseUrl() + "pages/privacy.html";
                getCustomActionBar().setTitleText("隐私政策");
                break;
        }
        mCustomWebView.loadUrl(url);
    }

    public static void toIntent(Activity activity, String backTitle, int showType) {
        Intent intent = new Intent(activity, GeneralProblemActivity.class);
        intent.putExtra("backTitle", backTitle);
        intent.putExtra("showType", showType);
        activity.startActivity(intent);
    }


    @Override
    protected void initializeData() {

    }

    @Override
    public void onClick(View v) {

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
}
