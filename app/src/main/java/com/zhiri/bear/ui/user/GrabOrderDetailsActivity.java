package com.zhiri.bear.ui.user;

import android.content.Intent;
import android.view.View;
import android.webkit.WebView;

import com.orhanobut.logger.Logger;
import com.zhiri.bear.R;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.utils.TextUtil;
import com.zhiri.bear.views.CustomWebView;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/03/12 17:17:45
 */
public class GrabOrderDetailsActivity extends BaseActivity {
    @Bind(R.id.mCustomWebView)
    CustomWebView mCustomWebView;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_base_webview);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setLeftTextVisible(true);
        getCustomActionBar().setLeftText("夺宝记录");
    }

    @Override
    protected void initializeViews() {
        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        String backTitle = getIntent().getStringExtra("backTitle");
        if (TextUtil.isValidate(backTitle)) {
            getCustomActionBar().setLeftText(backTitle);
        }
        getCustomActionBar().setTitleText(title);
        mCustomWebView.loadUrl(url);
        Logger.i("url:" + url);
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

    public static class HostJsScope {

        public static void centerClick(WebView webView, final String url, String title) {
            Intent intent = new Intent(webView.getContext(), GrabOrderDetailsActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            intent.putExtra("backTitle", "夺宝详情");
            ((GrabOrderDetailsActivity) webView.getContext()).startActivity(intent);
        }

        public static void toast(WebView webView, String message) {
            T.showShort(webView.getContext(), message);
        }


    }

    @Override
    public void onBackPressed() {
//        Intent intent = new Intent(this, HomeActivity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
//        finish();
        super.onBackPressed();
    }
}
