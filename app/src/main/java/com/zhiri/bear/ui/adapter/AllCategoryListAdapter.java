package com.zhiri.bear.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhiri.bear.R;
import com.zhiri.bear.models.GoodsType;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.utils.ImageLoader;

import java.util.ArrayList;

/**
 * Created by MagicBean on 2016/02/24 15:15:00
 */
public class AllCategoryListAdapter extends RecyclerView.Adapter<AllCategoryListAdapter.ViewHolder> {
    public ArrayList<GoodsType> datas = null;

    public AllCategoryListAdapter(ArrayList<GoodsType> datas) {
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_category_list_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        GoodsType category = datas.get(position);
        holder.mTextView.setText(category.name);
        ImageLoader.loadImage(holder.itemView.getContext(), Api.getImageUrl(category.icon), holder.mImage);
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
        public ImageView mImage;

        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.category_name);
            mImage = (ImageView) view.findViewById(R.id.category_image);
        }
    }

    private OnRecyclerViewItemClickListener mOnItemClickLitener;

    public void setOnItemClickLitener(OnRecyclerViewItemClickListener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
