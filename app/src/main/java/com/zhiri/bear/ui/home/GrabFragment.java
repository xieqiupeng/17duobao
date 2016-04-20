package com.zhiri.bear.ui.home;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.orhanobut.logger.Logger;
import com.zhiri.bear.R;
import com.zhiri.bear.models.Banner;
import com.zhiri.bear.models.GoodsType;
import com.zhiri.bear.models.GrabEntity;
import com.zhiri.bear.models.LuckUser;
import com.zhiri.bear.models.RobGoods;
import com.zhiri.bear.models.RobGoodsList;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.net.HttpResponse;
import com.zhiri.bear.net.RequestCallback;
import com.zhiri.bear.ui.adapter.GrabAdapter;
import com.zhiri.bear.ui.adapter.GrabBannerAdapter;
import com.zhiri.bear.ui.adapter.GrabGoodsCategoryAdapter;
import com.zhiri.bear.ui.adapter.GrabVerticalBannerViewAdapter;
import com.zhiri.bear.ui.adapter.OnRecyclerViewItemClickListener;
import com.zhiri.bear.ui.base.BaseFragment;
import com.zhiri.bear.ui.base.ViewHolder;
import com.zhiri.bear.ui.grab.AllCategoryActivity;
import com.zhiri.bear.ui.grab.GeneralProblemActivity;
import com.zhiri.bear.ui.grab.GoodsCategoryListActivity;
import com.zhiri.bear.ui.search.SearchActivity;
import com.zhiri.bear.utils.TextUtil;
import com.zhiri.bear.views.CustomTextView;
import com.zhiri.bear.views.DividerGridItemDecoration;
import com.zhiri.bear.views.VerticalBannerView;
import com.zhiri.bear.widgets.rollviewpager.RollPagerView;
import com.zhiri.bear.widgets.rollviewpager.hintview.ColorPointHintView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by MagicBean on 2016/02/23 15:15:48
 */
public class GrabFragment extends BaseFragment {

    private static final int PAGE_SIZE = 10;
    @Bind(R.id.mRecyclerview)
    XRecyclerView mRecyclerView;
    private GrabAdapter mAdapter;
    private ArrayList<RobGoodsList> goodsData = new ArrayList<>();
    private RecyclerView cagegoryRecyclerView;
    private CustomTextView mHotItem, mNewItem, mTenItem, mAllItem;
    private RollPagerView mBannerPager;
    private VerticalBannerView bannerView;
    private int offset = 1;
    public static final String NEWEST = "NEWEST";//最新
    public static final String POPULARITY = "POPULARITY";//人气
    public static final String TOTAL = "TOTAL";//总需
    public static final String TOTALDESC = "TOTALDESC";//总需倒序
    private String current_sort = POPULARITY;
    private String mUnit = "";
    private Call<HttpResponse<GrabEntity>> mRefreshCall;
    private Call<HttpResponse<RobGoods>> mLoadMoreCall;
    private ArrayList<GoodsType> mAllGoodsCategory;
    private ArrayList<LuckUser> mLuckUsers;

    public static GrabFragment newInstance() {
        Bundle args = new Bundle();
        GrabFragment fragment = new GrabFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getRootViewLayoutId() {
        return R.layout.fragment_grab_layout;
    }

    @Override
    protected void initializeViews() {
        findViewById(R.id.common_questions_btn).setOnClickListener(this);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        View header = LayoutInflater.from(getContext()).inflate(R.layout.grab_recyclerview_header, null);
        mRecyclerView.addHeaderView(header);
        initializeBannerView(header);
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(getContext()));
        mRecyclerView.setLayoutManager(layoutManager);
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
                bannerView.stop();
                initializeData();
            }

