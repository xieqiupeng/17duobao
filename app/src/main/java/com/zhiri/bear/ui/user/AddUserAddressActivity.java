package com.zhiri.bear.ui.user;

import android.view.View;
import android.webkit.WebView;

import com.orhanobut.logger.Logger;
import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.utils.TextUtil;
import com.zhiri.bear.views.CustomWebView;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/03/04 12:12:41
 */
public class AddUserAddressActivity extends BaseActivity {
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
        getCustomActionBar().setLeftText("地址列表");
        getCustomActionBar().setLeftTextVisible(true);
        getCustomActionBar().setRightTextColor(R.color.colorRed);
        getCustomActionBar().setRightText("保存");
        getCustomActionBar().setRightTextVisible(true);
        getCustomActionBar().setTitleText("修改地址");
    }

    @Override
    protected void initializeViews() {
        url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        String backTitle = getIntent().getStringExtra("backTitle");
        if (TextUtil.isValidate(backTitle)) {
            getCustomActionBar().setLeftText(backTitle);
        }
        String newUrl = url;
        if (!TextUtil.isValidate(url)) {
            newUrl = Api.getWebBaseUrl() + "pages/personcenter/center_addressModify.html?userId=" + App.getInst().getUser().id + "&token=" + App.getInst().getUser().token;
            title = "添加地址";
        }
        getCustomActionBar().setTitleText(title);
        mCustomWebView.setInjectedChromeClient(HostJsScope.class);
        mCustomWebView.loadUrl(newUrl);
        Logger.i("url:" + newUrl);
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
                if (TextUtil.isValidate(url)) {
                    mCustomWebView.loadUrl("javascript:$.ModifyAddress()");
                } else {
                    mCustomWebView.loadUrl("javascript:$.addAddress()");
                }
                break;
        }
    }

    public static class HostJsScope {

        public static void showLoading(WebView webView, int state) {
            ((AddUserAddressActivity) webView.getContext()).showLoading(state);
        }

        public static void onClick(WebView webView, final String phone, String code) {

        }

        public static void toast(WebView webView, String message) {
            T.showShort(webView.getContext(), message);
        }

        public static void goHome(WebView webView) {
            ((AddUserAddressActivity) webView.getContext()).setResult(RESULT_OK);
            ((AddUserAddressActivity) webView.getContext()).finish();
        }
    }

    @Override
    protected void onDestroy() {
        url = null;
        super.onDestroy();
    }
}
