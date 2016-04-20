package com.zhiri.bear.ui.grab;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.orhanobut.logger.Logger;
import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.db.SpCarDbHelper;
import com.zhiri.bear.db.models.GoodsModel;
import com.zhiri.bear.models.GoodsTypeDetails;
import com.zhiri.bear.models.GoodsTypeListEntity;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.net.HttpResponse;
import com.zhiri.bear.net.RequestCallback;
import com.zhiri.bear.ui.adapter.GoodsCategoryListAdapter;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.ui.user.UserManager;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.utils.TextUtil;
import com.zhiri.bear.views.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Created by MagicBean on 2016/02/29 15:15:38
 */
public class GoodsCategoryListActivity extends BaseActivity {
    private static final int PAGE_SIZE = 10;
    private int offset = 1;
    @Bind(R.id.mRecyclerview)
    XRecyclerView mRecyclerView;
    private GoodsCategoryListAdapter mAdapter;
    private String goodsTypeName;
    private int goodsTypeId;
    @Bind(R.id.actionbar_title)
    TextView actionTitle;
    @Bind(R.id.actionbar_left_btn)
    TextView actionbar_left_btn;
    private ArrayList<GoodsTypeDetails> goodsTypeDatas = new ArrayList<>();
    private Call<HttpResponse<GoodsTypeListEntity>> refreshCall;
    private Call<HttpResponse<GoodsTypeListEntity>> loadMoreCall;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_category_details_list);
    }

    @Override
    protected void initializeViews() {
        actionbar_left_btn.setText("夺宝");
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
                loadMoreData();
            }
        });

        mAdapter = new GoodsCategoryListAdapter(goodsTypeDatas);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initializeData() {
        goodsTypeName = getIntent().getStringExtra("goodsTypeName");
        goodsTypeId = getIntent().getIntExtra("goodsTypeId", 0);
        actionTitle.setText(goodsTypeName + "(" + 0 + ")");
        loadData();
    }

    private void loadData() {
        offset = 1;
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        params.put("goodsTypeId", goodsTypeId);
        params.put("size", PAGE_SIZE);
        params.put("offset", offset);
        refreshCall = Api.getRetrofit().getByGoodsType(params);
        refreshCall.enqueue(new RequestCallback<HttpResponse<GoodsTypeListEntity>>() {
            @Override
            public void onSuccess(HttpResponse<GoodsTypeListEntity> response) {
                Logger.i("msg:" + response.toString());
                if (response.isSuccess() && !refreshCall.isCanceled()) {
                    GoodsTypeListEntity temp = response.getDataFrist();
                    if (temp != null) {
                        if (temp.page != null) {
                            if (actionTitle != null) {
                                actionTitle.setText(goodsTypeName + "(" + temp.page.size + ")");
                            }
                        }
                        goodsTypeDatas.clear();
                        if (TextUtil.isValidate(temp.list)) {
                            goodsTypeDatas.addAll(temp.list);
                            offset++;
                        }
                        mAdapter.notifyDataSetChanged();
                    }
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

    protected void loadMoreData() {
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        params.put("goodsTypeId", goodsTypeId);
        params.put("size", PAGE_SIZE);
        params.put("offset", offset);
        loadMoreCall = Api.getRetrofit().getByGoodsType(params);
        loadMoreCall.enqueue(new RequestCallback<HttpResponse<GoodsTypeListEntity>>() {
            @Override
            public void onSuccess(HttpResponse<GoodsTypeListEntity> response) {
                Logger.i("msg:" + response.toString());
                if (response.isSuccess()) {
                    GoodsTypeListEntity temp = response.getDataFrist();
                    if (temp != null && TextUtil.isValidate(temp.list)) {
                        goodsTypeDatas.addAll(temp.list);
                        mAdapter.notifyDataSetChanged();
                        offset++;
                    } else {
                        mRecyclerView.noMoreLoading();
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
    public void onClick(View v) {

    }

    @OnClick({R.id.actionbar_left_btn, R.id.action_right_btn})
    public void onActionClick(View view) {
        switch (view.getId()) {
            case R.id.actionbar_left_btn:
                onBackPressed();
                break;
            case R.id.action_right_btn:
                addAll();
                break;
        }
    }

    private void addAll() {
        if (!TextUtil.isValidate(goodsTypeDatas)) return;
        for (GoodsTypeDetails goodsTypeData : goodsTypeDatas) {
            GoodsModel goodsModel = new GoodsModel();
            goodsModel.grabId = goodsTypeData.id;
            goodsModel.quantity = goodsTypeData.defaultUnit;
            if (App.getInst().getUser() == null) {
                UserManager.getIns().addGoodsModel(goodsModel);
            } else {
                SpCarDbHelper.getInst().saveGoods(goodsModel);
            }
        }
        T.showShort(getApplicationContext(), "添加成功");
    }


    @Override
    protected void onDestroy() {
        if (refreshCall != null) {
            refreshCall.cancel();
            Logger.i("onDestroy:cancel");
        }
        if (loadMoreCall != null) {
            loadMoreCall.cancel();
            Logger.i("loadMoreCall:cancel");
        }
        super.onDestroy();
    }
}
