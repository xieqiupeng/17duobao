package com.zhiri.bear.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhiri.bear.R;
import com.zhiri.bear.models.ProfileViewItem;

import java.util.ArrayList;

/**
 * Created by MagicBean on 2016/02/24 15:15:00
 */
public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileAdapter.ViewHolder> {
    public ArrayList<ProfileViewItem> datas = null;

    public UserProfileAdapter(ArrayList<ProfileViewItem> datas) {
        this.datas = datas;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile_list_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        ProfileViewItem item = datas.get(position);
        viewHolder.imge.setImageResource(item.itemIconRes);
        viewHolder.itemName.setText(item.itemName);
        viewHolder.dividerView.setVisibility(item.hasDivider ? View.VISIBLE : View.GONE);
        viewHolder.norDivider.setVisibility(item.hasNorDivider ? View.VISIBLE : View.GONE);
        if (mOnItemClickLitener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickLitener.onItemClick(viewHolder.itemView, position);
                }
            });
        }
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imge;
        private final TextView itemName;
        private final View dividerView, norDivider;

        public ViewHolder(View view) {
            super(view);
            imge = (ImageView) view.findViewById(R.id.winning_record_iv);
            itemName = (TextView) view.findViewById(R.id.item_name);
            dividerView = view.findViewById(R.id.divider_view);
            norDivider = view.findViewById(R.id.item_nor_divider);
        }
    }

    private OnRecyclerViewItemClickListener mOnItemClickLitener;

    public void setOnItemClickLitener(OnRecyclerViewItemClickListener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
