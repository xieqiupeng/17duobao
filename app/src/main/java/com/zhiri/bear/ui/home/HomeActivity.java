package com.zhiri.bear.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;

import com.igexin.sdk.PushManager;
import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.db.SpCarDbHelper;
import com.zhiri.bear.db.models.GoodsModel;
import com.zhiri.bear.models.SystemNotificationEntity;
import com.zhiri.bear.models.User;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.net.HttpResponse;
import com.zhiri.bear.net.RequestCallback;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.ui.base.BaseFragment;
import com.zhiri.bear.ui.user.LoginActivity;
import com.zhiri.bear.ui.user.UserManager;
import com.zhiri.bear.update.CheckUpdateUtil;
import com.zhiri.bear.utils.SharePreHelper;
import com.zhiri.bear.utils.TextUtil;
import com.zhiri.bear.views.CustomTabContainer;
import com.zhiri.bear.views.NotificationDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

public class HomeActivity extends BaseActivity implements CustomTabContainer.OnTabChangeListener {
    @Bind(R.id.tabContainer)
    CustomTabContainer mTabContainer;
    private GrabFragment mHomeGrabFragment;
    private ProfileFragment mHomeProfileFragment;
    private ShoppingCartListFragment mHomeCarListFragment;
    private ShowOrderFragment mHomeShowFragment;
    private NewMessageFragment mHomeNewFragment;
    BaseFragment mCurrentFragment;
    private int showTab;
    boolean isFristShow;
    private boolean mIsBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean exit = getIntent().getBooleanExtra("exit", false);
        super.onCreate(savedInstanceState);
        if (exit) {
            UserManager.getIns().clearToken();
            startActivity(LoginActivity.class);
            finish();
        }
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_home);
    }

    @Override
    protected void initializeViews() {

    }

    @Override
    protected void initializeViews(Bundle savedInstanceState) {
        super.initializeViews(savedInstanceState);

        if (savedInstanceState != null) {
            showTab = savedInstanceState.getInt("showTab", 0);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mHomeGrabFragment = (GrabFragment) getSupportFragmentManager().findFragmentById(R.id.home_grab_fragment);
        mHomeNewFragment = (NewMessageFragment) getSupportFragmentManager().findFragmentById(R.id.home_new_fragment);
        mHomeShowFragment = (ShowOrderFragment) getSupportFragmentManager().findFragmentById(R.id.home_show_fragment);
        mHomeCarListFragment = (ShoppingCartListFragment) getSupportFragmentManager().findFragmentById(R.id.home_car_list_fragment);
        mHomeProfileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.home_profile_fragment);
        transaction.hide(mHomeCarListFragment)
                .hide(mHomeShowFragment)
                .hide(mHomeProfileFragment)
                .hide(mHomeNewFragment)
                .hide(mHomeGrabFragment)
                .commitAllowingStateLoss();
        mCurrentFragment = mHomeGrabFragment;

        mTabContainer.setOnTabChangeListener(this);
        mTabContainer.addTab(new CustomTabContainer.TabEntity(getString(R.string.home_tab_grab_lable), R.drawable.home_bottom_grab_selector))
                .addTab(new CustomTabContainer.TabEntity(getString(R.string.home_tab_announced_lable), R.drawable.home_bottom_new_selector))
                .addTab(new CustomTabContainer.TabEntity(getString(R.string.home_tab_show_lable), R.drawable.home_bottom_show_selector))
                .addTab(new CustomTabContainer.TabEntity(getString(R.string.home_tab_list_lable), R.drawable.home_bottom_carlist_selector, true))
                .addTab(new CustomTabContainer.TabEntity(getString(R.string.home_tab_profile_lable), R.drawable.home_bottom_me_selector))
                .notifyDataChanged();
        mTabContainer.selectTab(showTab);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("showTab", showTab);
    }

    @Override
    protected void initializeData() {
        PushManager.getInstance().initialize(this.getApplicationContext());
        convertData();
        updateCarSize();
        CheckUpdateUtil.checkNewVersion();
        isFristShow = true;
        if (isFristShow) {
            SharePreHelper.getIns().setAdTime(System.currentTimeMillis());
            fetchNotification();
            isFristShow = false;
        }
    }

    private void convertData() {
        User user = App.getInst().getUser();
        if (user != null) {
            HashMap<Integer, GoodsModel> goodsMap = UserManager.getIns().getGoodsMap();
            if (goodsMap != null && goodsMap.size() > 0) {
                ArrayList<GoodsModel> temps = new ArrayList<>();
                GoodsModel goodsModel;
                for (Map.Entry<Integer, GoodsModel> goodsModelEntry : goodsMap.entrySet()) {
                    goodsModel = new GoodsModel();
                    goodsModel.quantity = goodsModelEntry.getValue().quantity;
                    goodsModel.grabId = goodsModelEntry.getValue().grabId;
                    temps.add(goodsModel);
                }
                SpCarDbHelper.getInst().saveAllGoods(temps);
                UserManager.getIns().getGoodsMap().clear();
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onTabChange(int position, CustomTabContainer.TabEntity tab) {
        showTab = position;
        switch (position) {
            case 0://夺宝
                switchContent(mHomeGrabFragment);
                break;
            case 1://最新揭晓
                switchContent(mHomeNewFragment);
                break;
            case 2://晒单
                switchContent(mHomeShowFragment);
                break;
            case 3://清单
                switchContent(mHomeCarListFragment);
                break;
            case 4://我的
                if (App.getInst().getUser() == null) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("fromHome", true);
                    startActivity(LoginActivity.class, bundle);
                }
                switchContent(mHomeProfileFragment);
                break;

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        boolean exit = intent.getBooleanExtra("exit", false);
        boolean finish = intent.getBooleanExtra("exit_finish", false);
        if (finish) {
            finish();
            return;
        }
        if (exit) {
            UserManager.getIns().clearToken();
            startActivity(LoginActivity.class);
            finish();
        } else {
            int show_position = getIntent().getIntExtra("show_position", -1);
            if (show_position != -1)
                mTabContainer.selectTab(show_position);
            boolean refreshUser = getIntent().getBooleanExtra("refreshUserInfo", false);
            if (refreshUser) {
                UserManager.getIns().refreshUserInfo();
            }
            convertData();
            updateCarSize();
//            if (mCurrentFragment != null) {
//                mCurrentFragment.onHiddenChanged(false);
//            }
//            if (mCurrentFragment != null && mCurrentFragment instanceof GrabFragment) {
//                ((GrabFragment) mCurrentFragment).initializeData();
//            }
        }
    }

    public void switchPage(int index) {
        mTabContainer.selectTab(index);
    }

    public void switchContent(BaseFragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();
        if (mCurrentFragment != null) {
            t.hide(mCurrentFragment);
        }
        t.show(fragment);
        t.commitAllowingStateLoss();
        mCurrentFragment = fragment;
    }

    private void fetchNotification() {
        if (App.getInst().getUser() == null) return;
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        params.put("offset", 1);
        params.put("rows", 100);
        params.put("deviceType", "Android");
        Api.getRetrofit().getHomeNotitication(params).enqueue(new RequestCallback<HttpResponse<SystemNotificationEntity.SystemNotification>>() {
            @Override
            public void onSuccess(HttpResponse<SystemNotificationEntity.SystemNotification> response) {
                if (response != null && response.code != 91001) {
                    if (response.code == 0) {
                        SystemNotificationEntity.SystemNotification data = response.getDataFrist();
                        if (data != null && TextUtil.isValidate(data.list)) {
                            new NotificationDialog(HomeActivity.this).setData(data.list).show();
                        }
                    }
                }
            }

            @Override
            public void onFinish() {
                SharePreHelper.getIns().setAdTime(System.currentTimeMillis());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCurrentFragment instanceof NewMessageFragment || mCurrentFragment instanceof ShowOrderFragment) {
            mCurrentFragment.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCurrentFragment != null) {
            mCurrentFragment.setUserVisibleHint(true);
            if (mCurrentFragment instanceof NewMessageFragment || mCurrentFragment instanceof ShowOrderFragment) {
                mCurrentFragment.onPause();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        convertData();
        updateCarSize();
        if (mCurrentFragment != null) {
            if (mCurrentFragment instanceof NewMessageFragment || mCurrentFragment instanceof ShowOrderFragment) {
                mCurrentFragment.onResume();
            }
            if (mCurrentFragment instanceof ProfileFragment) {
                ((ProfileFragment) mCurrentFragment).updateUser();
            }
        }
        if (SharePreHelper.getIns().shouldShowNotification()) {
            SharePreHelper.getIns().setShouldShowNotification(false);
            if (!isFristShow) {
                long adTime = SharePreHelper.getIns().getAdTime();
                if (System.currentTimeMillis() - adTime >= SharePreHelper.getIns().getAdInterval() * 1000) {
                    fetchNotification();
                }
            }
        }
    }

    public void updateCarSize() {
        if (App.getInst().getUser() != null) {
            ArrayList<GoodsModel> temps = SpCarDbHelper.getInst().getAllGoods();
            if (temps != null) {
                mTabContainer.setTabNewMsgCount(3, temps.size());
            } else {
                mTabContainer.setTabNewMsgCount(3, 0);
            }
        } else {
            mTabContainer.setTabNewMsgCount(3, UserManager.getIns().getGoodsMap().size());
        }
    }


//    private long firstTime = 0;
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_BACK:
//                long secondTime = System.currentTimeMillis();
//                if (secondTime - firstTime > 2000) {
//                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//                    firstTime = secondTime;
//                    return true;
//                } else {
//                    finish();
//                }
//                break;
//        }
//        return false;
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(false);
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }
}
