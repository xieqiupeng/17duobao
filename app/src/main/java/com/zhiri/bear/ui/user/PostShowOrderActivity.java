package com.zhiri.bear.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.net.HttpResponse;
import com.zhiri.bear.net.RequestCallback;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.utils.BitmapUtil;
import com.zhiri.bear.utils.CameraUtil;
import com.zhiri.bear.utils.SharePreHelper;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.views.CustomWebView;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by MagicBean on 2016/02/29 21:21:18
 */
public class PostShowOrderActivity extends BaseActivity {

    @Bind(R.id.mCustomWebView)
    CustomWebView mCustomWebView;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_post_show_order);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setTitleText("新晒单");
        getCustomActionBar().setLeftText("我的晒单");
        getCustomActionBar().setLeftTextVisible(true);
        getCustomActionBar().setRightText("保存");
        getCustomActionBar().setRightTextColor(R.color.colorRed);
        getCustomActionBar().setRightTextVisible(true);
    }

    @Override
    protected void initializeViews() {
        String title = getIntent().getStringExtra("title");
        getCustomActionBar().setTitleText(title);
        String url = getIntent().getStringExtra("url");
        if (checkLogin()) {
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
    public void onActionBarClick(View view) {
        super.onActionBarClick(view);
        switch (view.getId()) {
            case R.id.actionbar_left_txt:
                onBackPressed();
                break;
            case R.id.actionbar_right_txt:
                mCustomWebView.loadUrl("javascript:saveBaskOrder()");
                break;
        }
    }

    public static class HostJsScope {

        public static void showLoading(WebView webView, int state) {
            ((PostShowOrderActivity) webView.getContext()).showLoading(state);
        }

        public static void toast(WebView webView, String message) {
            T.showShort(webView.getContext(), message);
        }

        public static void OpenCamera(WebView webView) {
            ((PostShowOrderActivity) webView.getContext()).startActivityForResult(CameraUtil.getCameraIntent(), CameraUtil.TAKE_PHOTO);
        }

        public static void GetPhotos(WebView webView) {
            ((PostShowOrderActivity) webView.getContext()).startActivityForResult(CameraUtil.getAlbumIntent(), CameraUtil.PICK_PHOTO);
        }

        public static void goHome(WebView webView) {
            ((PostShowOrderActivity) webView.getContext()).setResult(RESULT_OK);
            ((PostShowOrderActivity) webView.getContext()).finish();
        }
    }

    private boolean uploadImage(String path) {
        if (!checkNetWork()) return false;
        File file = new File(path);
        if (!file.exists()) return true;
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);
        MultipartBody boy = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("device.id", HttpParamsHelper.device.deviceId)
                .addFormDataPart("device.osVersion", HttpParamsHelper.device.deviceOsVersion)
                .addFormDataPart("device.type", HttpParamsHelper.device.deviceType)
                .addFormDataPart("device.appVersion", HttpParamsHelper.device.deviceAppVersion + "")
                .addFormDataPart("device.model", HttpParamsHelper.device.deviceModel)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();
        showLoadingDialog();
        Api.getRetrofit().uploadFacePic(boy).enqueue(new RequestCallback<HttpResponse<ProfileSettingActivity.UploadImageResult>>() {
            @Override
            public void onSuccess(HttpResponse<ProfileSettingActivity.UploadImageResult> response) {
                T.showShort(getApplicationContext(), response.getMessage());
                if (response != null && response.isSuccess()) {
                    App.getInst().getUser().image = response.getDataFrist().fid;
                    modifyUserInfo(response.getDataFrist().fid);
                }
            }

            @Override
            public void onFinish() {
                closeLoadingDialog();
            }

        });
        return false;
    }


    private void modifyUserInfo(String fid) {
        mCustomWebView.loadUrl("javascript:$.getAppFileKeys('" + fid + "')");
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SharePreHelper.getIns().setShouldShowNotification(false);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CameraUtil.TAKE_PHOTO:
                    String path = CameraUtil.getRealFilePath();
                    if (new File(path).exists()) {
                        //图片存在？
                        Logger.i("imagePath:" + path);
                        try {
                            Bitmap temp = BitmapUtil.revitionImageSize(path);
                            path = BitmapUtil.saveBitmap(this, temp, path);
                            if (uploadImage(path)) return;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                    break;
                case CameraUtil.PICK_PHOTO:
                    try {
                        String pickPath = CameraUtil.resolvePhotoFromIntent(PostShowOrderActivity.this, data);
                        Uri uri = Uri.fromFile(new File(pickPath));
                        if (!new File(pickPath).exists()) {
                            Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        Bitmap header = BitmapUtil.revitionImageSize(pickPath);
                        String finalPath = BitmapUtil.saveBitmap(this, header, pickPath);
                        if (uploadImage(finalPath)) return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
