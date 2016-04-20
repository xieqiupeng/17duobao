package com.zhiri.bear.ui.user;

import android.view.View;
import android.webkit.WebView;

import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.views.CustomWebView;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/03/04 12:12:41
 */
public class UpdatePhoneActivity extends BaseActivity {
    @Bind(R.id.mCustomWebView)
    CustomWebView mCustomWebView;
    private String url;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_profile_setting);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setLeftText("验证手机号");
        getCustomActionBar().setLeftTextVisible(true);
        getCustomActionBar().setRightTextColor(R.color.colorRed);
        getCustomActionBar().setRightText("保存");
        getCustomActionBar().setRightTextVisible(true);
        getCustomActionBar().setTitleText("验证手机号");
    }

    @Override
    protected void initializeViews() {
        getCustomActionBar().setTitleText("修改手机号");
        url = getIntent().getStringExtra("url");
        mCustomWebView.loadUrl(url);
        mCustomWebView.setInjectedChromeClient(HostJsScope.class);
    }

    @Override
    protected void initializeData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCustomWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCustomWebView.onPause();
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
            case R.id.actionbar_right_txt:
                mCustomWebView.loadUrl("javascript:$.phoneModify()");
                break;
        }
    }

    public static class HostJsScope {

        public static void showLoading(WebView webView, int state) {
            ((UpdatePhoneActivity) webView.getContext()).showLoading(state);
        }

        public static void onClick(WebView webView, final String phone, String code) {

        }

        public static void toast(WebView webView, String message) {
            T.showShort(webView.getContext(), message);
        }

        public static void goHome(WebView webView) {
            ((UpdatePhoneActivity) webView.getContext()).setResult(RESULT_OK);
            ((UpdatePhoneActivity) webView.getContext()).finish();
        }

        public static void goHome3(WebView webView, String phone) {
            App.getInst().getUser().phone = phone;
            ((UpdatePhoneActivity) webView.getContext()).setResult(RESULT_OK);
            ((UpdatePhoneActivity) webView.getContext()).finish();
        }
    }

    @Override
    protected void onDestroy() {
        url = null;
        mCustomWebView.getWebView().destroy();
        super.onDestroy();
    }
}
