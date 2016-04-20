package com.zhiri.bear.ui.user;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.models.AppUpdateInfo;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.net.HttpResponse;
import com.zhiri.bear.net.RequestCallback;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.ui.grab.GeneralProblemActivity;
import com.zhiri.bear.ui.home.HomeActivity;
import com.zhiri.bear.update.DownloadService;
import com.zhiri.bear.views.CommonDialog;

import java.io.File;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MagicBean on 2016/02/29 12:12:50
 */
public class SettingActivity extends BaseActivity {
    @Bind(R.id.version_code_txt)
    TextView versionCodeTxt;
    @Bind(R.id.cache_size_txt)
    TextView cacheSizeTxt;
    private CommonDialog commonDialog;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_setting);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setTitleText("个人设置");
        getCustomActionBar().setLeftText("我的");
        getCustomActionBar().setLeftTextVisible(true);
    }

    @Override
    protected void initializeViews() {
        try {
            String version = App.getInst().getPackageManager().getPackageInfo(App.getInst().getPackageName(), 0).versionName;
            versionCodeTxt.setText("v" + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        getCacheSize();
    }

    @Override
    protected void initializeData() {

    }

    @Override
    public void onClick(View v) {

    }

    @OnClick({R.id.profile_setting_item, R.id.common_questions_item, R.id.exit_login, R.id.delete_cache_item,R.id.version_info_item})
    public void itemClick(View v) {
        switch (v.getId()) {
            case R.id.profile_setting_item:
                startActivity(ProfileSettingActivity.class);
                break;
            case R.id.common_questions_item:
                GeneralProblemActivity.toIntent(this, "个人设置", GeneralProblemActivity.SHOW_GENERAL_PROBLEM);
                break;
            case R.id.exit_login:
                if (commonDialog == null) {
                    commonDialog = new CommonDialog(this).setMessageTitle("请确认是否退出?").setListener(new CommonDialog.CommonDialogListener() {
                        @Override
                        public void onLeft(CommonDialog commonDialog) {

                        }

                        @Override
                        public void onRight(CommonDialog commonDialog) {
                            logout();
                        }
                    });
                }
                commonDialog.show();
                break;
            case R.id.version_info_item:
                checkNewVersion();
                break;
            case R.id.delete_cache_item:
                clearCacheSize();
                break;
        }
    }

    /**
     * 检测新版本
     */
    private void checkNewVersion() {
        Api.getRetrofit().checkUpdate(HttpParamsHelper.createParams()).enqueue(new Callback<HttpResponse<AppUpdateInfo>>() {
            @Override
            public void onFailure(Throwable t) {

            }

            @Override
            public void onResponse(final Response<HttpResponse<AppUpdateInfo>> response) {
                if (response != null) {
                    if (response.body() != null) {
                        if (response.body().code == 90001) { //必须更新
                            CommonDialog commonDialog = new CommonDialog(SettingActivity.this).setMessageTitle(response.body().getMessage()).setShowLeft(false).setListener(new CommonDialog.CommonDialogListener() {
                                @Override
                                public void onLeft(CommonDialog commonDialog) {

                                }

                                @Override
                                public void onRight(CommonDialog commonDialog) {
                                    Intent intent = new Intent(SettingActivity.this, DownloadService.class);
                                    intent.putExtra("url",response.body().getDataFrist().url);
                                    startService(intent);
                                }
                            });
                            commonDialog.setCanceledOnTouchOutside(false);
                            commonDialog.show();
                        } else if (response.body().code == 90002) { //有更新
                            new CommonDialog(SettingActivity.this).setMessageTitle(response.body().getMessage()).setListener(new CommonDialog.CommonDialogListener() {
                                @Override
                                public void onLeft(CommonDialog commonDialog) {

                                }

                                @Override
                                public void onRight(CommonDialog commonDialog) {
                                    Intent intent = new Intent(SettingActivity.this, DownloadService.class);
                                    intent.putExtra("url",response.body().getDataFrist().url);
                                    startService(intent);
                                }
                            }).show();
                        }
                    }
                }
            }
        });
    }

    private void logout() {
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        Api.getRetrofit().logout(params).enqueue(new RequestCallback<HttpResponse>(this) {
            @Override
            public void onSuccess(HttpResponse response) {

            }

            @Override
            public void onFinish() {
                UserManager.getIns().getGoodsMap().clear();
                UserManager.getIns().clearToken();
                App.getInst().setUser(null);
                Intent intent = new Intent(SettingActivity.this, HomeActivity.class);
                intent.putExtra("show_position", 0);
                startActivity(intent);
                finish();
            }
        });

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
    protected void onDestroy() {
        if (commonDialog != null) {
            commonDialog.dismiss();
        }
        super.onDestroy();
    }

    public void clearCacheSize() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingDialog();
            }

            @Override
            protected Void doInBackground(Void... params) {
                File file = Glide.getPhotoCacheDir(getApplicationContext());
                deleteFile(file);
                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                closeLoadingDialog();
                getCacheSize();
            }
        }.execute();
    }

    public void getCacheSize() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                File file = Glide.getPhotoCacheDir(getApplicationContext());
                String formatter = null;
                if (file != null) {
                    try {
                        formatter = Formatter.formatFileSize(getApplicationContext(), getFolderSize(file));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return formatter;
            }

            @Override
            protected void onPostExecute(String cacheSize) {
                super.onPostExecute(cacheSize);
                cacheSizeTxt.setText(cacheSize);
            }
        }.execute();
    }


    public void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
    }

    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }
}
