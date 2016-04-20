package com.zhiri.bear.ui.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class CommonBaseAdapter<T> extends BaseAdapter {

    private List<T> datas;

    public CommonBaseAdapter(List<T> datas) {
        this.datas = datas;
    }

    public CommonBaseAdapter() {

    }

    /**
     * 设置数据
     *
     * @param datas
     */
    public void setDatas(List<T> datas) {
        if (datas == null)
            return;
        this.datas = datas;
        notifyDataSetChanged();
    }

    /**
     * 添加数据
     *
     * @param datas
     */
    public void addDatas(List<T> datas) {
        if (datas == null)
            return;
        this.datas.addAll(datas);
        notifyDataSetChanged();
    }

    /**
     * 添加数据
     *
     * @param t
     */
    public void addData(T t) {
        if (t == null) return;
        this.datas.add(t);
    }

    /**
     * 添加数据到顶部
     *
     * @param t
     */
    public void addDataToFrist(T t) {
        if (t == null) return;
        this.datas.add(0, t);
    }

    /**
     * 清楚数据
     */
    public void cleanData() {
        if (datas == null) return;
        datas.clear();
        notifyDataSetChanged();
    }


    /**
     * 根据 position删除
     *
     * @param position
     */
    public void remove(int position) {
        if (datas == null) return;
        datas.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getAdapterViewAtPosition(position, convertView, parent);
    }

    public abstract View getAdapterViewAtPosition(int position, View convertView, ViewGroup parent);

}
