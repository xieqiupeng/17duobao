package com.zhiri.bear.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhiri.bear.R;
import com.zhiri.bear.models.RobGoodsList;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.ui.grab.GrabDetailsActivity;
import com.zhiri.bear.utils.ImageLoader;

import java.util.ArrayList;

/**
 * Created by MagicBean on 2016/02/25 14:14:24
 */
public class CommonRecommendAdapter extends RecyclerView.Adapter<CommonRecommendAdapter.ViewHolder> {
    public ArrayList<RobGoodsList> datas = null;
    private int itemWidth;

    public CommonRecommendAdapter(ArrayList<RobGoodsList> datas) {
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_car_recommend_list_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final RobGoodsList goods = datas.get(position);
        holder.mTextView.setText(goods.name);
        int progress = ((goods.total -goods.surplus) * 100) / goods.total;
        holder.mProgressBar.setProgress(progress);
        ImageLoader.loadImage(holder.itemView.getContext(), Api.getImageUrl(goods.goodsImageFileKey), holder.mImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(context, GrabDetailsActivity.class);
                intent.putExtra("goodsId", goods.goodsId);
                intent.putExtra("grabId", goods.id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImage;
        public ProgressBar mProgressBar;

        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.goods_name);
            mImage = (ImageView) view.findViewById(R.id.goods_img);
            mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        }
    }

    private OnRecyclerViewItemClickListener mOnItemClickLitener;

    public void setOnItemClickLitener(OnRecyclerViewItemClickListener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
