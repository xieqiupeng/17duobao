package com.zhiri.bear.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.orhanobut.logger.Logger;
import com.zhiri.bear.R;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.ui.base.BaseFragment;
import com.zhiri.bear.ui.user.ShowOrderDetailsActivity;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.views.CustomWebView;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/02/23 15:15:48
 */
public class ShowOrderFragment extends BaseFragment {
    @Bind(R.id.mCustomWebView)
    CustomWebView mWebView;
    private String url;

    public static ShowOrderFragment newInstance() {
        Bundle args = new Bundle();
        ShowOrderFragment fragment = new ShowOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getRootViewLayoutId() {
        return R.layout.fragment_show_order_layout;
    }

    @Override
    protected void initializeViews() {
        setWebView();
    }

    @Override
    protected void initializeData() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            mWebView.onPause();
        } else {
            mWebView.onResume();
            mWebView.loadUrl("javascript:$.reloadData()");
        }
    }

    private void setWebView() {
        String device = HttpParamsHelper.getUrlDeviceInfo();
        url = Api.getWebBaseUrl() + "pages/baskOrder/baskorder-share.html";
        mWebView.loadUrl(url);
        Logger.i("url:" + url);
        mWebView.setInjectedChromeClient(HostJsScope.class);
    }

    public static class HostJsScope {

        public static void showLoading(WebView webView, int state) {
            ShowOrderFragment showOrderFragment = (ShowOrderFragment) ((HomeActivity) webView.getContext()).getSupportFragmentManager().findFragmentById(R.id.home_show_fragment);
            if (showOrderFragment != null) {
                showOrderFragment.showLoading(state);
            }
        }

        public static void centerClick(WebView webView, final String url, String title) {
            Intent intent = new Intent(webView.getContext(), ShowOrderDetailsActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            intent.putExtra("backTitle", "晒单分享");
            ((HomeActivity) webView.getContext()).startActivity(intent);
        }

        public static void toast(WebView webView, String message) {
            T.showShort(webView.getContext(), message);
        }
    }
}
