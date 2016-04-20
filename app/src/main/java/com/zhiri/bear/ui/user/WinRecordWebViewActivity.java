package com.zhiri.bear.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.ui.grab.GrabDetailsActivity;
import com.zhiri.bear.ui.home.HomeActivity;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.utils.TextUtil;
import com.zhiri.bear.views.CustomWebView;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/03/12 17:17:45
 */
public class WinRecordWebViewActivity extends BaseActivity {
    @Bind(R.id.mCustomWebView)
    CustomWebView mCustomWebView;
    /**
     * 所有
     */
    public static final int ALL_ORDER = 1;
    /**
     * 进行中
     */
    public static final int DOING_ORDER = 2;
    /**
     * 结束
     */
    public static final int FINISH_ORDER = 3;
    private int status;

    public static final int REQUEST_CODE = 1001;
    private String url;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_base_webview_2);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setLeftText("我的");
        getCustomActionBar().setLeftTextVisible(true);
        getCustomActionBar().setTitleText("中奖记录");
    }

    @Override
    protected void initializeViews() {
        String title = getIntent().getStringExtra("title");
        if (TextUtil.isValidate(title)) {
            getCustomActionBar().setTitleText(title);
        } else {
            getCustomActionBar().setLeftText("返回");
        }
        if (checkLogin()) {
            url = Api.base_url + "pages/getAward/getAward_index.html?token=" + App.getInst().getUser().token + HttpParamsHelper.getUrlDeviceInfo();
            mCustomWebView.loadUrl(url);
            mCustomWebView.setInjectedChromeClient(HostJsScope.class);
        }
    }

    @Override
    protected void initializeData() {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (mCustomWebView != null) {
            mCustomWebView.reload();
        }
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

    public static Bundle putIntentExtra(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        return bundle;
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
    protected void onRestart() {
        super.onRestart();
        mCustomWebView.reload();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent data) {
        super.onActivityResult(arg0, arg1, data);
        if (arg0 == REQUEST_CODE && arg1 == RESULT_OK) {
            mCustomWebView.loadUrl("javascript:$.DomLoadFinish()");
        }
    }

    public static class HostJsScope {

        public static void showLoading(WebView webView, int state) {
            ((WinRecordWebViewActivity) webView.getContext()).showLoading(state);
        }

        public static void centerClick(WebView webView, final String url, String title) {
            Intent intent = new Intent(webView.getContext(), WinDetailsViewActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            ((WinRecordWebViewActivity) webView.getContext()).startActivity(intent);
        }

        public static void centerClick1(WebView webView, final String url, String title) {
            Intent intent = new Intent(webView.getContext(), CommonEditUserInfoActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            intent.putExtra("backTitle", "返回");
            ((WinRecordWebViewActivity) webView.getContext()).startActivityForResult(intent, REQUEST_CODE);
        }

        public static void justShow(WebView webView, final String url, String title) {
            Intent intent = new Intent(webView.getContext(), PostShowOrderActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            ((WinRecordWebViewActivity) webView.getContext()).startActivityForResult(intent, REQUEST_CODE);
        }

        public static void toast(WebView webView, String message) {
            T.showShort(webView.getContext(), message);
        }

        public static void justPlay1(WebView webView) {
            WinRecordWebViewActivity context = (WinRecordWebViewActivity) webView.getContext();
            Intent intent = new Intent(context, HomeActivity.class);
            intent.putExtra("show_position", 0);
            context.startActivity(intent);
            context.finish();
        }

        public static void justPlay(WebView webView, int grabId, int goodsId) {
            WinRecordWebViewActivity context = (WinRecordWebViewActivity) webView.getContext();
            Intent intent = new Intent(context, GrabDetailsActivity.class);
            intent.putExtra("goodsId", goodsId);
            intent.putExtra("grabId", grabId);
            intent.putExtra("type", 1);
            context.startActivity(intent);
        }
    }
}
