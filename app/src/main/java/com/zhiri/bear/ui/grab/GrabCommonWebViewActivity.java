package com.zhiri.bear.ui.grab;

import android.content.Intent;
import android.view.View;
import android.webkit.WebView;

import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.ui.user.OtherUserProfileActivity;
import com.zhiri.bear.ui.user.ShowOrderDetailsActivity;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.views.CustomWebView;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/03/09 15:15:00
 */
public class GrabCommonWebViewActivity extends BaseActivity {
    @Bind(R.id.mCustomWebView)
    CustomWebView mCustomWebView;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_base_webview);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setLeftText("详情信息");
        getCustomActionBar().setLeftTextVisible(true);
    }

    @Override
    protected void initializeViews() {
        String title = getIntent().getStringExtra("title");
        String url = getIntent().getStringExtra("url");
        getCustomActionBar().setTitleText(title);
        if (getIntent().getStringExtra("backTitle") != null)
        {
            String backTitle = getIntent().getStringExtra("backTitle");
            getCustomActionBar().setLeftText(backTitle);
        }
        mCustomWebView.loadUrl(url);
        mCustomWebView.setInjectedChromeClient(HostJsScope.class);
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
        if (App.getInst().getUser() != null && App.getInst().getUser().token != null)
            mCustomWebView.loadUrl("javascript:$.upEndTimeData('"+ App.getInst().getUser().token+"')");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCustomWebView.onPause();
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

    public void showLoading(int status) {
        if (status == 0) {
            showLoadingDialog();
        } else {
            closeLoadingDialog();
        }
    }

    public static class HostJsScope {
        public static void showLoading(WebView webView, int state) {
            ((GrabCommonWebViewActivity) webView.getContext()).showLoading(state);
        }

        public static void toast(WebView webView, String message) {
            T.showShort(webView.getContext(), message);
        }

        public static void centerClick(WebView webView, final String url, String title) {
            Intent intent = new Intent(webView.getContext(), ShowOrderDetailsActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            intent.putExtra("backTitle", "返回");
            webView.getContext().startActivity(intent);
        }

        public static void goPersonalCenter(WebView webView, String url, String title, String backTitle) {
            GrabCommonWebViewActivity context = (GrabCommonWebViewActivity) webView.getContext();
            Intent intent = new Intent(context, OtherUserProfileActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            intent.putExtra("backTitle", backTitle);
            context.startActivity(intent);
        }

        public static void justPlay(WebView webView, int grabId, int goodsId) {
            GrabCommonWebViewActivity context = (GrabCommonWebViewActivity) webView.getContext();
            Intent intent = new Intent(context, GrabDetailsActivity.class);
            intent.putExtra("goodsId", goodsId);
            intent.putExtra("grabId", grabId);
            intent.putExtra("type", 1);
            context.startActivity(intent);
        }

        public static void goLotteryQuery(WebView webView) {
            Intent intent = new Intent(webView.getContext(), GrabCommonWebViewActivity.class);
            intent.putExtra("url", "http://data.shishicai.cn/cqssc/haoma/");
            intent.putExtra("title", "开奖查询");
            intent.putExtra("backTitle", "计算详情");
            webView.getContext().startActivity(intent);
        }
    }
}
