package com.zhiri.bear.ui.user;

import android.view.View;
import android.webkit.WebView;

import com.orhanobut.logger.Logger;
import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.ui.pay.RechargeActivity;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.views.CustomWebView;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/03/04 15:15:28
 */
public class RechargeRecordActivity extends BaseActivity {
    @Bind(R.id.mCustomWebView)
    CustomWebView mCustomWebView;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_profile_setting);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setTitleText("充值记录");
        getCustomActionBar().setLeftText("我的");
        getCustomActionBar().setLeftTextVisible(true);
        getCustomActionBar().setRightTextColor(R.color.colorRed);
        getCustomActionBar().setRightText("充值");
        getCustomActionBar().setRightTextVisible(true);
    }

    @Override
    protected void initializeViews() {
        if (checkLogin()) {
            String url = Api.base_url + "pages/recharge/recharge-list.html?userId=" + App.getInst().getUser().id + "&token=" + App.getInst().getUser().token+ HttpParamsHelper.getUrlDeviceInfo();
            mCustomWebView.loadUrl(url);
            Logger.i("url:" + url);
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
                startActivity(RechargeActivity.class);
                break;
        }
    }

    public static class HostJsScope {

        public static void showLoading(WebView webView, int state) {
            ((RechargeRecordActivity) webView.getContext()).showLoading(state);
        }
//        public static void centerClick(WebView webView, final String url, String title) {
//            Intent intent = new Intent(webView.getContext(), CommonEditUserInfoActivity.class);
//            intent.putExtra("url", url);
//            intent.putExtra("title", title);
//            webView.getContext().startActivity(intent);
//        }

        public static void toast(WebView webView, String message) {
            T.showShort(webView.getContext(), message);
        }
    }
}
