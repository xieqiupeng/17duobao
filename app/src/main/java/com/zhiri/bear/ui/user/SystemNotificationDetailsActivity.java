package com.zhiri.bear.ui.user;

import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.zhiri.bear.R;
import com.zhiri.bear.models.SystemNotificationEntity;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.views.CustomWebView;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/02/29 21:21:18
 */
public class SystemNotificationDetailsActivity extends BaseActivity {
    @Bind(R.id.notification_title)
    TextView notificationTitle;
    @Bind(R.id.notification_time)
    TextView notificationTime;
    @Bind(R.id.mCustomWebView)
    CustomWebView mCustomWebView;
    private SystemNotificationEntity notification;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_notification_details);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setTitleText("通知详情");
        getCustomActionBar().setLeftText("通知");
        getCustomActionBar().setLeftTextVisible(true);
    }

    @Override
    protected void initializeViews() {
        notification = getIntent().getParcelableExtra("notification");
        notificationTitle.setText(notification.title);
        notificationTime.setText(notification.dateTime);
        mCustomWebView.loadData(notification.content, "text/html; charset=UTF-8", null);
        WebView webView = mCustomWebView.getWebView();
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setDatabaseEnabled(false);
//        webView.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
//        webView.getSettings().setDefaultFontSize(50);
//        webView.getSettings().setDefaultFixedFontSize(50);
    }

    @Override
    protected void initializeData() {

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
        }
    }
}
