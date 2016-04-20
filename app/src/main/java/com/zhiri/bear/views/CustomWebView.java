package com.zhiri.bear.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.orhanobut.logger.Logger;
import com.zhiri.bear.R;
import com.zhiri.bear.net.cookie.PersistentCookieStore;
import com.zhiri.bear.utils.TextUtil;

import java.util.List;
import java.util.Map;

import cn.pedant.SafeWebViewBridge.InjectedChromeClient;
import okhttp3.Cookie;

/**
 * Created by MagicBean on 2016/03/02 13:13:54
 */
public class CustomWebView extends FrameLayout {
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private boolean isError;
    /**
     * 默认的js交互对象名
     */
    public static final String INJECTED_NAME = "JSBridge";
    private WebViewClient mWebViewClient;
    private String mJs;

    public CustomWebView(Context context) {
        super(context);
        initializeView();
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView();
    }

    private void initializeView() {
        inflate(getContext(), R.layout.base_webview_layout, this);
        mWebView = (WebView) findViewById(R.id.mWebView);
        mProgressBar = (ProgressBar) findViewById(R.id.webView_progress_bar);
        setWebView();
    }

    private void setWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setAppCacheEnabled(true);  //设置允许缓存
        mWebView.getSettings().setDatabaseEnabled(true); //设置允许使用localstore
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setDomStorageEnabled(true);
//        mWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
//        String appCachePath = mWebView.getContext().getApplicationContext().getCacheDir().getAbsolutePath();
//        mWebView.getSettings().setAppCachePath(appCachePath);
//        String databasePath = mWebView.getContext().getApplicationContext().getDir("databases", Context.MODE_PRIVATE).getPath();
//        mWebView.getSettings().setDatabasePath(databasePath);

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.getSettings().setBlockNetworkLoads(false);
//        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            mWebView.getSettings().setLoadsImagesAutomatically(false);
        }
    }


    public void syncCookie(String url) {
        try {
            CookieSyncManager.createInstance(getContext().getApplicationContext());
            CookieManager cookieManager = CookieManager.getInstance();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.setAcceptThirdPartyCookies(mWebView, true);
            } else {
                cookieManager.setAcceptCookie(true);
            }
            cookieManager.removeSessionCookie();
            cookieManager.removeAllCookie();
            String oldCookie = cookieManager.getCookie(url);
            PersistentCookieStore store = new PersistentCookieStore(getContext().getApplicationContext());
            List<Cookie> cookies = store.getCookies();
            if (TextUtil.isValidate(cookies)) {
                for (Cookie cookie : cookies) {
                    StringBuilder sbCookie = new StringBuilder();
                    sbCookie.append(String.format(cookie.name() + "=%s", cookie.value()));
                    sbCookie.append(String.format(";Domain=%s", cookie.domain()));
                    sbCookie.append(String.format(";Path=%s", cookie.path()));
                    cookieManager.setCookie(url, sbCookie.toString());
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.flush();
            } else {
                CookieSyncManager.getInstance().sync();
            }
            String newCookie = cookieManager.getCookie(url);
            Logger.i("oldCookie:" + oldCookie + "--newCookie:" + newCookie + "--url:" + url);
        } catch (Exception e) {
        }
    }

    /**
     * 判断SD卡是否可用
     */
    private static boolean sdCardIsExit() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡路径
     */
    public static String getSDCardPath() {
        if (sdCardIsExit()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        }
        return null;
    }

    /**
     * 执行js方法
     *
     * @param js
     */
    public void evaluateJavascript(String js) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript(js, null);
        } else {
            mWebView.loadUrl(js);
        }
    }

    public void setProgressBarVisibility(boolean visibility) {
        mProgressBar.setVisibility(visibility ? VISIBLE : GONE);
    }

    public WebView getWebView() {
        return mWebView;
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        removeView(mWebView);
        mWebView.removeAllViews();
        mWebView.destroy();
        super.onDetachedFromWindow();
        mWebView = null;
//        WebViewHelper.releaseAllWebViewCallback();
    }

    /**
     * 注入js交互对象
     *
     * @param injectedName 注入对象名
     * @param injectedCls  注入类
     */
    public void setInjectedChromeClient(String injectedName, Class injectedCls) {
        mWebView.setWebChromeClient(new MyInjectedChromeClient(injectedName, injectedCls));
    }

    /**
     * 默认的交互对象名，只需设置注入class
     *
     * @param injectedCls 注入class
     */
    public void setInjectedChromeClient(Class injectedCls) {
        mWebView.setWebChromeClient(new MyInjectedChromeClient(INJECTED_NAME, injectedCls));
    }

    public void setWebClient(WebViewClient webViewClient) {
        this.mWebViewClient = webViewClient;

    }

    public void loadUrl(String url) {
        if (TextUtil.isValidate(url) && (url.startsWith("http://") || url.startsWith("https://"))) {
            syncCookie(url);
        }
        mWebView.loadUrl(url);
    }

    public void loadData(String data, String mimeType, String encoding) {
        mWebView.loadData(data, mimeType, encoding);
    }

    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        mWebView.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        syncCookie(url);
        mWebView.loadUrl(url, additionalHttpHeaders);
    }

    public void reload() {
        mWebView.reload();
    }

    public boolean canGoBack() {
        return mWebView.canGoBack();
    }

    public void goBack() {
        mWebView.goBack();
    }

    public void onResume() {
        mWebView.onResume();
        mWebView.resumeTimers();
    }

    public void onPause() {
        mWebView.onPause();
        mWebView.pauseTimers();
    }

    public void setInjectedChromeClient(InjectedChromeClient myInjectedChromeClient) {
        mWebView.setWebChromeClient(myInjectedChromeClient);
    }

    public void setLoadingFinishedJs(String js) {
        mJs = js;
    }


    public void onDestroy() {
        if (mWebView != null) {
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mProgressBar.setVisibility(View.GONE);
            view.loadUrl(mJs);
            if (!view.getSettings().getLoadsImagesAutomatically()) {
                view.getSettings().setLoadsImagesAutomatically(true);
            }
        }


        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            Logger.i("onReceivedError:" + error.toString());
            isError = true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }
    }


    private class MyInjectedChromeClient extends InjectedChromeClient {
        public MyInjectedChromeClient(String injectedName, Class injectedCls) {
            super(injectedName, injectedCls);
        }


        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            mProgressBar.setProgress(newProgress);
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            mProgressBar.setProgress(newProgress);
        }
    }
}
