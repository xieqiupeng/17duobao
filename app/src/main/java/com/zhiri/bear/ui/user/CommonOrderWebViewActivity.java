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
import com.zhiri.bear.views.CustomWebView;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/03/12 17:17:45
 */
public class CommonOrderWebViewActivity extends BaseActivity {
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
    private boolean isHome;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_base_webview);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setLeftText("我的");
        getCustomActionBar().setLeftTextVisible(true);
    }

    @Override
    protected void initializeViews() {
        getCustomActionBar().setTitleText("夺宝记录");
        if (checkLogin()) {
            status = getIntent().getIntExtra("status", ALL_ORDER);
            String url = Api.base_url + "pages/awardrecords/records-index.html?status=" + status + "&token=" + App.getInst().getUser().token + HttpParamsHelper.getUrlDeviceInfo();
//            mCustomWebView.setLoadingFinishedJs("javascript:$.DomLoadFinish()");
            mCustomWebView.loadUrl(url);
            mCustomWebView.setInjectedChromeClient(HostJsScope.class);
        }
        isHome = getIntent().getBooleanExtra("isHome", false);
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
    protected void onDestroy() {
        super.onDestroy();
    }

    public static Bundle putIntentExtra(int status) {
        Bundle bundle = new Bundle();
        bundle.putInt("status", status);
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

    public static class HostJsScope {

        public static void showLoading(WebView webView, int state) {
            ((CommonOrderWebViewActivity) webView.getContext()).showLoading(state);
        }

        public static void justPlay(WebView webView, int grabId, int goodsId) {
            CommonOrderWebViewActivity context = (CommonOrderWebViewActivity) webView.getContext();
            Intent intent = new Intent(context, GrabDetailsActivity.class);
            intent.putExtra("goodsId", goodsId);
            intent.putExtra("grabId", grabId);
            intent.putExtra("type", 1);
            context.startActivity(intent);
        }

        public static void toast(WebView webView, String message) {
            T.showShort(webView.getContext(), message);
        }

        public static void centerClick(WebView webView, final String url, String title) {
            Intent intent = new Intent(webView.getContext(), GrabOrderDetailsActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            intent.putExtra("backTitle", "夺宝记录");
            ((CommonOrderWebViewActivity) webView.getContext()).startActivity(intent);
        }

        public static void justPlay1(WebView webView) {
            CommonOrderWebViewActivity context = (CommonOrderWebViewActivity) webView.getContext();
            Intent intent = new Intent(context, HomeActivity.class);
            intent.putExtra("show_position", 0);
            context.startActivity(intent);
            context.finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (isHome) {
            intent.putExtra("show_position", 0);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
        finish();
//        super.onBackPressed();
    }
}
