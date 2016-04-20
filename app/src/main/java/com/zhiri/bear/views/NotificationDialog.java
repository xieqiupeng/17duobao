package com.zhiri.bear.views;

import android.app.Dialog;
import android.content.Context;
import android.net.http.SslError;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.zhiri.bear.R;
import com.zhiri.bear.models.SystemNotificationEntity;
import com.zhiri.bear.utils.JsonParser;
import com.zhiri.bear.utils.WebViewHelper;

import java.util.ArrayList;

/**
 * Created by MagicBean on 2016/03/12 15:15:21
 */
public class NotificationDialog extends Dialog implements View.OnClickListener {
    //    private TextView mTitle;
    private TextView sure;
    private WebView mCustomWebView;
    private ArrayList<SystemNotificationEntity> data;
    private View contentLy;
    private View rootViewLy;
//    private TextView mTime;

    public NotificationDialog(Context context) {
        super(context, R.style.dialogTheme2);
        initializeView();
    }


    private void initializeView() {
        setContentView(R.layout.notification_layout);
//        mTitle = (TextView) findViewById(R.id.mNotificationTitle);
//        mTime = (TextView) findViewById(R.id.mNotification_time);
        mCustomWebView = (WebView) findViewById(R.id.mCustomWebView);
        sure = (TextView) findViewById(R.id.sure_btn);
        contentLy = findViewById(R.id.content_ly);
        rootViewLy = findViewById(R.id.rootView);
        sure.setOnClickListener(this);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);
        rootViewLy.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (((v.getY() > contentLy.getHeight() || v.getY() < contentLy.getY()))
                        || (v.getX() > contentLy.getWidth() || v.getX() < contentLy.getX())) {
                    dismiss();
                }
                return false;
            }
        });
        mCustomWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });
    }

    public NotificationDialog setData(ArrayList<SystemNotificationEntity> data) {
        this.data = data;
        if (data != null) {
//            mCustomWebView.loadData(data.content, "text/html; charset=UTF-8", null);
            final String json = JsonParser.serializeToJson(data);
            mCustomWebView.loadUrl("file:///android_asset/html/systemNotice.html");
            mCustomWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    view.loadUrl("javascript:getNoticeArray(" + json + ")");
                }
            });
            WebViewHelper.setWebView(mCustomWebView);
        }
        return this;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure_btn:
                dismiss();
                break;
        }
    }
}
