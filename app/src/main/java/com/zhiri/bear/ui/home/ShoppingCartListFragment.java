package com.zhiri.bear.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.orhanobut.logger.Logger;
import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.db.SpCarDbHelper;
import com.zhiri.bear.db.models.GoodsModel;
import com.zhiri.bear.models.RobGoods;
import com.zhiri.bear.models.RobGoodsList;
import com.zhiri.bear.models.User;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.net.HttpResponse;
import com.zhiri.bear.net.RequestCallback;
import com.zhiri.bear.ui.adapter.OnRecyclerViewItemClickListener;
import com.zhiri.bear.ui.adapter.ShoppingCarListAdapter;
import com.zhiri.bear.ui.adapter.ShoppingCarRecommendAdapter;
import com.zhiri.bear.ui.base.BaseFragment;
import com.zhiri.bear.ui.grab.GrabDetailsActivity;
import com.zhiri.bear.ui.pay.PayActivity;
import com.zhiri.bear.ui.user.UserManager;
import com.zhiri.bear.utils.KeyBoardUtils;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.utils.TextUtil;
import com.zhiri.bear.views.DividerItemDecoration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;


/**
 * Created by MagicBean on 2016/02/23 15:15:48
 */
public class ShoppingCartListFragment extends BaseFragment {
    @Bind(R.id.mRecyclerview)
    XRecyclerView mRecyclerView;
    @Bind(R.id.edit_btn)
    TextView editBtn;
    @Bind(R.id.edit_item)
    LinearLayout editItem;
    @Bind(R.id.settlement_item)
    LinearLayout settlementItem;
    @Bind(R.id.edite_all_checkbox)
    CheckBox editeAllCheckbox;
    @Bind(R.id.recommend_goods_list)
    RecyclerView recommendGoodsList;
    @Bind(R.id.goods_total_price)
    TextView goodsTotalPrice;
    @Bind(R.id.goods_total_quantity)
    TextView goodsTotalQuantity;
    @Bind(R.id.select_edit_goods_total)
    TextView editGoodsTotal;
    @Bind(R.id.all_select_label)
    TextView allSelectLabel;
    @Bind(R.id.rootView)
    LinearLayout rootView;

    @Bind(R.id.recommend_layout)
    View recommendLayout;
    @Bind(R.id.car_list_layout)
    View carListLayout;

    /**
     * 刷新数据
     */
    public static final int REFRESH_DATA = 1;
    /**
     * 刷新时间
     */
    public static final int REFRESH_DATA_DELAYED = 60 * 10 * 1000;
    private ShoppingCarListAdapter mAdapter;
    private ArrayList<RobGoodsList> listData = new ArrayList<>();
    HashMap<Integer, GoodsModel> tempMap = new HashMap<>();
    private WeakReferenceHandler mHandler;
    private double mTotalPrice;
    private Call<HttpResponse<RobGoods>> mLoadDataCall;
    private ArrayList<RobGoodsList> recommendGoodsData = new ArrayList<>();
    private ShoppingCarRecommendAdapter mRecommendAdapter;
    private InputMethodManager mInputMethodManager;

