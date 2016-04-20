package com.zhiri.bear.ui.home;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.orhanobut.logger.Logger;
import com.zhiri.bear.R;
import com.zhiri.bear.models.AtestAnnounce;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.net.HttpResponse;
import com.zhiri.bear.net.RequestCallback;
import com.zhiri.bear.ui.adapter.AtestAnnounceAdapter;
import com.zhiri.bear.ui.base.BaseFragment;
import com.zhiri.bear.utils.TextUtil;
import com.zhiri.bear.views.DividerGridItemDecoration2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.iwgang.countdownview.CountdownView;
import cn.iwgang.countdownview.CustomCountDownTimer;

/**
 * Created by MagicBean on 2016/02/23 15:15:48
 */
public class AtestAnnounceFragment extends BaseFragment {

    @Bind(R.id.mRecyclerview)
    XRecyclerView mRecyclerview;
    private AtestAnnounceAdapter mAdapter;
    private Map<Integer, MyCustomCountDownTimer> mCustomCountDownTimers = new HashMap<>();
    private List<AtestAnnounce> datas = new ArrayList<>();

    public static AtestAnnounceFragment newInstance() {
        Bundle args = new Bundle();
        AtestAnnounceFragment fragment = new AtestAnnounceFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getRootViewLayoutId() {
        return R.layout.fragment_atest_announce_layout;
    }

    @Override
    protected void initializeViews() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerview.setLayoutManager(layoutManager);
        mRecyclerview.addItemDecoration(new DividerGridItemDecoration2(getContext()));
        mRecyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerview.setLaodingMoreProgressStyle(ProgressStyle.Pacman);
        mRecyclerview.setArrowImageView(R.mipmap.iconfont_downgrey);
        mRecyclerview.setHasFixedSize(true);
    }

    @Override
    protected void initializeData() {
        loadData();
//        mAdapter = new AtestAnnounceAdapter(datas, mCustomCountDownTimers);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    private void loadData() {
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        params.put("size", 10);
        params.put("offset", 1);
        Api.getRetrofit().getAtestAnnounce(params).enqueue(new RequestCallback<HttpResponse<AtestAnnounce.AtestAnnounceEntity>>() {
            @Override
            public void onSuccess(HttpResponse<AtestAnnounce.AtestAnnounceEntity> response) {
                if (response != null && TextUtil.isValidate(response.data) && TextUtil.isValidate(response.getDataFrist().list)) {
                    ArrayList<AtestAnnounce> temp = response.getDataFrist().list;
                    int size = temp.size();
                    for (int i = 0; i < size; i++) {
                        AtestAnnounce entity = temp.get(i);
                        long countdown = entity.openTime.time - entity.systemTime.time;
                        if (countdown > 0) {
                            entity.setCountdown(countdown);
                            entity.setCountId(entity.id);
                            MyCustomCountDownTimer myCustomCountDownTimer = new MyCustomCountDownTimer(entity.getCountdown(), 50, entity, mRecyclerview); // 第二个参数是间隔时间, 如果倒计时只到秒级别的, 请填1000
                            myCustomCountDownTimer.start();
                            mCustomCountDownTimers.put(entity.getCountId(), myCustomCountDownTimer);
                        }
                    }
                    datas.clear();
                    datas.addAll(temp);
                    mRecyclerview.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFinish() {

            }
        });
    }

    public static class MyCustomCountDownTimer extends CustomCountDownTimer {
        private Map<Integer, MyCustomCountDownTimer> mCustomCountDownTimers;
        private AtestAnnounceAdapter mAdapter;
        private XRecyclerView mRecyclerview;
        private CountdownView mCountdownView;
        private AtestAnnounce mItemInfo;

        public MyCustomCountDownTimer(long millisInFuture, long countDownInterval, AtestAnnounce itemInfo, XRecyclerView mRecyclerview) {
            super(millisInFuture, countDownInterval);
            this.mItemInfo = itemInfo;
            this.mRecyclerview = mRecyclerview;
        }

        public void refView(CountdownView countdownView, AtestAnnounceAdapter mAdapter, Map<Integer, MyCustomCountDownTimer> mCustomCountDownTimers) {
            this.mCountdownView = countdownView;
            this.mCustomCountDownTimers = mCustomCountDownTimers;
            this.mAdapter = mAdapter;
        }

        @Override
        public void onTick(long l) {
            if (l < 0) l = 0;
            mItemInfo.setCountdown(l);
            // 这里需要注意会有很多情况下出现两个线程去刷新同一个CountdownView，所以使用tag中获取显示的item和当前的对象对比是否是同一个
            if (null != mCountdownView && Long.valueOf(mCountdownView.getTag().toString()) == mItemInfo.getCountId()) {
                mCountdownView.updateShow(l);
            }
        }

        @Override
        public void onFinish() {
            mItemInfo.setCountdown(0);
            if (null != mCountdownView && Long.valueOf(mCountdownView.getTag().toString()) == mItemInfo.getCountId()) {
                mCountdownView.updateShow(0);
                refreshLotteryResult(mItemInfo, Integer.parseInt(mCountdownView.getTag(R.id.tag_id).toString()));
            }
            if (mCustomCountDownTimers != null)
                mCustomCountDownTimers.remove(mItemInfo.getCountId());
        }

        private synchronized void refreshLotteryResult(final AtestAnnounce mItemInfo, final int position) {
            HashMap<String, Object> params = HttpParamsHelper.createParams();
            params.put("ids", mItemInfo.id);
            Api.getRetrofit().getLotteryResult(params).enqueue(new RequestCallback<HttpResponse<AtestAnnounce>>() {
                @Override
                public void onSuccess(HttpResponse<AtestAnnounce> response) {
                    if (response != null && response.isSuccess()) {
                        AtestAnnounce temp = response.getDataFrist();
                        Logger.i("temp:" + temp);
                        mItemInfo.appUserInfoDto = temp.appUserInfoDto;
                        mItemInfo.status = temp.status;
                        mItemInfo.luckyNo = temp.luckyNo;
                    }
                }

                @Override
                public void onFinish() {
                    if (mRecyclerview != null) {
                        mRecyclerview.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyItemChanged(position);
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        // 处理线程可能占用的资源不能回收问题
        if (null != mCustomCountDownTimers && mCustomCountDownTimers.size() > 0) {
            for (Map.Entry<Integer, MyCustomCountDownTimer> ccd : mCustomCountDownTimers.entrySet()) {
                ccd.getValue().stop();
                ccd.setValue(null);
            }
            mCustomCountDownTimers.clear();
            mCustomCountDownTimers = null;
        }
    }
}
