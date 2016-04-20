package com.zhiri.bear.ui.base;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.models.User;
import com.zhiri.bear.ui.home.HomeActivity;
import com.zhiri.bear.ui.user.LoginActivity;
import com.zhiri.bear.utils.NetworkUtils;
import com.zhiri.bear.utils.SharePreHelper;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.views.CustomActionBar;
import com.zhiri.bear.views.LoadingDialog;
import com.zhiri.bear.views.WinningDialog;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by MagicBean on 2016/01/06 17:17:21
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener, CustomActionBar.OnActionBarListener {
    private boolean isStartActivity;
    protected LayoutInflater mInflater;
    protected CustomActionBar mActionBar;
    private LoadingDialog mLoadingDialog;
    private boolean mIsBackground;
    private WinningDialog mLuckDialog;
//    protected KProgressHUD mHudDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        ButterKnife.bind(this);
        initializeActionBar();
        initializeViews(savedInstanceState);
        initializeViews();
        initializeData();

    }


    protected abstract void setContentView();

    protected abstract void initializeViews();

    protected abstract void initializeData();

    public abstract void onClick(View v);

    protected void initializeActionBar() {
        mActionBar = (CustomActionBar) findViewById(R.id.actionBar);
        mInflater = getLayoutInflater();
        if (mActionBar != null) {
            mActionBar.setActionBarListener(this);
        }
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        if (toolbar != null) {
//            setSupportActionBar(toolbar);
//        }
    }

    protected void initializeViews(Bundle savedInstanceState) {

    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (mActionBar != null) {
            mActionBar.setTitleText(title);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (!isAppOnForeground()) {
//            Logger.i("enter background");
//            mIsBackground = true;
//        } else {
//            Logger.i("foreground");
//            mIsBackground = false;
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isAppOnForeground()) {
            Logger.i("enter background");
            mIsBackground = true;
            SharePreHelper.getIns().setShouldShowNotification(true);
            SharePreHelper.getIns().setAdTime(System.currentTimeMillis());
        } else {
            Logger.i("foreground");
            SharePreHelper.getIns().setShouldShowNotification(false);
            mIsBackground = false;
        }
    }

    /**
     * 判断是否进入后台
     *
     * @return true 后台 false 前台
     */
    protected boolean isBackground() {
        return mIsBackground;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public CustomActionBar getCustomActionBar() {
        return mActionBar;
    }

    public void onActionBarClick(View view) {

    }

    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

//    protected int getResColor(int color) {
//        return this.getResources().getResColor(color);
//    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        isStartActivity = true;
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        isStartActivity = true;
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    protected void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    protected void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    protected void startActivity(String action) {
        startActivity(action, null);
    }

    protected void startActivity(String action, Bundle bundle) {
        Intent intent = new Intent();
        intent.setAction(action);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();
        if (!isStartActivity) {
            overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent data) {
        super.onActivityResult(arg0, arg1, data);
    }

    public void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
        }
        mLoadingDialog.show();
    }

    public void closeLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    public void showLoading(int status) {
        if (status == 0) {
            showLoadingDialog();
        } else {
            closeLoadingDialog();
        }
    }

    public boolean checkNetWork() {
        boolean available = NetworkUtils.isNetworkAvailable(getApplicationContext());
        if (!available) {
            T.showShort(getApplicationContext(), "请检测网络链接");
            return false;
        }
        return true;
    }


    public void showLuckDialog(String number, String message) {
        if (mLoadingDialog == null) {
            mLuckDialog = new WinningDialog(this);
            mLuckDialog.setCanceledOnTouchOutside(false);
            mLuckDialog.setCancelable(false);
            mLuckDialog.setOwnerActivity(this);
        }
        mLuckDialog.setData(number,message);
//        mLuckDialog.setOwnerActivity();
        mLuckDialog.show();
    }

    public void closeLuckDialog() {
        if (mLuckDialog != null && mLuckDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
//        closeLoadingDialog();
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    public void exit() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("exit", true);
        startActivity(intent);
    }

    public void exit_finish() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("exit_finish", true);
        startActivity(intent);
    }

    public boolean checkLogin() {
        User user = App.getInst().getUser();
        if (user == null) {
            startActivity(LoginActivity.class);
            return false;
        }
        return true;
    }
}
