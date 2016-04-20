package com.zhiri.bear.ui.user;

import android.content.Intent;
import android.view.View;
import android.webkit.WebView;

import com.zhiri.bear.R;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.views.CustomWebView;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/03/12 17:17:45
 */
public class WinDetailsViewActivity extends BaseActivity {
    @Bind(R.id.mCustomWebView)
    CustomWebView mCustomWebView;
    public static final int REQUEST_CODE = 1001;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_base_webview);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setLeftText("中奖确认");
        getCustomActionBar().setLeftTextVisible(true);
    }

    @Override
    protected void initializeViews() {
        String title = getIntent().getStringExtra("title");
        getCustomActionBar().setTitleText(title);
        String url = getIntent().getStringExtra("url");
        if (checkLogin()) {
            mCustomWebView.loadUrl(url);
            mCustomWebView.setInjectedChromeClient(HostJsScope.class);
        }
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

                break;
        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent data) {
        super.onActivityResult(arg0, arg1, data);
        if (arg0 == REQUEST_CODE && arg1 == RESULT_OK) {
            mCustomWebView.reload();
        }
    }

    public static class HostJsScope {

        public static void showLoading(WebView webView, int state) {
            ((WinDetailsViewActivity) webView.getContext()).showLoading(state);
        }

        public static void centerClick(WebView webView, final String url, String title) {
            Intent intent = new Intent(webView.getContext(), AddUserAddressActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            intent.putExtra("backTitle", "返回");
            ((WinDetailsViewActivity) webView.getContext()).startActivity(intent);
        }

        public static void centerClick1(WebView webView, final String url, String title) {
            Intent intent = new Intent(webView.getContext(), CommonEditUserInfoActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            intent.putExtra("backTitle", "返回");
            ((WinDetailsViewActivity) webView.getContext()).startActivityForResult(intent, REQUEST_CODE);
        }

        public static void justShow(WebView webView, final String url, String title) {
            Intent intent = new Intent(webView.getContext(), PostShowOrderActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            ((WinDetailsViewActivity) webView.getContext()).startActivityForResult(intent, REQUEST_CODE);
        }

        public static void centerClick2(WebView webView) {
            Intent intent = new Intent(webView.getContext(), MyShowOrderActivity.class);
            ((WinDetailsViewActivity) webView.getContext()).startActivityForResult(intent, REQUEST_CODE);
        }

        public static void toast(WebView webView, String message) {
            T.showShort(webView.getContext(), message);
        }
    }
}
