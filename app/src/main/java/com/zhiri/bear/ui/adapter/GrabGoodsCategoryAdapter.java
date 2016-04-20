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
import com.zhiri.bear.utils.DisplayUtils;
import com.zhiri.bear.utils.ImageLoader;

import java.util.ArrayList;

/**
 * Created by MagicBean on 2016/02/25 14:14:24
 */
public class GrabGoodsCategoryAdapter extends RecyclerView.Adapter<GrabGoodsCategoryAdapter.ViewHolder> {
    public ArrayList<GoodsType> datas = null;
    private int itemWidth;

    public GrabGoodsCategoryAdapter(ArrayList<GoodsType> datas) {
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grab_category_item, parent, false);
        if (itemWidth == 0) {
            itemWidth = (DisplayUtils.getScreenWidth(parent.getContext()) - DisplayUtils.dip2px(parent.getContext(), 20)) / 4;
        }
        view.getLayoutParams().width = itemWidth;
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        GoodsType category = datas.get(position);
        holder.mTextView.setText(category.name);
        if (position == 0) {
            holder.mImage.setImageResource(R.mipmap.icon_category);
        } else {
            ImageLoader.loadImage(holder.itemView.getContext(), Api.getImageUrl(category.icon), holder.mImage);
        }
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
