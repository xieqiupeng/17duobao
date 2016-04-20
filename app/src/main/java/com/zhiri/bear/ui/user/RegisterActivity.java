package com.zhiri.bear.ui.user;

import android.view.View;
import android.webkit.WebView;

import com.igexin.sdk.PushManager;
import com.orhanobut.logger.Logger;
import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.db.SpCarDbHelper;
import com.zhiri.bear.models.User;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.net.HttpResponse;
import com.zhiri.bear.net.RequestCallback;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.ui.grab.GeneralProblemActivity;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.views.CustomWebView;

import java.util.HashMap;

import butterknife.Bind;


/**
 * Created by MagicBean on 2016/02/24 11:11:04
 */
public class RegisterActivity extends BaseActivity {

    @Bind(R.id.mCustomWebView)
    CustomWebView mCustomWebView;
    private boolean fromHome;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setTitleText("注册");
        getCustomActionBar().setLeftText("取消");
        getCustomActionBar().setLeftTextColor(R.color.colorRed);
        getCustomActionBar().setLeftTextVisible(true);
        getCustomActionBar().getLeftText().setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    @Override
    protected void initializeViews() {
        String dev = HttpParamsHelper.getUrlDeviceInfo();
        mCustomWebView.loadUrl(Api.base_url + "pages/register.html?" + dev.substring(1, dev.length()));
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

    public static class HostJsScope {
        public static void showLoading(WebView webView, int state) {
            ((RegisterActivity) webView.getContext()).showLoading(state);
        }

        public static void showAgreement(WebView webView) {
            GeneralProblemActivity.toIntent((RegisterActivity)webView.getContext(), "注册", GeneralProblemActivity.SHOW_PRIVACY);
        }
        public static void toRegister(WebView webView, final String phone, String code) {
            ((RegisterActivity) webView.getContext()).doRegister(phone, code);
        }

        public static void toast(WebView webView, String message) {
            T.showShort(webView.getContext(), message);
        }
    }

    private void doRegister(String phone, String code) {
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        params.put("phone", phone);
        params.put("verifyCode", code);
        Api.getRetrofit().register(params).enqueue(new RequestCallback<HttpResponse<User>>(this) {
            @Override
            public void onSuccess(HttpResponse<User> response) {
                Logger.i("msg:" + response.toString());
                if (response.isSuccess()) {
                    User user = response.getDataFrist();
                    if (user != null) {
                        App.getInst().setUser(user);
                        UserManager.getIns().saveUserInfo(user);
                        SpCarDbHelper.getInst().initDb(user.id + "");
                        PushManager.getInstance().initialize(RegisterActivity.this.getApplicationContext());
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            }

            @Override
            public void onFinish() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
