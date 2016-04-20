package com.zhiri.bear.ui.home;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.orhanobut.logger.Logger;
import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.event.UserEvent;
import com.zhiri.bear.models.ProfileViewItem;
import com.zhiri.bear.models.User;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.net.HttpResponse;
import com.zhiri.bear.net.RequestCallback;
import com.zhiri.bear.ui.adapter.OnRecyclerViewItemClickListener;
import com.zhiri.bear.ui.adapter.UserProfileAdapter;
import com.zhiri.bear.ui.base.BaseFragment;
import com.zhiri.bear.ui.base.ViewHolder;
import com.zhiri.bear.ui.pay.RechargeActivity;
import com.zhiri.bear.ui.user.CommonOrderWebViewActivity;
import com.zhiri.bear.ui.user.LoginActivity;
import com.zhiri.bear.ui.user.MyShowOrderActivity;
import com.zhiri.bear.ui.user.ProfileSettingActivity;
import com.zhiri.bear.ui.user.RechargeRecordActivity;
import com.zhiri.bear.ui.user.SettingActivity;
import com.zhiri.bear.ui.user.SystemNotificationActivity;
import com.zhiri.bear.ui.user.UserManager;
import com.zhiri.bear.ui.user.WinRecordWebViewActivity;
import com.zhiri.bear.utils.ImageLoader;
import com.zhiri.bear.utils.TextUtil;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import retrofit2.Call;


/**
 * Created by MagicBean on 2016/02/23 15:15:48
 */
public class ProfileFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    @Bind(R.id.mRecyclerview)
    XRecyclerView mRecyclerView;
    ImageView userAvatar;
    TextView userBalance;
    TextView userName;
    private ArrayList<ProfileViewItem> datas = new ArrayList<>();
    private UserProfileAdapter mAdapter;
    private Call<HttpResponse<User>> mRefreshCall;

    public static ProfileFragment newInstance() {
        Bundle args = new Bundle();
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getRootViewLayoutId() {
        return R.layout.fragment_profile_layout;
    }

    @Override
    protected void initializeViews() {
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setArrowImageView(R.mipmap.iconfont_downgrey);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLoadingMoreEnabled(false);
        View header = LayoutInflater.from(getContext()).inflate(R.layout.profile_list_header, null);
        mRecyclerView.addHeaderView(header);
        mRecyclerView.setLoadingListener(this);

        userAvatar = ViewHolder.get(header, R.id.user_avatar);
        userBalance = ViewHolder.get(header, R.id.balance_tv);
        userName = ViewHolder.get(header, R.id.user_name);

        userAvatar.setOnClickListener(this);
        ViewHolder.get(header, R.id.recharge_btn).setOnClickListener(this);
        ViewHolder.get(header, R.id.all_order_btn).setOnClickListener(this);
        ViewHolder.get(header, R.id.waiting_open_btn).setOnClickListener(this);
        ViewHolder.get(header, R.id.finish_btn).setOnClickListener(this);

    }

    @Override
    protected void initializeData() {
        datas.clear();
        datas.add(new ProfileViewItem(R.mipmap.ic_prize_record, "中奖记录"));
        datas.add(new ProfileViewItem(false, R.mipmap.ic_show, "我的晒单"));
        datas.add(new ProfileViewItem(R.mipmap.icon_depositmoney, "充值记录", true));
        datas.add(new ProfileViewItem(R.mipmap.ic_setting, "个人设置"));
        datas.add(new ProfileViewItem(false, R.mipmap.ic_notification, "系统通知"));
        mAdapter = new UserProfileAdapter(datas);
        mRecyclerView.setAdapter(mAdapter);
        updateUser();
        mAdapter.setOnItemClickLitener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!checkLogin()) return;
                ProfileViewItem item = datas.get(position);
                switch (item.itemIconRes) {
                    case R.mipmap.ic_prize_record:
                        startActivity(WinRecordWebViewActivity.class,
                                WinRecordWebViewActivity.putIntentExtra("中奖记录"));
                        break;
                    case R.mipmap.ic_show:
                        startActivity(MyShowOrderActivity.class);
                        break;
                    case R.mipmap.icon_depositmoney:
                        startActivity(RechargeRecordActivity.class);
                        break;
                    case R.mipmap.ic_setting:
                        startActivity(SettingActivity.class);
                        break;
                    case R.mipmap.ic_notification:
                        startActivity(SystemNotificationActivity.class);
                        break;

                }
            }
        });
    }

    public void updateUser() {
        User user = App.getInst().getUser();
        if (user != null) {
            userName.setText(user.name);
            if (TextUtil.isValidate(user.image) && user.image.startsWith("http")) {
                ImageLoader.loadCicleImage(this, user.image, R.mipmap.default_header, userAvatar);
            } else {
                ImageLoader.loadCicleImage(this, Api.getImageUrl(user.image), R.mipmap.default_header, userAvatar);
            }
            userBalance.setText(user.balance + "");
        }
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefresh() {
        refreshUser();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {

        } else {
            updateUser();
            refreshUser();
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void refreshUI(UserEvent event) {
        initializeData();
    }

    public void refreshUser() {
        if (checkLogin()) {
            if (App.getInst().getUser() != null && !TextUtil.isValidate(App.getInst().getUser().token)) {
                return;
            }
            HashMap<String, Object> params = HttpParamsHelper.createParams();
            params.put("token", App.getInst().getUser().token);
            mRefreshCall = Api.getRetrofit().refreshUserInfo(params);
            mRefreshCall.enqueue(new RequestCallback<HttpResponse<User>>() {
                @Override
                public void onSuccess(HttpResponse<User> response) {
                    Logger.i("refreshUserInfo msg:" + response.toString());
                    if (response.isSuccess() && !mRefreshCall.isCanceled()) {
                        User user = response.getDataFrist();
                        if (user != null) {
                            App.getInst().setUser(user);
                            UserManager.getIns().saveUserInfo(user);
                            initializeData();
                        }
                    } else if (response.code == 401 || response.code == 403) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("fromHome", true);
                        startActivity(LoginActivity.class, bundle);
                    }
                }

                @Override
                public void onFinish() {
                    if (mRecyclerView != null) {
                        mRecyclerView.refreshComplete();
                    }
                }
            });
        } else {
            mRecyclerView.refreshComplete();
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        if (mRefreshCall != null) {
            mRefreshCall.cancel();
            Logger.i("mRefreshCall: cancel");
        }
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.user_avatar:
                if (checkLogin())
                    startActivity(ProfileSettingActivity.class);
                break;
            case R.id.recharge_btn:
                if (checkLogin())
                    startActivity(RechargeActivity.class);
                break;
            case R.id.all_order_btn:
                if (checkLogin())
                    startActivity(CommonOrderWebViewActivity.class,
                            CommonOrderWebViewActivity.putIntentExtra(CommonOrderWebViewActivity.ALL_ORDER));
                break;
            case R.id.waiting_open_btn:
                if (checkLogin())
                    startActivity(CommonOrderWebViewActivity.class,
                            CommonOrderWebViewActivity.putIntentExtra(CommonOrderWebViewActivity.DOING_ORDER));
                break;
            case R.id.finish_btn:
                if (checkLogin())
                    startActivity(CommonOrderWebViewActivity.class,
                            CommonOrderWebViewActivity.putIntentExtra(CommonOrderWebViewActivity.FINISH_ORDER));
                break;
        }
    }
}
