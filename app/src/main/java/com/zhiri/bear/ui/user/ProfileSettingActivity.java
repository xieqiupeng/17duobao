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
import com.zhiri.bear.views.LoadingDialog;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import butterknife.Bind;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by MagicBean on 2016/02/29 13:13:25
 */
public class ProfileSettingActivity extends BaseActivity {
    @Bind(R.id.mCustomWebView)
    CustomWebView mCustomWebView;
    private LoadingDialog loadingDialog;
    public static final int REQUEST_CODE = 1001;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_profile_setting);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setTitleText("个人资料");
        getCustomActionBar().setLeftText("个人设置");
        getCustomActionBar().setLeftTextVisible(true);
    }

    @Override
    protected void initializeViews() {
        if (checkLogin()) {
            String url = Api.getWebBaseUrl() + "pages/personcenter/center_index.html?token=" + App.getInst().getUser().token + HttpParamsHelper.getUrlDeviceInfo();
            mCustomWebView.loadUrl(url);
            Logger.i("url:" + url);
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

    @Override
    protected void onRestart() {
        super.onRestart();
//        mCustomWebView.reload();
    }

    public static class HostJsScope {

        public static void showLoading(WebView webView, int state) {
            ((ProfileSettingActivity) webView.getContext()).showLoading(state);
        }

        public static void centerClick(WebView webView, final String url, String title) {
            Intent intent = new Intent(webView.getContext(), CommonEditUserInfoActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
//            webView.getContext().startActivity(intent);
            ((ProfileSettingActivity) webView.getContext()).startActivityForResult(intent, REQUEST_CODE);
        }

        public static void toast(WebView webView, String message) {
            T.showShort(webView.getContext(), message);
        }

        public static void OpenCamera(WebView webView) {
            ((ProfileSettingActivity) webView.getContext()).startActivityForResult(CameraUtil.getCameraIntent(), CameraUtil.TAKE_PHOTO);
        }

        public static void GetPhotos(WebView webView) {
            ((ProfileSettingActivity) webView.getContext()).startActivityForResult(CameraUtil.getAlbumIntent(), CameraUtil.PICK_PHOTO);
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
        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        Api.getRetrofit().uploadFacePic(boy).enqueue(new RequestCallback<HttpResponse<UploadImageResult>>() {
            @Override
            public void onSuccess(HttpResponse<UploadImageResult> response) {
                T.showShort(getApplicationContext(), response.getMessage());
                if (response != null && response.isSuccess()) {
                    App.getInst().getUser().image = response.getDataFrist().fid;
                    modifyUserInfo(response.getDataFrist().fid);
                }
            }

            @Override
            public void onFinish() {
                loadingDialog.dismiss();
            }

        });
        return false;
    }


    private void modifyUserInfo(String fid) {
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        params.put("userId", App.getInst().getUser().id);
        params.put("faceImage", fid);
        Api.getRetrofit().modifyUserInfo(params).enqueue(new RequestCallback<HttpResponse>() {
            @Override
            public void onSuccess(HttpResponse response) {
                if (response.isSuccess()) {
                    mCustomWebView.reload();
                }
            }

            @Override
            public void onFinish() {
                loadingDialog.dismiss();
            }
        });
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
                        String pickPath = CameraUtil.resolvePhotoFromIntent(ProfileSettingActivity.this, data);
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
                case REQUEST_CODE:
                    mCustomWebView.reload();
                    break;
            }
        }
    }

    public static class UploadImageResult {
        public String fid;
    }
}
