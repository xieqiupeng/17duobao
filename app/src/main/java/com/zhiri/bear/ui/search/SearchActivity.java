package com.zhiri.bear.ui.search;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.db.SpCarDbHelper;
import com.zhiri.bear.db.models.GoodsModel;
import com.zhiri.bear.models.GoodsTypeDetails;
import com.zhiri.bear.models.HistoryKeyWords;
import com.zhiri.bear.models.HotWords;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.net.HttpResponse;
import com.zhiri.bear.net.RequestCallback;
import com.zhiri.bear.ui.adapter.GoodsCategoryListAdapter;
import com.zhiri.bear.ui.adapter.HistorySearchAdapter;
import com.zhiri.bear.ui.adapter.HotSearchAdapter;
import com.zhiri.bear.ui.adapter.OnRecyclerViewItemClickListener;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.ui.user.UserManager;
import com.zhiri.bear.utils.DisplayUtils;
import com.zhiri.bear.utils.JsonParser;
import com.zhiri.bear.utils.SharePreHelper;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.utils.TextUtil;
import com.zhiri.bear.views.ClearEditText;
import com.zhiri.bear.views.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/02/29 17:17:28
 */
public class SearchActivity extends BaseActivity implements TextView.OnEditorActionListener {
    @Bind(R.id.clear_edit_ly)
    ClearEditText searchEditLy;
    @Bind(R.id.hot_search_list)
    RecyclerView hotSearchList;
    @Bind(R.id.history_search_list)
    RecyclerView historySearchList;
    @Bind(R.id.hot_search_item)
    LinearLayout hotSearchItem;
    @Bind(R.id.result_header_item)
    RelativeLayout resultHeaderItem;
    @Bind(R.id.search_history_item)
    RelativeLayout searchHistoryItem;
    @Bind(R.id.action_left_btn)
    TextView actionLeftBtn;
    @Bind(R.id.search_result_txt)
    TextView search_result_txt;
    private ArrayList<HistoryKeyWords> historyListData = new ArrayList<>();
    private ArrayList<GoodsTypeDetails> searchResult = new ArrayList<>();
    private ArrayList<HotWords> hotWordsData = new ArrayList<>();
    private HistorySearchAdapter historySearchAdapter;
    private HotSearchAdapter hotSearchAdapter;
    private GoodsCategoryListAdapter searchResultAdapter;
    private LiteOrm mLiteOrm;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_search);
    }

    @Override
    protected void initializeViews() {
        findViewById(R.id.action_right_btn).setOnClickListener(this);
        actionLeftBtn.setOnClickListener(this);
        searchEditLy.getEditView().setOnClickListener(this);
        searchEditLy.getEditView().setOnEditorActionListener(this);
        searchEditLy.getClearCloseBtn().setOnClickListener(this);
        findViewById(R.id.clear_history).setOnClickListener(this);

        //热词
        hotSearchList.setHasFixedSize(true);
        hotSearchList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        hotSearchAdapter = new HotSearchAdapter(hotWordsData);
        hotSearchList.setAdapter(hotSearchAdapter);
        hotSearchAdapter.setOnItemClickLitener(hotItemClickListener);

        //搜索结果
        searchResultAdapter = new GoodsCategoryListAdapter(searchResult);

        //历史记录
        historySearchList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        historySearchList.setHasFixedSize(true);
        historySearchList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        historySearchAdapter = new HistorySearchAdapter(historyListData);
        historySearchList.setAdapter(historySearchAdapter);
        historySearchAdapter.setOnItemClickLitener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                HistoryKeyWords historyKeyWords = historySearchAdapter.datas.get(position);
                showResultUI();
                searchEditLy.setEditViewVale(historyKeyWords.keyWords);
                searchByKeyWords(historyKeyWords.keyWords);
            }
        });
    }

    @Override
    protected void initializeData() {
        if (mLiteOrm == null) {
            mLiteOrm = LiteOrm.newSingleInstance(App.getInst(), "search_history.db");
        }
        loadHotWordsByCache();
        loadHotWords();
        initializeDb();
    }

    private void loadHotWordsByCache() {
        String hotwords = SharePreHelper.getIns().getHotWords();
        if (TextUtil.isValidate(hotwords)) {
            HttpResponse<HotWords.BaseHotWords> response = JsonParser.deserializeByJson(hotwords, new TypeToken<HttpResponse<HotWords.BaseHotWords>>() {
            }.getType());
            if (response != null && response.isSuccess()) {
                hotWordsData.clear();
                if (TextUtil.isValidate(response.getDataFrist().list)) {
                    hotWordsData.addAll(response.getDataFrist().list);
                    hotSearchAdapter.notifyDataSetChanged();
                    SharePreHelper.getIns().saveHotWords(JsonParser.serializeToJson(response));
                }
            }
        }
    }

    private void initializeDb() {
        refreshDataFromDb();
    }

    private void refreshDataFromDb() {
        historyListData.clear();
        ArrayList<HistoryKeyWords> temps = mLiteOrm.query(new QueryBuilder<HistoryKeyWords>(HistoryKeyWords.class).appendOrderDescBy(HistoryKeyWords.KEY_ID));
        if (TextUtil.isValidate(temps)) {
            historyListData.addAll(temps);
            searchHistoryItem.setVisibility(View.VISIBLE);
        } else {
            searchHistoryItem.setVisibility(View.GONE);
        }
        historySearchAdapter.notifyDataSetChanged();
    }


    private void loadHotWords() {
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        Api.getRetrofit().hotWords(params).enqueue(new RequestCallback<HttpResponse<HotWords.BaseHotWords>>() {
            @Override
            public void onSuccess(HttpResponse<HotWords.BaseHotWords> response) {
                if (response != null && response.isSuccess()) {
                    hotWordsData.clear();
                    if (TextUtil.isValidate(response.getDataFrist().list)) {
                        hotWordsData.addAll(response.getDataFrist().list);
                        SharePreHelper.getIns().saveHotWords(JsonParser.serializeToJson(response));
                    }
                    hotSearchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFinish() {

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_edit:
                showSearchUI();
                break;
            case R.id.clear_close_iv:
                searchEditLy.clearEditView();
                showSearchUI();
                break;
            case R.id.action_right_btn:
                if (resultHeaderItem.getVisibility() == View.VISIBLE) {
                    showSearchUI();
                } else {
                    onBackPressed();
                }
                break;
            case R.id.action_left_btn:
                showSearchUI();
                break;
            case R.id.add_all_btn:
                addAll();
                break;
            case R.id.clear_history:
                mLiteOrm.deleteAll(HistoryKeyWords.class);
                T.showShort(getApplicationContext(), "清除成功");
                refreshDataFromDb();
                break;
        }
    }

    private void addAll() {
        if (!TextUtil.isValidate(searchResult)) return;
        for (GoodsTypeDetails goodsTypeData : searchResult) {
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

    OnRecyclerViewItemClickListener hotItemClickListener = new OnRecyclerViewItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            HotWords hot = hotSearchAdapter.datas.get(position);
            showResultUI();
            searchEditLy.setEditViewVale(hot.name);
            searchByKeyWords(hot.name);
            saveDb(hot.name);
        }
    };

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            String key = searchEditLy.getEditViewVaule();
            if (TextUtil.isValidate(key)) {
                searchByKeyWords(key);
                saveDb(key);
            } else {
                T.showShort(getApplicationContext(), "搜索关键字不能为空");
            }
        }
        return false;
    }

    private void saveDb(String key) {
        HistoryKeyWords keyWords = new HistoryKeyWords(key);
        mLiteOrm.save(keyWords);
    }

    private void showResultUI() {
        actionLeftBtn.setVisibility(View.VISIBLE);
        hotSearchItem.setVisibility(View.GONE);
        resultHeaderItem.setVisibility(View.VISIBLE);
        searchHistoryItem.setVisibility(View.GONE);
        historySearchList.setPadding(0, 0, 0, 0);
        historySearchList.setPadding(0, 0, 0, 0);
        historySearchList.setAdapter(searchResultAdapter);
    }

    private void showSearchUI() {
        actionLeftBtn.setVisibility(View.GONE);
        hotSearchItem.setVisibility(View.VISIBLE);
        resultHeaderItem.setVisibility(View.GONE);
        searchHistoryItem.setVisibility(View.VISIBLE);
        historySearchList.setPadding(DisplayUtils.dip2px(getApplicationContext(), 10), 0, 0, 0);
        historySearchList.setAdapter(historySearchAdapter);
        refreshDataFromDb();
    }

    private void searchByKeyWords(final String keywords) {
        searchResult.clear();
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        params.put("goodsName", keywords);
        Api.getRetrofit().search(params).enqueue(new RequestCallback<HttpResponse<GoodsTypeDetails>>(this) {
            @Override
            public void onSuccess(HttpResponse<GoodsTypeDetails> response) {
                searchResult.clear();
                if (response.isSuccess()) {
                    if (TextUtil.isValidate(response.data)) {
                        search_result_txt.setText("搜索结果 " + keywords + "(" + response.data.size() + ")");
                        searchResult.addAll(response.data);
                        showResultUI();
                    } else {
                        T.showShort(getApplicationContext(), response.getMessage() + "暂无数据");
                        showSearchUI();
                    }
                } else {
                    T.showShort(getApplicationContext(), response.getMessage());
                }
            }

            @Override
            public void onFinish() {
                searchResultAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
