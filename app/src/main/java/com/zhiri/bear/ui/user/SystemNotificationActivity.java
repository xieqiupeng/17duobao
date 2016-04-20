package com.zhiri.bear.ui.user;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zhiri.bear.R;
import com.zhiri.bear.models.SystemNotificationEntity;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.net.HttpResponse;
import com.zhiri.bear.net.RequestCallback;
import com.zhiri.bear.ui.adapter.OnRecyclerViewItemClickListener;
import com.zhiri.bear.ui.adapter.SystemNotificationListAdapter;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.utils.TextUtil;
import com.zhiri.bear.views.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/02/29 21:21:18
 */
public class SystemNotificationActivity extends BaseActivity {
    @Bind(R.id.mRecyclerview)
    XRecyclerView mRecyclerView;
    @Bind(R.id.empty_txt_msg)
    TextView mEmptyTextView;
    private SystemNotificationListAdapter mAdapter;
    private ArrayList<SystemNotificationEntity> datas = new ArrayList<>();
    private static final int PAGE_SIZE = 20;
    private int offset = 1;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_notification);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setTitleText("通知");
        getCustomActionBar().setLeftText("我的");
        getCustomActionBar().setLeftTextVisible(true);
    }

    @Override
    protected void initializeViews() {
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLaodingMoreProgressStyle(ProgressStyle.Pacman);
        mRecyclerView.setArrowImageView(R.mipmap.iconfont_downgrey);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                if (!checkNetWork()) {
                    mRecyclerView.refreshComplete();
                    return;
                }
                loadData();
            }

            @Override
            public void onLoadMore() {
                if (!checkNetWork()) {
                    mRecyclerView.loadMoreComplete();
                    return;
                }
                loadMore();
            }
        });

        mAdapter = new SystemNotificationListAdapter(datas);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickLitener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SystemNotificationEntity systemNotificationEntity = mAdapter.datas.get(position);
                Intent intent = new Intent(SystemNotificationActivity.this, SystemNotificationDetailsActivity.class);
                intent.putExtra("notification", systemNotificationEntity);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initializeData() {
        loadData();
    }

    @Override
    public void onClick(View v) {

    }

    private void loadData() {
        offset = 1;
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        params.put("offset", offset);
        params.put("rows", PAGE_SIZE);
        params.put("deviceType", "Android");
        Api.getRetrofit().getHomeNotitication(params).enqueue(new RequestCallback<HttpResponse<SystemNotificationEntity.SystemNotification>>(this) {
            @Override
            public void onSuccess(HttpResponse<SystemNotificationEntity.SystemNotification> response) {
                if (response != null) {
                    datas.clear();
                    if (response.getDataFrist() != null && TextUtil.isValidate(response.getDataFrist().list)) {
                        datas.addAll(response.getDataFrist().list);
                        offset++;
                    }
                    else {
                        mRecyclerView.setVisibility(View.GONE);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFinish() {
                if (mRecyclerView != null) {
                    mRecyclerView.refreshComplete();
                }
            }
        });
    }

    private void loadMore() {
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        params.put("offset", offset);
        params.put("rows", PAGE_SIZE);
        params.put("deviceType", "Android");
        Api.getRetrofit().getNotfication(params).enqueue(new RequestCallback<HttpResponse<SystemNotificationEntity.SystemNotification>>() {
            @Override
            public void onSuccess(HttpResponse<SystemNotificationEntity.SystemNotification> response) {
                if (response != null) {
                    if (TextUtil.isValidate(response.getDataFrist().list)) {
                        datas.addAll(response.getDataFrist().list);
                        offset++;
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFinish() {
                if (mRecyclerView != null) {
                    mRecyclerView.loadMoreComplete();
                }
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
}
