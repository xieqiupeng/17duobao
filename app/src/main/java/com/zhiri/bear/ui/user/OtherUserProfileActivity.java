package com.zhiri.bear.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.webkit.WebView;

import com.orhanobut.logger.Logger;
import com.zhiri.bear.R;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.ui.grab.GrabDetailsActivity;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.views.CustomWebView;
import com.zhiri.bear.views.LoadingDialog;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/02/29 13:13:25
 */
public class OtherUserProfileActivity extends BaseActivity {
    @Bind(R.id.mCustomWebView)
    CustomWebView mCustomWebView;
    private LoadingDialog loadingDialog;
    public static final int REQUEST_CODE = 1001;
    private String title;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_profile_setting);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setTitleText("个人中心");
        getCustomActionBar().setLeftText("个人设置");
        getCustomActionBar().setLeftTextVisible(true);
    }

    @Override
    protected void initializeViews() {
        String url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        String backTitle = getIntent().getStringExtra("backTitle");
        getCustomActionBar().setLeftText(backTitle);
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
        }
    }

    public String getActTitle() {
        return title;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public static class HostJsScope {

        public static void showLoading(WebView webView, int state) {
            ((OtherUserProfileActivity) webView.getContext()).showLoading(state);
        }

        public static void centerClick(WebView webView, final String url, String title) {
            Intent intent = new Intent(webView.getContext(), GrabOrderDetailsActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            intent.putExtra("backTitle", "返回");
            ((OtherUserProfileActivity) webView.getContext()).startActivity(intent);
        }

        public static void justPlay(WebView webView, int grabId, int goodsId) {
            OtherUserProfileActivity context = (OtherUserProfileActivity) webView.getContext();
            Intent intent = new Intent(context, GrabDetailsActivity.class);
            intent.putExtra("goodsId", goodsId);
            intent.putExtra("grabId", grabId);
            intent.putExtra("type", 1);
            context.startActivity(intent);
        }

        public static void toast(WebView webView, String message) {
            T.showShort(webView.getContext(), message);
        }

        public static void jumpDetails(WebView webView, final String url, String title) {
            Intent intent = new Intent(webView.getContext(), ShowOrderDetailsActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            ((OtherUserProfileActivity) webView.getContext()).startActivity(intent);
        }
    }


    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    mCustomWebView.reload();
                    break;
            }
        }
    }
}
