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
import com.zhiri.bear.ui.grab.GrabDetailsActivity;
import com.zhiri.bear.ui.user.OtherUserProfileActivity;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.views.CustomWebView;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/02/23 15:15:48
 */
public class NewMessageFragment extends BaseFragment {
    @Bind(R.id.mCustomWebView)
    CustomWebView mWebView;
    private String url;
    private static Boolean isFromGrabDetail = false;
    public static NewMessageFragment newInstance() {
        Bundle args = new Bundle();
        NewMessageFragment fragment = new NewMessageFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getRootViewLayoutId() {
        return R.layout.fragment_new_message_layout;
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
        mWebView.loadUrl("javascript:$.upEndTimeData()");
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
            if (!isFromGrabDetail)
            {
                mWebView.loadUrl("javascript:$.reloadData()");
            }
            else {
                isFromGrabDetail = false;
            }
        }
    }

    private void setWebView() {
        String device = HttpParamsHelper.getUrlDeviceInfo();
        url = Api.getWebBaseUrl() + "pages/new-announce.html";
        mWebView.loadUrl(url);
        Logger.i("url:" + url);
        mWebView.setInjectedChromeClient(HostJsScope.class);
    }

    public static void setIsFromGrabDetail(Boolean isFromGrabDetail) {
        NewMessageFragment.isFromGrabDetail = isFromGrabDetail;
    }

    public static class HostJsScope {

        public static void showLoading(WebView webView, int state) {
            NewMessageFragment homeNewFragment = (NewMessageFragment) ((HomeActivity) webView.getContext()).getSupportFragmentManager().findFragmentById(R.id.home_new_fragment);
            if (homeNewFragment != null) {
                homeNewFragment.showLoading(state);
            }
        }

        public static void justPlay(WebView webView, int grabId, int goodsId) {
            Intent intent = new Intent(webView.getContext(), GrabDetailsActivity.class);
            intent.putExtra("goodsId", goodsId);
            intent.putExtra("grabId", grabId);
            intent.putExtra("type", 1);
            intent.putExtra("backTitle", "最新揭晓");
            ((HomeActivity) webView.getContext()).startActivity(intent);
            NewMessageFragment.setIsFromGrabDetail(true);
        }

        public static void goPersonalCenter(WebView webView, String url, String title, String backTitle) {
            HomeActivity context = (HomeActivity) webView.getContext();
            Intent intent = new Intent(context, OtherUserProfileActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            intent.putExtra("backTitle", backTitle);
            context.startActivity(intent);
            NewMessageFragment.setIsFromGrabDetail(true);
        }

        public static void toast(WebView webView, String message) {
            T.showShort(webView.getContext(), message);
        }
    }
}
