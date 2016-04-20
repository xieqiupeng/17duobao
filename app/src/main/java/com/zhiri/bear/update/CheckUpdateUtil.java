package com.zhiri.bear.update;

import android.app.Activity;
import android.content.Intent;

import com.zhiri.bear.App;
import com.zhiri.bear.models.AppUpdateInfo;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.net.HttpResponse;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.utils.SharePreHelper;
import com.zhiri.bear.views.CommonDialog;

import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MagicBean on 2016/03/31 01:1:19
 */
public class CheckUpdateUtil {


    /**
     * 检测新版本
     */
    public static void checkNewVersion() {
        final Activity activity = App.getInst().getCurrentShowActivity();
        Api.getRetrofit().checkUpdate(HttpParamsHelper.createParams()).enqueue(new Callback<HttpResponse<AppUpdateInfo>>() {
            @Override
            public void onFailure(Throwable t) {

            }

            @Override
            public void onResponse(final Response<HttpResponse<AppUpdateInfo>> response) {
                if (response != null) {
                    if (response.body() != null) {
                        if (response.body().code == 90001) { //必须更新
                            CommonDialog commonDialog = new CommonDialog(activity).setMessageTitle(response.body().getMessage()).setShowLeft(false).setListener(new CommonDialog.CommonDialogListener() {
                                @Override
                                public void onLeft(CommonDialog commonDialog) {

                                }

                                @Override
                                public void onRight(CommonDialog commonDialog) {
                                    Intent intent = new Intent(activity, DownloadService.class);
                                    intent.putExtra("url", response.body().getDataFrist().url);
                                    activity.startService(intent);
                                    ((BaseActivity) activity).exit_finish();
                                }
                            });
                            commonDialog.setCanceledOnTouchOutside(false);
                            commonDialog.setCancelable(false);
                            commonDialog.show();
                        } else if (response.body().code == 90002) { //有更新
                            if (response.body().getDataFrist().appVersion > SharePreHelper.getIns().getCurrentUpdateViewsion()) {
                                new CommonDialog(activity).setMessageTitle(response.body().getMessage()).setLeftBtnText("忽略").setRightBtnText("更新").setListener(new CommonDialog.CommonDialogListener() {
                                    @Override
                                    public void onLeft(CommonDialog commonDialog) {
                                        SharePreHelper.getIns().setCurrentUpdateVersion(response.body().getDataFrist().appVersion);
                                    }

                                    @Override
                                    public void onRight(CommonDialog commonDialog) {
                                        Intent intent = new Intent(activity, DownloadService.class);
                                        intent.putExtra("url", response.body().getDataFrist().url);
                                        activity.startService(intent);
                                    }
                                }).show();
                            }
                        }
                    }
                }
            }
        });
    }
}