    public static ShoppingCartListFragment newInstance() {
        Bundle args = new Bundle();
        ShoppingCartListFragment fragment = new ShoppingCartListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getRootViewLayoutId() {
        return R.layout.fragment_buy_list_layout;
    }

    boolean isChangeUi;

    @Override
    protected void initializeViews() {
        findViewById(R.id.edit_btn).setOnClickListener(this);
        findViewById(R.id.settlement_btn).setOnClickListener(this);
        findViewById(R.id.delete_btn).setOnClickListener(this);
        findViewById(R.id.just_grab_btn).setOnClickListener(this);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLaodingMoreProgressStyle(ProgressStyle.Pacman);
        mRecyclerView.setArrowImageView(R.mipmap.iconfont_downgrey);
        mRecyclerView.setLoadingMoreEnabled(false);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                if (!checkNetWork()) {
                    mRecyclerView.refreshComplete();
                    return;
                }
                silenceRefreshData(false);
            }

            @Override
            public void onLoadMore() {

            }
        });

        mAdapter = new ShoppingCarListAdapter(listData);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnCommonChangeListener(new ShoppingCarListAdapter.OnCommonChangeListener() {
            @Override
            public void onPriceChange(int price) {
                mTotalPrice = price;
                goodsTotalPrice.setText(price + "元");
            }

            @Override
            public void onCheckChange(int position, RobGoodsList robGoodsList) {
                ArrayList<RobGoodsList> temps = mAdapter.getSelect();
                editGoodsTotal.setText(temps.size() + "");
                if (temps.isEmpty()) {
                    allSelectLabel.setText("全选");
                    editeAllCheckbox.setChecked(false);
                } else {
                    if (mAdapter.getSelect().size() == mAdapter.getItemCount()) {
                        allSelectLabel.setText("取消全选");
                        editeAllCheckbox.setChecked(true);
                    } else {
                        allSelectLabel.setText("全选");
                        isChangeUi = true;
                        editeAllCheckbox.setChecked(false);
                    }
                }
            }
        });
        editeAllCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                allSelectLabel.setText(checked ? "取消全选" : "全选");
                if (!isChangeUi) {
                    editGoodsTotal.setText(mAdapter.datas.size() + "");
                    mAdapter.setSelectAll(checked);
                } else {
                    isChangeUi = false;
                }
            }
        });

        recommendGoodsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recommendGoodsList.setHasFixedSize(true);
        mRecommendAdapter = new ShoppingCarRecommendAdapter(recommendGoodsData);
        recommendGoodsList.setAdapter(mRecommendAdapter);
        mRecommendAdapter.setOnItemClickLitener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                RobGoodsList goods = mRecommendAdapter.datas.get(position);
                Intent intent = new Intent(getActivity(), GrabDetailsActivity.class);
                intent.putExtra("goodsId", goods.goodsId);
                intent.putExtra("grabId", goods.id);
                getActivity().startActivity(intent);
            }
        });
        mInputMethodManager = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                return false;
            }
        });
    }

    @Override
    protected void initializeData() {
        getRecommendGoodsData();
        KeyBoardUtils.setEventListener(getActivity(), new KeyBoardUtils.KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (!isOpen) {
                    mAdapter.clearCurrentEditTextFocus();
                }
            }
        });
    }


    private void getRecommendGoodsData() {
        mLoadDataCall = Api.getRetrofit().guessYouLike(HttpParamsHelper.createParams());
        mLoadDataCall.enqueue(new RequestCallback<HttpResponse<RobGoods>>() {
            @Override
            public void onSuccess(HttpResponse<RobGoods> response) {
                Logger.i("msg:" + response.toString());
                if (response.isSuccess() && !mLoadDataCall.isCanceled()) {
                    RobGoods temp = response.getDataFrist();
                    recommendGoodsData.clear();
                    if (temp != null && TextUtil.isValidate(temp.list)) {
                        recommendGoodsData.addAll(temp.list);
                    }
                    mRecommendAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFinish() {

            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            stopTimerTask();
        } else {
            silenceRefreshData(true);
            getRecommendGoodsData();
        }
    }

    protected void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                mInputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settlement_btn:
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("goods", mAdapter.datas);
                bundle.putDouble("price", mTotalPrice);
                stopTimerTask();
                startActivity(PayActivity.class, bundle);
                break;
            case R.id.edit_btn:
                if (editItem.getVisibility() == View.VISIBLE) {
                    editBtn.setText("编辑");
                    mAdapter.setEdit(false);
                    editItem.setVisibility(View.GONE);
                    settlementItem.setVisibility(View.VISIBLE);
                    silenceRefreshData(false);
                    mRecyclerView.setPullRefreshEnabled(true);
                } else {
                    editBtn.setText("取消");
                    mAdapter.setEdit(true);
                    editItem.setVisibility(View.VISIBLE);
                    settlementItem.setVisibility(View.GONE);
                    editeAllCheckbox.setChecked(false);
                    mRecyclerView.setPullRefreshEnabled(false);
                    if (mHandler != null) {
                        mHandler.removeCallbacksAndMessages(null);
                        Logger.i("->->->->->->->停止定时任务");
                    }
                }
                break;
            case R.id.just_grab_btn:
                ((HomeActivity) getActivity()).switchPage(0);
                break;
            case R.id.delete_btn:
                if (editeAllCheckbox.isChecked()) {
                    deleteAll();
                } else {
                    deleteSelect();
                }
                break;
        }
    }

    /**
     * 删除选中
     */
    private void deleteSelect() {
        ArrayList<RobGoodsList> selects = mAdapter.getSelect();
        if (TextUtil.isValidate(selects)) {
            Iterator<RobGoodsList> iterator = selects.iterator();
            User user = App.getInst().getUser();
            while (iterator.hasNext()) {
                RobGoodsList robGoodsList = iterator.next();
                if (user != null) {
                    SpCarDbHelper.getInst().deleteGoodsByGrabId(robGoodsList.id);
                } else {
                    UserManager.getIns().removeGoodsModel(robGoodsList.id);
                }
            }

            Iterator<RobGoodsList> iterator1 = listData.iterator();
            while (iterator1.hasNext()) {
                RobGoodsList robGoodsList = iterator1.next();
                for (RobGoodsList goodsList : selects) {
                    if (robGoodsList.id == goodsList.id) {
                        iterator1.remove();
                    }
                }
            }
            mAdapter.notifyDataSetChanged();
            if (!TextUtil.isValidate(listData)) {
                mAdapter.setEdit(false);
                editBtn.setText("编辑");
                editItem.setVisibility(View.GONE);
                editBtn.setVisibility(View.GONE);
                carListLayout.setVisibility(View.GONE);
                settlementItem.setVisibility(View.VISIBLE);
                recommendLayout.setVisibility(View.VISIBLE);
            } else {
                editBtn.setText("编辑");
                mAdapter.setEdit(false);
                editItem.setVisibility(View.GONE);
                settlementItem.setVisibility(View.VISIBLE);
            }
            mRecyclerView.setPullRefreshEnabled(true);
            silenceRefreshData(true);
            FragmentActivity activity = getActivity();
            if (activity != null) {
                ((HomeActivity) activity).updateCarSize();
            }
        } else {
            T.showShort(getContext().getApplicationContext(), "请选择");
        }
    }

    /**
     * 删除所有
     */
    private void deleteAll() {
        ArrayList<RobGoodsList> selects = mAdapter.getSelect();
        if (TextUtil.isValidate(selects)) {
            Iterator<RobGoodsList> iterator = selects.iterator();
            User user = App.getInst().getUser();
            while (iterator.hasNext()) {
                RobGoodsList robGoodsList = iterator.next();
                if (user != null) {
                    SpCarDbHelper.getInst().deleteGoodsByGrabId(robGoodsList.id);
                } else {
                    UserManager.getIns().removeGoodsModel(robGoodsList.id);
                }
            }
            mAdapter.notifyDataSetChanged();
            if (!TextUtil.isValidate(listData)) {
                editBtn.setText("编辑");
                editBtn.setVisibility(View.GONE);
                editItem.setVisibility(View.GONE);
                mAdapter.setEdit(false);
                settlementItem.setVisibility(View.VISIBLE);
                carListLayout.setVisibility(View.GONE);
                recommendLayout.setVisibility(View.VISIBLE);
            } else {
                editBtn.setText("编辑");
                mAdapter.setEdit(false);
                editItem.setVisibility(View.GONE);
                settlementItem.setVisibility(View.VISIBLE);
            }
            mRecyclerView.setPullRefreshEnabled(true);
            silenceRefreshData(true);
            FragmentActivity activity = getActivity();
            if (activity != null) {
                ((HomeActivity) activity).updateCarSize();
            }
        } else {
            T.showShort(getContext().getApplicationContext(), "请选择");
        }
    }

    @Override
    public void onDestroyView() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 开启定时任务
     */
    public void startTimerTask() {
        if (mHandler == null) {
            mHandler = new WeakReferenceHandler(this);
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessageDelayed(REFRESH_DATA, REFRESH_DATA_DELAYED);
    }

    /**
     * 停止定时任务
     */
    public void stopTimerTask() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            Logger.i("->->->->->->->停止定时任务");
        }
        mAdapter.setEdit(false);
        editItem.setVisibility(View.GONE);
        editBtn.setText("编辑");
        settlementItem.setVisibility(View.VISIBLE);
    }

    static class WeakReferenceHandler extends Handler {
        WeakReference<ShoppingCartListFragment> mActivityReference;

        public WeakReferenceHandler(ShoppingCartListFragment fragment) {
            mActivityReference = new WeakReference<ShoppingCartListFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_DATA:
                    if (mActivityReference.get() != null) {
                        Logger.i("->->->->->->->开始处理定时任务");
                        mActivityReference.get().silenceRefreshData(false);
                        Logger.i("->->->->->->->开始启动定时任务");
                        sendEmptyMessageDelayed(REFRESH_DATA, REFRESH_DATA_DELAYED);
                    }
                    break;
            }
        }
    }

    private void silenceRefreshData(boolean isShow) {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        ArrayList<GoodsModel> cacheGoodsList = null;
        if (App.getInst().getUser() == null) {
            cacheGoodsList = new ArrayList<>();
            HashMap<Integer, GoodsModel> tempGoods = UserManager.getIns().getGoodsMap();
            for (GoodsModel goodsModel : tempGoods.values()) {
                cacheGoodsList.add(goodsModel);
            }
        } else {
            cacheGoodsList = SpCarDbHelper.getInst().getAllGoods();
        }
        if (TextUtil.isValidate(cacheGoodsList)) {
            tempMap.clear();
            StringBuilder sb = new StringBuilder();
            for (GoodsModel goodsModel : cacheGoodsList) {
                Logger.i("cache goods:" + goodsModel.toString());
                sb.append(goodsModel.grabId).append(",");
                tempMap.put(goodsModel.grabId, goodsModel);
            }
            String ids = sb.toString();
            if (TextUtil.isValidate(ids)) {
                HashMap<String, Object> params = HttpParamsHelper.createParams();
                params.put("list", ids);
                Api.getRetrofit().shoppingCarList(params).enqueue(new RequestCallback<HttpResponse<RobGoodsList>>(isShow ? getActivity() : null) {
                    @Override
                    public void onSuccess(HttpResponse<RobGoodsList> response) {
                        if (response != null && response.isSuccess()) {
                            listData.clear();
                            if (TextUtil.isValidate(response.data)) {
                                goodsTotalQuantity.setText(response.data.size() + "");
                                double price = 0;
                                double temp = 0;
                                for (RobGoodsList goodsList : response.data) {
                                    if (tempMap.containsKey(goodsList.id)) {
                                        GoodsModel goodModel = tempMap.get(goodsList.id);
                                        if (goodModel.quantity > goodsList.surplus) {
                                            temp = goodsList.surplus;
                                        } else if (goodModel.quantity < goodsList.unit) {
                                            temp = goodsList.unit;
                                        } else if (goodModel.quantity >= goodsList.unit && goodModel.quantity <= goodsList.surplus) {
                                            temp = goodModel.quantity;
                                        }
                                        goodsList.buyCount = (int) temp;
                                        price += temp;
                                        tempMap.remove(goodModel.grabId);
                                    }
                                }
                                listData.addAll(response.data);
                                goodsTotalPrice.setText(price + "元");
                                mTotalPrice = price;
                                recommendLayout.setVisibility(View.GONE);
                                carListLayout.setVisibility(View.VISIBLE);
                                settlementItem.setVisibility(View.VISIBLE);
                                editBtn.setVisibility(View.VISIBLE);
                                deleteCache();
                            } else {
                                deleteCache();
                                recommendLayout.setVisibility(View.VISIBLE);
                                carListLayout.setVisibility(View.GONE);
                                editBtn.setVisibility(View.GONE);
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFinish() {
                        if (mRecyclerView != null) {
                            mRecyclerView.refreshComplete();
                        }
                        tempMap.clear();
                        if (TextUtil.isValidate(listData)) {
                            startTimerTask();
                        }
                    }
                });
            }
        } else {
            recommendLayout.setVisibility(View.VISIBLE);
            editBtn.setVisibility(View.GONE);
            carListLayout.setVisibility(View.GONE);
        }
    }


    private void deleteCache() {
        User user = App.getInst().getUser();
        for (Map.Entry<Integer, GoodsModel> entry : tempMap.entrySet()) {
            if (user != null) {
                SpCarDbHelper.getInst().deleteGoodsById(entry.getValue().grabId);
            } else {
                UserManager.getIns().removeGoodsModel(entry.getValue().grabId);
            }
        }
        tempMap.clear();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            ((HomeActivity) activity).updateCarSize();
        }
    }
}
