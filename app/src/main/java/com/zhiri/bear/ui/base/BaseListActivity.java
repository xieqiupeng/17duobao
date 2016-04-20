package com.zhiri.bear.ui.base;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.zhiri.bear.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/01/07 16:16:59
 */
public abstract class BaseListActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    protected List<Object> mEntities = new ArrayList<>();
    @Bind(R.id.mlistView)
    protected ListView mListView;
    protected BaseListAdapter mBaseListAdapter;
    @Bind(R.id.swipe_layout)
    protected SwipeRefreshLayout mSwipeLayout;

    @Override
    protected void setContentView() {
        setContentView(R.layout.base_list_activity);
    }

    @Override
    protected void initializeViews() {
//        mSwipeLayout.setColorSchemeResources(R.color.colorYellow, R.color.colorYellow, R.color.colorDarkYellow, R.color.colorDarkYellow);
        addHeader(mListView);
        mBaseListAdapter = new BaseListAdapter();
        mListView.setAdapter(mBaseListAdapter);
        mListView.setOnItemClickListener(this);
        addHeader(mListView);
    }

    public void addHeader(ListView listView) {

    }

    public void addFooter(ListView listView) {

    }

    @Override
    protected void initializeData() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onActionBarClick(View view) {
        super.onActionBarClick(view);
        switch (view.getId()) {
            case R.id.actionbar_left_image:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public class BaseListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mEntities != null ? mEntities.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mEntities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return getAdapterViewType(position);
        }

        @Override
        public int getViewTypeCount() {
            return getAdapterViewTypeCount();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getAdapterViewAtPosition(position, convertView, parent);
        }
    }

    public int getAdapterViewType(int position) {
        return 0;
    }

    public int getAdapterViewTypeCount() {
        return 1;
    }

    public Object getAdapterItem(int position) {
        return mBaseListAdapter.getItem(position);
    }

    public abstract View getAdapterViewAtPosition(int position, View convertView, ViewGroup parent);


}
