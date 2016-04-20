package com.zhiri.bear.ui.user;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;

import com.orhanobut.logger.Logger;
import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.utils.BtnClickUtils;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.utils.TextUtil;
import com.zhiri.bear.views.CustomWebView;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/03/04 12:12:41
 */
public class CommonEditUserInfoActivity extends BaseActivity {
    public static final int REQUEST_CODE = 1001;
    public static final int REQUEST_BACK_CODE = 1002;
    @Bind(R.id.mCustomWebView)
    CustomWebView mCustomWebView;
    private String title;
    private String url;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_profile_setting);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setLeftText("个人资料");
        getCustomActionBar().setLeftTextVisible(true);
        getCustomActionBar().setRightTextColor(R.color.colorRed);
        getCustomActionBar().setRightTextVisible(true);
    }

    @Override
    protected void initializeViews() {
        url = getIntent().getStringExtra("url");
        Logger.i("url:" + url);
        title = getIntent().getStringExtra("title");
        String backTitle = getIntent().getStringExtra("backTitle");
        if (TextUtil.isValidate(backTitle)) {
            getCustomActionBar().setLeftText(backTitle);
        }
        getCustomActionBar().setTitleText(title);
        if (TextUtils.equals(title, "地址列表")) {
            getCustomActionBar().setRightText("添加地址");
        } else if (TextUtils.equals(title, "修改昵称") || (TextUtils.equals(title, "绑定手机号"))) {
            getCustomActionBar().setRightText("保存");
        } else {
            getCustomActionBar().setRightText("下一步");
        }
        mCustomWebView.loadUrl(url);
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
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onActionBarClick(View view) {
        super.onActionBarClick(view);
        switch (view.getId()) {
            case R.id.actionbar_left_txt:
                onBackPressed();
                break;
            case R.id.actionbar_right_txt:
                if (TextUtils.equals(title, "绑定手机号")) {
                    mCustomWebView.loadUrl("javascript:$.phoneModify()");
                } else if (TextUtils.equals(title, "修改昵称")) {
                    mCustomWebView.loadUrl("javascript:$.NameModify()");
                } else if (TextUtils.equals(title, "验证手机号")) {
                    if (!BtnClickUtils.isFastDoubleClick())
                        mCustomWebView.loadUrl("javascript:$.checkBeforePhone()");
                } else {
                    Intent intent = new Intent(CommonEditUserInfoActivity.this, AddUserAddressActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent data) {
        super.onActivityResult(arg0, arg1, data);
        if (arg1 == RESULT_OK) {
            if (arg0 == REQUEST_CODE) {
//                mCustomWebView.reload();
                mCustomWebView.loadUrl(url);
            }
            if (arg0 == REQUEST_BACK_CODE) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    public static class HostJsScope {
        public static void showLoading(WebView webView, int state) {
            ((CommonEditUserInfoActivity) webView.getContext()).showLoading(state);
        }

        public static void centerClick(WebView webView, final String url, String title) {
            Intent intent = new Intent(webView.getContext(), AddUserAddressActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            ((CommonEditUserInfoActivity) webView.getContext()).startActivityForResult(intent, REQUEST_CODE);
        }

        public static void toast(WebView webView, String message) {
            T.showShort(webView.getContext(), message);
        }

        public static void goHome(WebView webView) {
            ((CommonEditUserInfoActivity) webView.getContext()).setResult(RESULT_OK);
            ((CommonEditUserInfoActivity) webView.getContext()).finish();
        }

        public static void checkSuccess(WebView webView, String url) {
            Intent intent = new Intent(webView.getContext(), UpdatePhoneActivity.class);
            intent.putExtra("url", url);
            ((CommonEditUserInfoActivity) webView.getContext()).startActivityForResult(intent, REQUEST_BACK_CODE);
        }

        public static void goHome2(WebView webView, String nickName) {
            App.getInst().getUser().name = nickName;
            ((CommonEditUserInfoActivity) webView.getContext()).setResult(RESULT_OK);
            ((CommonEditUserInfoActivity) webView.getContext()).finish();
        }

        public static void goHome3(WebView webView, String phone) {
            App.getInst().getUser().phone = phone;
            ((CommonEditUserInfoActivity) webView.getContext()).setResult(RESULT_OK);
            ((CommonEditUserInfoActivity) webView.getContext()).finish();
        }
    }
}
