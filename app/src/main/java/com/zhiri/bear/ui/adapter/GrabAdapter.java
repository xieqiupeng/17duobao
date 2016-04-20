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
import com.zhiri.bear.models.RobGoodsList;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.ui.grab.GrabDetailsActivity;
import com.zhiri.bear.ui.home.HomeActivity;
import com.zhiri.bear.ui.user.UserManager;
import com.zhiri.bear.utils.ImageLoader;
import com.zhiri.bear.utils.T;

import java.util.ArrayList;

/**
 * Created by MagicBean on 2016/02/24 15:15:00
 */
public class GrabAdapter extends RecyclerView.Adapter<GrabAdapter.ViewHolder> {
    public ArrayList<RobGoodsList> datas = null;

    public GrabAdapter(ArrayList<RobGoodsList> datas) {
        this.datas = datas;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grab_recyclerview_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final RobGoodsList goods = datas.get(position);
        viewHolder.mTextView.setText(goods.name);
        int progress = ((goods.total - goods.surplus) * 100) / goods.total;
        viewHolder.mProgressTxt.setText(progress + "%");
        viewHolder.mProgressBar.setProgress(progress);
        ImageLoader.loadImage(viewHolder.itemView.getContext(), Api.getImageUrl(goods.goodsImageFileKey), viewHolder.mGoodsImg);
        if (goods.unit == 10) {
            viewHolder.mMarkImg.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mMarkImg.setVisibility(View.GONE);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = viewHolder.itemView.getContext();
                Intent intent = new Intent(context, GrabDetailsActivity.class);
                intent.putExtra("goodsId", goods.goodsId);
                intent.putExtra("grabId", goods.id);
                intent.putExtra("backTitle", "夺宝");
                context.startActivity(intent);
            }
        });
        viewHolder.mAddBuyCar.setOnClickListener(new View.OnClickListener() {
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
                ((HomeActivity) viewHolder.itemView.getContext()).updateCarSize();
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
        public TextView mTextView;
        public TextView mProgressTxt;
        public TextView mAddBuyCar;
        public ProgressBar mProgressBar;
        public ImageView mGoodsImg;
        public ImageView mMarkImg;

        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.goods_name);
            mProgressTxt = (TextView) view.findViewById(R.id.progress_txt);
            mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
            mGoodsImg = (ImageView) view.findViewById(R.id.goods_img);
            mMarkImg = (ImageView) view.findViewById(R.id.mark_img);
            mAddBuyCar = (TextView) view.findViewById(R.id.add_buy_car);
        }
    }
}
