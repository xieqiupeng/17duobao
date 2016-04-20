package com.zhiri.bear.ui.user;

import android.content.Intent;
import android.view.View;
import android.webkit.WebView;

import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.views.CustomWebView;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/03/12 17:17:45
 */
public class MyShowOrderActivity extends BaseActivity {
    @Bind(R.id.mCustomWebView)
    CustomWebView mCustomWebView;
    private boolean isEdit;
    public static final int REQUEST_CODE = 1001;
    private boolean isDelete;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_base_webview);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setLeftTextVisible(true);
        getCustomActionBar().setLeftText("我的");
        getCustomActionBar().setTitleText("我的晒单");
        getCustomActionBar().setRightTextVisible(true);
        getCustomActionBar().setRightText("编辑");
        getCustomActionBar().setRightTextColor(R.color.colorRed);
    }

    @Override
    protected void initializeViews() {
        if (checkLogin()) {
            mCustomWebView.loadUrl(Api.getWebBaseUrl() + "pages/baskOrder/mybask-order.html?userId=" + App.getInst().getUser().id + HttpParamsHelper.getUrlDeviceInfo());
            mCustomWebView.setInjectedChromeClient(HostJsScope.class);
        }
    }

    @Override
    protected void initializeData() {
    }

    @Override
    public void onClick(View v) {

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
    protected void onActivityResult(int arg0, int arg1, Intent data) {
        super.onActivityResult(arg0, arg1, data);
        if (arg0 == REQUEST_CODE && arg1 == RESULT_OK) {
            mCustomWebView.reload();
        }
    }

    @Override
    public void onActionBarClick(View view) {
        super.onActionBarClick(view);
        switch (view.getId()) {
            case R.id.actionbar_left_txt:
                if (isEdit) {
                    getCustomActionBar().setLeftText("我的");
                    getCustomActionBar().setTitleText("我的晒单");
                    getCustomActionBar().setRightText("编辑");
                    getCustomActionBar().setRightTextColor(R.color.colorRed);
                    getCustomActionBar().setLeftTextColor(R.color.color_light_Black);
                    getCustomActionBar().getLeftText().setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back, 0, 0, 0);
                    mCustomWebView.loadUrl("javascript:cacelList()");
                    getCustomActionBar().getRightText().setEnabled(true);
                    isEdit = false;
                } else {
                    onBackPressed();
                }
                break;
            case R.id.actionbar_right_txt:
                isEdit = !isEdit;
                if (isEdit) {
                    getCustomActionBar().setRightText("删除");
                    getCustomActionBar().setRightTextColor(R.color.colorDivider);
                    getCustomActionBar().setLeftText("取消");
                    getCustomActionBar().setLeftTextColor(R.color.colorRed);
                    getCustomActionBar().getLeftText().setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    mCustomWebView.loadUrl("javascript:editList()");
                    getCustomActionBar().getRightText().setEnabled(false);
                } else {
                    getCustomActionBar().getRightText().setEnabled(true);
                    mCustomWebView.loadUrl("javascript:deleteList()");
                    getCustomActionBar().setLeftText("我的");
                    getCustomActionBar().setTitleText("我的晒单");
                    getCustomActionBar().setRightText("编辑");
                    getCustomActionBar().setRightTextColor(R.color.colorRed);
                    getCustomActionBar().setLeftTextColor(R.color.color_light_Black);
                    getCustomActionBar().getLeftText().setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back, 0, 0, 0);
                }
                break;
        }
    }

    public void updateViewStatus(int status) {
        if (isEdit) {
            getCustomActionBar().setRightText("删除");
            getCustomActionBar().setRightTextColor(status == 0 ? R.color.colorDivider : R.color.colorRed);
            getCustomActionBar().getRightText().setEnabled(status == 0 ? false : true);
        }
    }

    public static class HostJsScope {

        public static void showLoading(WebView webView, int state) {
            ((MyShowOrderActivity) webView.getContext()).showLoading(state);
        }

        public static void centerClick(WebView webView, final String url, String title) {
            Intent intent = new Intent(webView.getContext(), ShowOrderDetailsActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            ((MyShowOrderActivity) webView.getContext()).startActivity(intent);
        }

        public static void justShow(WebView webView, final String url, String title) {
            Intent intent = new Intent(webView.getContext(), PostShowOrderActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            ((MyShowOrderActivity) webView.getContext()).startActivityForResult(intent, REQUEST_CODE);
        }

        public static void toast(WebView webView, String message) {
            T.showShort(webView.getContext(), message);
        }

        public static void isChecked(WebView webView, int size) {
            ((MyShowOrderActivity) webView.getContext()).updateViewStatus(size);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
