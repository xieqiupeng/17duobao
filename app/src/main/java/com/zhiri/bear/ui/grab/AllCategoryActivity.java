package com.zhiri.bear.ui.grab;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zhiri.bear.R;
import com.zhiri.bear.models.GoodsType;
import com.zhiri.bear.ui.adapter.AllCategoryListAdapter;
import com.zhiri.bear.ui.adapter.OnRecyclerViewItemClickListener;
import com.zhiri.bear.ui.base.BaseActivity;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/03/29 17:17:33
 */
public class AllCategoryActivity extends BaseActivity {

    @Bind(R.id.mRecyclerview)
    RecyclerView mRecyclerview;
    private ArrayList<GoodsType> categorys;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_allcategory_list);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setLeftText("夺宝");
        getCustomActionBar().setTitleText("分类浏览");
        getCustomActionBar().setLeftTextVisible(true);
    }

    @Override
    protected void initializeViews() {
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        categorys = getIntent().getParcelableArrayListExtra("allCategory");
        final AllCategoryListAdapter mAdapter = new AllCategoryListAdapter(categorys);
        mRecyclerview.setAdapter(mAdapter);
        mAdapter.setOnItemClickLitener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                GoodsType goosType1 = mAdapter.datas.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("goodsTypeName", goosType1.name);
                bundle.putInt("goodsTypeId", goosType1.id);
                startActivity(GoodsCategoryListActivity.class, bundle);
            }
        });
    }

    @Override
    protected void initializeData() {

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

    @Override
    public void onClick(View v) {

    }

}
