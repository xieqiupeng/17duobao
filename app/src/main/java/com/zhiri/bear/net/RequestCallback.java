package com.zhiri.bear.net;

import android.app.Activity;

import com.orhanobut.logger.Logger;
import com.zhiri.bear.App;
import com.zhiri.bear.update.CheckUpdateUtil;
import com.zhiri.bear.views.LoadingDialog;

import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by MagicBean on 2016/01/12 17:17:15
 */
public abstract class RequestCallback<T> implements Callback<T> {
    private LoadingDialog mShowDialog;
    private WeakReference<Activity> mWeakReference;

    public RequestCallback() {
    }

    public RequestCallback(Activity activity) {
        showLoading(activity, true);
    }

    public RequestCallback(Activity activity, boolean cancel) {
        showLoading(activity, cancel);
    }

    private void showLoading(Activity activity, boolean cancel) {
        if (activity != null) {
            mWeakReference = new WeakReference<>(activity);
            if (mWeakReference.get() != null && !mWeakReference.get().isFinishing()) {
                mShowDialog = new LoadingDialog(mWeakReference.get());
                mShowDialog.setCancelable(cancel);
                mShowDialog.setCanceledOnTouchOutside(cancel);
                mShowDialog.show();
            }
        }
    }

    @Override
    public void onFailure(Throwable t) {
        t.printStackTrace();
        if (t instanceof Exception) {
            if (t instanceof ConnectException) {
                com.zhiri.bear.utils.T.showShort(App.getInst(), "连接服务器失败!");
            } else if (t instanceof SocketTimeoutException) {
                com.zhiri.bear.utils.T.showShort(App.getInst(), "连接服务器超时!");
            }
        }
        Logger.i("http failure:" + t.toString());
        closeDialog();
        onFinish();
    }

    @Override
    public void onResponse(Response<T> response) {
        Logger.i("http code:" + response.code() + "--success:" + response.isSuccess() + "-- message:" + response.message());
        closeDialog();
        if (response.isSuccess()) {
            onSuccess(response.body());
            if (response.body() instanceof HttpResponse) {
                if (((HttpResponse) response.body()).code == 401 || ((HttpResponse) response.body()).code == 403) {//401 token过期 403禁止登录
                    App.getInst().toLogin();
                } else if (((HttpResponse) response.body()).code == 90001 || ((HttpResponse) response.body()).code == 90002) {//90001 必须更新  // 90002 有更新
                    CheckUpdateUtil.checkNewVersion();
                }
            }
        } else {
            onError(response.errorBody());
        }
        onFinish();
    }

    public void onError(ResponseBody responseBody) {
    }

    public abstract void onSuccess(T response);

    public abstract void onFinish();

    private void closeDialog() {
        if (mShowDialog != null) {
            mShowDialog.dismiss();
            mShowDialog = null;
        }
    }
}
