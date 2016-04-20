package com.zhiri.bear.ui.pay;

import android.content.Intent;
import android.view.View;
import android.webkit.WebView;

import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.models.User;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.ui.grab.GrabDetailsActivity;
import com.zhiri.bear.ui.home.HomeActivity;
import com.zhiri.bear.ui.user.CommonEditUserInfoActivity;
import com.zhiri.bear.ui.user.CommonOrderWebViewActivity;
import com.zhiri.bear.utils.SharePreHelper;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.utils.TextUtil;
import com.zhiri.bear.views.CustomWebView;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/03/14 20:20:45
 */
public class PayStatusActivity extends BaseActivity {
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
        getCustomActionBar().setLeftText("返回");
        getCustomActionBar().setLeftTextVisible(true);
        getCustomActionBar().setTitleText("支付结果");
    }

    @Override
    protected void initializeViews() {
        String params = SharePreHelper.getIns().getPayResultInfo();
        User user = App.getInst().getUser();
        if (user != null && TextUtil.isValidate(params)) {
            String url = Api.base_url + "pages/personcenter/pay_result.html?token=" + user.token + "&phone=" + user.phone + HttpParamsHelper.getUrlDeviceInfo() + "&userId=" + user.id;
            mCustomWebView.setLoadingFinishedJs("javascript:$.getParam(" + params + ")");
            mCustomWebView.loadUrl(url);
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
    protected void onRestart() {
        super.onRestart();
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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("show_position", 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent data) {
        super.onActivityResult(arg0, arg1, data);
        switch (arg0) {
            case REQUEST_CODE:
                User user = App.getInst().getUser();
                if (user != null) {
                    String url = Api.base_url + "pages/personcenter/pay_result.html?token=" + user.token + "&phone=" + user.phone + HttpParamsHelper.getUrlDeviceInfo() + "&userId=" + user.id;
                    mCustomWebView.loadUrl(url);
                }
                break;
        }
    }

    public static class HostJsScope {

        public static void showLoading(WebView webView, int state) {
            ((PayStatusActivity) webView.getContext()).showLoading(state);
        }

        public static void toast(WebView webView, String message) {
            T.showShort(webView.getContext(), message);
        }

        public static void jumpPage(WebView webView, int type) {
            Intent intent = new Intent();
            PayStatusActivity act = (PayStatusActivity) webView.getContext();
            if (type == 0) {
                intent.setClass(act, HomeActivity.class);
                intent.putExtra("show_position", 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else if (type == 1) {
                intent.setClass(act, CommonOrderWebViewActivity.class);
                intent.putExtra("status", CommonOrderWebViewActivity.ALL_ORDER);
                intent.putExtra("isHome", true);
            }
            act.startActivity(intent);
            act.finish();
        }

        public static void justPlay(WebView webView, int grabId) {
            PayStatusActivity context = (PayStatusActivity) webView.getContext();
            Intent intent = new Intent(context, GrabDetailsActivity.class);
            intent.putExtra("grabId", grabId);
            intent.putExtra("type", 1);
//            intent.putExtra("backTitle", "支付结果");
            context.startActivity(intent);
        }

        public static void centerClick(WebView webView, final String url, String title) {
            Intent intent = new Intent(webView.getContext(), CommonEditUserInfoActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            ((PayStatusActivity) webView.getContext()).startActivityForResult(intent, REQUEST_CODE);
        }
    }
}
