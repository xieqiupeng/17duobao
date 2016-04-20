package com.zhiri.bear.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.db.SpCarDbHelper;
import com.zhiri.bear.db.models.GoodsModel;
import com.zhiri.bear.models.RobGoodsList;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.ui.grab.GrabDetailsActivity;
import com.zhiri.bear.ui.user.UserManager;
import com.zhiri.bear.utils.ImageLoader;
import com.zhiri.bear.utils.TextUtil;
import com.zhiri.bear.views.OrderQuantityView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by MagicBean on 2016/02/26 11:11:24
 */
public class ShoppingCarListAdapter extends RecyclerView.Adapter<ShoppingCarListAdapter.ViewHolder> {
    public ArrayList<RobGoodsList> datas = null;
    public boolean isEdit;
    public OnCommonChangeListener mCommonChangeListener;
    public WeakReference<EditText> mCurrentEdit;

    public ShoppingCarListAdapter(ArrayList<RobGoodsList> datas) {
        this.datas = datas;
    }

    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
        notifyDataSetChanged();
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.buy_list_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.mGoodsEditCheckBox.setVisibility(isEdit ? View.VISIBLE : View.GONE);
        final RobGoodsList goods = datas.get(position);
        if (goods.buyCount == 0) {
            goods.buyCount = goods.defaultUnit;
        }
        viewHolder.mGoodsName.setText(goods.name);
        ImageLoader.loadImage(viewHolder.itemView.getContext(), Api.getImageUrl(goods.goodsImageFileKey), viewHolder.mGoodsImage);
        if (goods.unit == 10) {
            viewHolder.mGoodsMarkImge.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mGoodsMarkImge.setVisibility(View.GONE);
        }
        viewHolder.mGoodsCount.setText(goods.total + "");
        viewHolder.mGoodsSurplus.setText(goods.surplus + "");

        viewHolder.mGoodsAlertSurplus.setText(goods.surplus + "");
        viewHolder.mGoodsJoinDes.setText("参与人次需是" + goods.unit + "的倍数");
        if (goods.buyCount > goods.surplus) {
            viewHolder.mGoodsAlertSurplus.setText(goods.surplus + "");
            viewHolder.surplusLy.setVisibility(View.VISIBLE);
        } else {
            viewHolder.surplusLy.setVisibility(View.GONE);
        }
        if (isEdit) {
            viewHolder.mGoodsEditCheckBox.setChecked(goods.isCheck);
            viewHolder.mGoodsEditCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    goods.isCheck = isChecked;
                    if (mCommonChangeListener != null) {
                        mCommonChangeListener.onCheckChange(position, goods);
                    }
                }
            });
        }

        viewHolder.mGoodsQuantity.setMax(goods.surplus);
        viewHolder.mGoodsQuantity.setMin(goods.unit);
        viewHolder.mGoodsQuantity.setDefaultNumber(goods.defaultUnit);
        viewHolder.mGoodsQuantity.setQuantity(goods.buyCount);
        viewHolder.mGoodsQuantity.getQuantityEdit().setTag(viewHolder.getAdapterPosition());
        viewHolder.mGoodsQuantity.setAdapter(ShoppingCarListAdapter.this);
        viewHolder.mGoodsQuantity.setOnQuantityChangeListener(new MyOnQuantityChangeListener(viewHolder.itemView, position));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEdit) {
                    Context context = viewHolder.itemView.getContext();
                    Intent intent = new Intent(context, GrabDetailsActivity.class);
                    intent.putExtra("goodsId", goods.goodsId);
                    intent.putExtra("grabId", goods.id);
                    intent.putExtra("backTitle", "清单");
                    context.startActivity(intent);
                }
            }
        });
    }

    private class MyOnQuantityChangeListener implements OrderQuantityView.OnQuantityChangeListener {
        private final int position;
        private final View view;

        public MyOnQuantityChangeListener(View view, int position) {
            this.view = view;
            this.position = position;
        }

        @Override
        public void onQuantityChange(int quantity) {
            RobGoodsList goods = datas.get(position);
            goods.buyCount = quantity;
            GoodsModel goodsModel = new GoodsModel();
            goodsModel.grabId = goods.id;
            goodsModel.quantity = goods.buyCount;
            Logger.i("OnQuantityChangeListener:" + position + "--bubCount:" + quantity);
            if (App.getInst().getUser() == null) {
                UserManager.getIns().updateGoodsModel(goodsModel);
            } else {
                SpCarDbHelper.getInst().updateGoods(goodsModel);
            }
            getTotalPrice();
        }
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public ArrayList<RobGoodsList> getSelect() {
        ArrayList<RobGoodsList> lists = new ArrayList<>();
        for (RobGoodsList data : datas) {
            if (data.isCheck) {
                lists.add(data);
            }
        }
        return lists;
    }

    public void setSelectAll(boolean isCheck) {
        if (datas == null) return;
        for (RobGoodsList data : datas) {
            data.isCheck = isCheck;
        }
        notifyDataSetChanged();
    }

    /**
     * 获取总价
     *
     * @return 总价
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        if (TextUtil.isValidate(datas)) {
            for (RobGoodsList data : datas) {
                totalPrice += data.buyCount;
            }
        }
        if (mCommonChangeListener != null) {
            mCommonChangeListener.onPriceChange(totalPrice);
        }
        return totalPrice;
    }


    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mGoodsName, mGoodsCount, mGoodsSurplus, mGoodsJoinDes, mGoodsAlertSurplus;
        public ImageView mGoodsImage;
        public ImageView mGoodsMarkImge;
        public CheckBox mGoodsEditCheckBox;
        public OrderQuantityView mGoodsQuantity;
        public View surplusLy;

        public ViewHolder(View view) {
            super(view);
            mGoodsEditCheckBox = (CheckBox) view.findViewById(R.id.checkbox);
            mGoodsMarkImge = (ImageView) view.findViewById(R.id.goods_mark);
            mGoodsJoinDes = (TextView) view.findViewById(R.id.goods_join_describe);
            mGoodsSurplus = (TextView) view.findViewById(R.id.goods_super_txt);
            mGoodsCount = (TextView) view.findViewById(R.id.goods_count_txt);
            mGoodsName = (TextView) view.findViewById(R.id.goods_name);
            mGoodsImage = (ImageView) view.findViewById(R.id.goods_img);
            mGoodsAlertSurplus = (TextView) view.findViewById(R.id.goods_alert_surplus_txt);
            mGoodsQuantity = (OrderQuantityView) view.findViewById(R.id.goods_quantity_view);
            surplusLy = view.findViewById(R.id.surplus_ly);
        }
    }

    public interface OnCommonChangeListener {
        void onPriceChange(int price);

        void onCheckChange(int position, RobGoodsList robGoodsList);
    }

    /**
     * 总价变化监回调
     *
     * @param listener
     */
    public void setOnCommonChangeListener(OnCommonChangeListener listener) {
        this.mCommonChangeListener = listener;
    }

    /**
     * 清除当前editText 焦点 校正数据
     */
    public void clearCurrentEditTextFocus() {
        if (mCurrentEdit != null) {
            EditText before = mCurrentEdit.get();
            if (before != null) {
                before.clearFocus();
                mCurrentEdit.clear();
                mCurrentEdit = null;
            }
        }
    }
}