            @Override
            public void onLoadMore() {
                if (!checkNetWork()) {
                    mRecyclerView.loadMoreComplete();
                    return;
                }
                loadMoreData(false);
            }
        });

        mAdapter = new GrabAdapter(goodsData);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initializeBannerView(View header) {
        mHotItem = ViewHolder.get(header, R.id.hot_item);
        mNewItem = ViewHolder.get(header, R.id.new_item);
        mTenItem = ViewHolder.get(header, R.id.ten_item);
        mAllItem = ViewHolder.get(header, R.id.all_item);
        mHotItem.setOnClickListener(this);
        mNewItem.setOnClickListener(this);
        mTenItem.setOnClickListener(this);
        mAllItem.setOnClickListener(this);

        mHotItem.setCheck(true);
        cagegoryRecyclerView = ViewHolder.get(header, R.id.goods_category_recyclerview);
        cagegoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        mBannerPager = ViewHolder.get(header, R.id.grab_banner_pager);
        mBannerPager.setHintView(new ColorPointHintView(getContext(), getResColor(R.color.colorRed), getResColor(R.color.colorWhite)));
        bannerView = ViewHolder.get(header, R.id.vertical_banner_view);
    }

    @Override
    protected void initializeData() {
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        //TODO
//        Log.e("channel", params.toString());

        mRefreshCall = Api.getRetrofit().index(params);
        mRefreshCall.enqueue(new RequestCallback<HttpResponse<GrabEntity>>(getActivity()) {
            @Override
            public void onResponse(Response<HttpResponse<GrabEntity>> response) {
                super.onResponse(response);
//                Log.e("index:", response.toString());
            }

            @Override
            public void onSuccess(HttpResponse<GrabEntity> response) {
                Logger.i("msg:" + response.toString());
                if (response.isSuccess()) {
                    GrabEntity temp = response.getDataFrist();
                    if (temp != null) {
                        initializeBanner(temp.banner);
                        initializeCategory(temp.goosType);
                        initializeLuckUser(temp.luckUsers);
                        initializeGoods(temp.robGoods);
                    }
                }
            }

            @Override
            public void onFinish() {
                if (mRecyclerView != null) {
                    mRecyclerView.refreshComplete();
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (bannerView.hasAdapter()) {
                                bannerView.start();
                            }
                        }
                    });
                    fixLoadMore();
                }
                setCheckFristTab();
            }
        });
    }

    GrabVerticalBannerViewAdapter verticalBannerViewAdapter;

    private void initializeLuckUser(ArrayList<LuckUser> luckUsers) {
        if (TextUtil.isValidate(luckUsers)) {
            if (verticalBannerViewAdapter == null) {
                mLuckUsers = new ArrayList<>();
                mLuckUsers.addAll(luckUsers);
                verticalBannerViewAdapter = new GrabVerticalBannerViewAdapter(mLuckUsers);
                bannerView.setAdapter(verticalBannerViewAdapter);
            } else {
                mLuckUsers.clear();
                mLuckUsers.addAll(luckUsers);
                bannerView.onChanged();
            }
        }
    }

    private void initializeGoods(RobGoods robGoods) {
        goodsData.clear();
        if (robGoods != null) {
            if (TextUtil.isValidate(robGoods.list)) {
                goodsData.addAll(robGoods.list);
                offset = 2;
            }
        }
        mAdapter.notifyDataSetChanged();
        fixLoadMore();
    }

    private void initializeCategory(final ArrayList<GoodsType> goosType) {
        if (TextUtil.isValidate(goosType)) {
            this.mAllGoodsCategory = goosType;
            ArrayList<GoodsType> temp = new ArrayList<>();
            if (goosType.size() > 3) {
                temp.addAll(goosType.subList(0, 3));
            } else {
                temp.addAll(goosType);
            }
            GoodsType goodsCategory = new GoodsType();
            goodsCategory.name = "全部分类";
            temp.add(0, goodsCategory);
            final GrabGoodsCategoryAdapter categoryAdapter = new GrabGoodsCategoryAdapter(temp);
            if (cagegoryRecyclerView != null) {
                cagegoryRecyclerView.setAdapter(categoryAdapter);
            }
            categoryAdapter.setOnItemClickLitener(new OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (position == 0) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("allCategory", mAllGoodsCategory);
                        startActivity(AllCategoryActivity.class, bundle);
                    } else {
                        GoodsType goosType1 = categoryAdapter.datas.get(position);
                        Bundle bundle = new Bundle();
                        bundle.putString("goodsTypeName", goosType1.name);
                        bundle.putInt("goodsTypeId", goosType1.id);
                        startActivity(GoodsCategoryListActivity.class, bundle);
                    }
                }
            });
        }
    }

    private void initializeBanner(ArrayList<Banner> banner) {
        if (TextUtil.isValidate(banner)) {
            GrabBannerAdapter bannerAdapter = new GrabBannerAdapter(banner);
            mBannerPager.setAdapter(bannerAdapter);
        }
    }

    protected void loadMoreData(final boolean isShow) {
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        params.put("sort", current_sort);
        params.put("size", PAGE_SIZE);
        params.put("offset", offset);
        params.put("unit", mUnit);
        mLoadMoreCall = Api.getRetrofit().robgoods(params);
        mLoadMoreCall.enqueue(new RequestCallback<HttpResponse<RobGoods>>(isShow ? getActivity() : null) {
            @Override
            public void onSuccess(HttpResponse<RobGoods> response) {
                Logger.i("msg:" + response.toString());
                if (response.isSuccess()) {
                    if (offset == 1) {
                        fixLoadMore();
                    }
                    RobGoods temp = response.getDataFrist();
                    if (temp != null && TextUtil.isValidate(temp.list)) {
                        goodsData.addAll(temp.list);
                        offset++;
                    } else {
//                        if (mRecyclerView != null) {
//                            mRecyclerView.noMoreLoading();
//                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFinish() {
                if (mRecyclerView != null) {
                    mRecyclerView.loadMoreComplete();
                    if (isShow) {
                        mRecyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (bannerView.hasAdapter()) {
                                    bannerView.start();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 修复切换不能加载更多
     */
    private void fixLoadMore() {
        if (mRecyclerView == null) return;
        try {
            Field field = mRecyclerView.getClass().getDeclaredField("isnomore");
            field.setAccessible(true);
            field.set(mRecyclerView, false);
            Field field1 = mRecyclerView.getClass().getDeclaredField("previousTotal");
            field1.setAccessible(true);
            field1.set(mRecyclerView, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (bannerView != null) {
                bannerView.stop();
            }
            mBannerPager.stopPlay();
        } else {
            initializeData();
//            mBannerPager.startPlay();
        }
    }

    private boolean isCheck;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_questions_btn:
                GeneralProblemActivity.toIntent(getActivity(), "夺宝", GeneralProblemActivity.SHOW_GENERAL_PROBLEM);
//                startActivity(TestActivity.class);
                break;
            case R.id.hot_item:
                setCheckFristTab();
                mUnit = "";
                offset = 1;
                current_sort = POPULARITY;
                reloadMoreData();
                break;
            case R.id.new_item:
                mNewItem.setCheck(true);
                mTenItem.setCheck(false);
                mAllItem.setCheck(false);
                mHotItem.setCheck(false);
                current_sort = NEWEST;
                mUnit = "";
                offset = 1;
                reloadMoreData();
                break;
            case R.id.ten_item:
                mTenItem.setCheck(true);
                mNewItem.setCheck(false);
                mAllItem.setCheck(false);
                mHotItem.setCheck(false);
                current_sort = TOTAL;
                mUnit = "10";
                offset = 1;
                reloadMoreData();
                break;
            case R.id.all_item:
                mAllItem.setCheck(true);
                isCheck = !isCheck;
                if (isCheck) {
                    current_sort = TOTALDESC;
                } else {
                    current_sort = TOTAL;
                }
                mAllItem.setCompoundDrawablesWithIntrinsicBounds(0, 0, isCheck ? R.mipmap.icon_switch_desend : R.mipmap.icon_switch_asend, 0);
                mTenItem.setCheck(false);
                mNewItem.setCheck(false);
                mHotItem.setCheck(false);
                mUnit = "";
                offset = 1;
                reloadMoreData();
                break;
        }
    }

    private void reloadMoreData() {
        goodsData.clear();
        mAdapter.notifyDataSetChanged();
        loadMoreData(true);
    }

    public void setCheckFristTab() {
        mHotItem.setCheck(true);
        mNewItem.setCheck(false);
        mTenItem.setCheck(false);
        mAllItem.setCheck(false);
        current_sort = POPULARITY;
        mUnit = "";
    }

    @OnClick({R.id.common_questions_btn, R.id.search_btn})
    public void actionClick(View view) {
        switch (view.getId()) {
            case R.id.search_btn:
                startActivity(SearchActivity.class);
                break;
            case R.id.common_questions_btn:
                startActivity(GeneralProblemActivity.class);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        if (mRefreshCall != null) {
            mRefreshCall.cancel();
            mRefreshCall = null;
            Logger.i("mRefreshCall: cancel");
        }
        if (mLoadMoreCall != null) {
            mLoadMoreCall.cancel();
            mLoadMoreCall = null;
            Logger.i("mLoadMoreCall: cancel");
        }
        super.onDestroyView();
    }
}
