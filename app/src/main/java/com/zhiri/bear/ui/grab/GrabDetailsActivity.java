package com.zhiri.bear.ui.grab;

import android.content.Intent;
import android.view.View;
import android.webkit.WebView;

import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.db.SpCarDbHelper;
import com.zhiri.bear.db.models.GoodsModel;
import com.zhiri.bear.models.User;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.ui.home.HomeActivity;
import com.zhiri.bear.ui.user.LoginActivity;
import com.zhiri.bear.ui.user.OtherUserProfileActivity;
import com.zhiri.bear.ui.user.UserManager;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.utils.TextUtil;
import com.zhiri.bear.views.CustomWebView;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/03/09 14:14:05
 */
public class GrabDetailsActivity extends BaseActivity {
    @Bind(R.id.mCustomWebView)
    CustomWebView mCustomWebView;
    private int goodsId;
    private User user;
    private int type;
    private int grabId;
    private String backTitle;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_base_webview);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setTitleText("详情信息");
        getCustomActionBar().setLeftText("返回");
        getCustomActionBar().setLeftTextVisible(true);
    }

    @Override
    protected void initializeViews() {
        goodsId = getIntent().getIntExtra("goodsId", 0);
        grabId = getIntent().getIntExtra("grabId", 0);
        type = getIntent().getIntExtra("type", 0);
        backTitle = getIntent().getStringExtra("backTitle");
        user = App.getInst().getUser();
    }

    @Override
    protected void initializeData() {
        boolean status = false;
        if (user != null) {
            status = SpCarDbHelper.getInst().getGoodsNumber(grabId);
        } else {
            status = UserManager.getIns().getGoodsMap().isEmpty();
        }
        String url = Api.base_url + "pages/awardflow/viper_details.html?goodsId=" + goodsId + "&orderNumber=" + getCarSize() + "&grabId=" + grabId + "&status=" + status + "&statusType=" + type + HttpParamsHelper.getUrlDeviceInfo();
        if (App.getInst().getUser() != null) {
            url += ("&token=" + App.getInst().getUser().token);
        }
        if (TextUtil.isValidate(backTitle)) {
            getCustomActionBar().setLeftText(backTitle);
        }
        mCustomWebView.loadUrl(url);
        mCustomWebView.setInjectedChromeClient(HostJsScope.class);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCustomWebView.onResume();
        if (App.getInst().getUser() != null && App.getInst().getUser().token != null)
            mCustomWebView.loadUrl("javascript:$.upEndTimeData('"+ App.getInst().getUser().token+"')");
        else
            mCustomWebView.loadUrl("javascript:$.upEndTimeData()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCustomWebView.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        mCustomWebView.reload();
    }

    public int getCarSize() {
        if (App.getInst().getUser() != null) {
            ArrayList<GoodsModel> temps = SpCarDbHelper.getInst().getAllGoods();
            if (temps != null) {
                return temps.size();
            }
            return 0;
        } else {
            return UserManager.getIns().getGoodsMap().size();
        }
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

    public static class HostJsScope {
        public static void showLoading(WebView webView, int state) {
            ((GrabDetailsActivity) webView.getContext()).showLoading(state);
        }

        public static void centerClick(WebView webView, final String url, String title) {
            Intent intent = new Intent(webView.getContext(), GrabCommonWebViewActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            webView.getContext().startActivity(intent);
        }

        public static void toast(WebView webView, String message) {
            T.showShort(webView.getContext(), message);
        }

        public static void loginPage(WebView webView) {
            Intent intent = new Intent(webView.getContext(), LoginActivity.class);
            webView.getContext().startActivity(intent);
        }

        public static void goodsParam(WebView webView, int grabId, String quantity) {
            GoodsModel goodsModel = new GoodsModel();
            goodsModel.grabId = grabId;
            goodsModel.quantity = Integer.parseInt(quantity);
            if (App.getInst().getUser() == null) {
                UserManager.getIns().addGoodsModel(goodsModel);
            } else {
                SpCarDbHelper.getInst().saveGoods(goodsModel);
            }
        }

        public static void justPlay(WebView webView, int grabId, String quantity) {
            GoodsModel goodsModel = new GoodsModel();
            goodsModel.grabId = grabId;
            goodsModel.quantity = Integer.parseInt(quantity);
            if (App.getInst().getUser() == null) {
                UserManager.getIns().addGoodsModel(goodsModel);
            } else {
                SpCarDbHelper.getInst().saveGoods(goodsModel);
            }
            GrabDetailsActivity context = (GrabDetailsActivity) webView.getContext();
            Intent intent = new Intent(context, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("show_position", 3);
            context.startActivity(intent);
            context.finish();
        }

        public static void justPlay2(WebView webView, int goodsId, int grabId) {
            GrabDetailsActivity context = (GrabDetailsActivity) webView.getContext();
            context.goodsId = goodsId;
            context.grabId = grabId;
            context.type = 0;
            context.initializeData();
        }

        public static void goPersonalCenter(WebView webView, String url, String title, String backTitle) {
            GrabDetailsActivity context = (GrabDetailsActivity) webView.getContext();
            Intent intent = new Intent(context, OtherUserProfileActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            intent.putExtra("backTitle", backTitle);
            context.startActivity(intent);
        }

        public static void toOrderList(WebView webView) {
            GrabDetailsActivity context = (GrabDetailsActivity) webView.getContext();
            Intent intent = new Intent(context, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("show_position", 3);
            context.startActivity(intent);
            context.finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (mCustomWebView != null && mCustomWebView.canGoBack()) {
            mCustomWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
