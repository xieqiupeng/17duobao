package com.zhiri.bear.ui.home;

import android.net.http.SslError;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.orhanobut.logger.Logger;
import com.zhiri.bear.R;
import com.zhiri.bear.models.SystemSetting;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.net.HttpResponse;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.ui.user.UserManager;
import com.zhiri.bear.utils.SharePreHelper;
import com.zhiri.bear.utils.WebViewHelper;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MagicBean on 2016/03/12 16:16:00
 */
public class SplashActivity extends BaseActivity {
    @Bind(R.id.mWebView)
    WebView mWebView;
    public static final int SUCCESS = 0;
    private WeakReferenceHandler mHandler;

    @Override
    protected void setContentView() {
        //无title
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void initializeViews() {
        WebViewHelper.setWebView(mWebView);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        String device = HttpParamsHelper.getUrlDeviceInfo();
        String url = Api.getWebBaseUrl() + "pages/launcher.html?" + device.substring(1, device.length());
        Logger.i("url:" + url);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });
        mWebView.loadUrl(url);
    }

    @Override
    protected void initializeData() {
        UserManager.getIns().refreshUserInfo();
        mHandler = new WeakReferenceHandler(this);
        mHandler.sendEmptyMessageDelayed(SUCCESS, 3 * 1000);
        Api.getRetrofit().getSettings(HttpParamsHelper.createParams()).enqueue(new Callback<HttpResponse<SystemSetting>>() {
            @Override
            public void onResponse(Response<HttpResponse<SystemSetting>> response) {
                if (response != null && response.isSuccess() && response.body() != null) {
                    if (response.body().code == HttpResponse.SUCCESS) {
                        SystemSetting systemSetting = response.body().getDataFrist();
                        if (systemSetting != null) {
                            SharePreHelper.getIns().setAdInterval(systemSetting.wakeUpTime);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    public void goHome() {
        startActivity(HomeActivity.class);
        finish();
    }

    static class WeakReferenceHandler extends Handler {
        WeakReference<SplashActivity> mActivityReference;

        public WeakReferenceHandler(SplashActivity activity) {
            mActivityReference = new WeakReference<SplashActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    if (mActivityReference != null && mActivityReference.get() != null) {
                        mActivityReference.get().goHome();
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mWebView.removeAllViews();
        mWebView.destroy();
        mWebView = null;
        WebViewHelper.releaseAllWebViewCallback();
        super.onDestroy();
    }
}
