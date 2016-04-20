package com.zhiri.bear.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhiri.bear.R;
import com.zhiri.bear.models.HistoryKeyWords;

import java.util.ArrayList;

/**
 * Created by MagicBean on 2016/02/25 14:14:24
 */
public class HistorySearchAdapter extends RecyclerView.Adapter<HistorySearchAdapter.ViewHolder> {
    public ArrayList<HistoryKeyWords> datas = null;

    public HistorySearchAdapter(ArrayList<HistoryKeyWords> datas) {
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_search_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        HistoryKeyWords history = datas.get(position);
        holder.mTextView.setText(history.keyWords);
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickLitener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;


        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.history_keywords);
        }
    }

    private OnRecyclerViewItemClickListener mOnItemClickLitener;

    public void setOnItemClickLitener(OnRecyclerViewItemClickListener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
