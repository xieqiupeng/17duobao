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

import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.db.SpCarDbHelper;
import com.zhiri.bear.db.models.GoodsModel;
import com.zhiri.bear.models.GoodsTypeDetails;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.ui.grab.GrabDetailsActivity;
import com.zhiri.bear.ui.user.UserManager;
import com.zhiri.bear.utils.ImageLoader;
import com.zhiri.bear.utils.T;

import java.util.ArrayList;

/**
 * Created by MagicBean on 2016/02/24 15:15:00
 */
public class GoodsCategoryListAdapter extends RecyclerView.Adapter<GoodsCategoryListAdapter.ViewHolder> {
    public ArrayList<GoodsTypeDetails> datas = null;

    public GoodsCategoryListAdapter(ArrayList<GoodsTypeDetails> datas) {
        this.datas = datas;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.goods_category_recyclerview_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final GoodsTypeDetails goods = datas.get(position);
        viewHolder.mGoodsName.setText(goods.name);
        viewHolder.mGoodsCount.setText(goods.total + "");
        viewHolder.mGoodsSurplus.setText(goods.surplus + "");
        if (goods.unit == 10) {
            viewHolder.mGoodsMark.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mGoodsMark.setVisibility(View.GONE);
        }
        if (goods.total != 0) {
            int progress = ((goods.total - goods.surplus) * 100) / goods.total;
            viewHolder.mGoodsProgressBar.setProgress(progress);
        } else {
            viewHolder.mGoodsProgressBar.setProgress(0);
        }
        ImageLoader.loadImage(viewHolder.itemView.getContext(), Api.getImageUrl(goods.goodsImageFileKey), viewHolder.mGoodsImage);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = viewHolder.itemView.getContext();
                Intent intent = new Intent(context, GrabDetailsActivity.class);
                intent.putExtra("goodsId", goods.goodsId);
                intent.putExtra("grabId", goods.id);
                context.startActivity(intent);
            }
        });
        viewHolder.mAddBuyCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodsModel goodsModel = new GoodsModel();
                goodsModel.grabId = goods.id;
                goodsModel.quantity = goods.defaultUnit;
                if (App.getInst().getUser() == null) {
                    UserManager.getIns().addGoodsModel(goodsModel);
                } else {
                    SpCarDbHelper.getInst().saveGoods(goodsModel);
                }
                T.showShort(viewHolder.itemView.getContext(), "添加成功");
            }
        });
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mGoodsName, mAddBuyCarBtn, mGoodsSurplus, mGoodsCount;
        public ProgressBar mGoodsProgressBar;
        public ImageView mGoodsImage, mGoodsMark;

        public ViewHolder(View view) {
            super(view);
            mGoodsName = (TextView) view.findViewById(R.id.goods_name);
            mGoodsMark = (ImageView) view.findViewById(R.id.goods_mark);
            mGoodsImage = (ImageView) view.findViewById(R.id.goods_img);
            mGoodsProgressBar = (ProgressBar) view.findViewById(R.id.goods_progressbar);
            mGoodsCount = (TextView) view.findViewById(R.id.goods_count_txt);
            mGoodsSurplus = (TextView) view.findViewById(R.id.goods_surplus_txt);
            mAddBuyCarBtn = (TextView) view.findViewById(R.id.add_buy_car_btn);
        }
    }
}
