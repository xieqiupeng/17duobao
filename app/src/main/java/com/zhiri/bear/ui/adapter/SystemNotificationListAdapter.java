package com.zhiri.bear.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhiri.bear.R;
import com.zhiri.bear.models.SystemNotificationEntity;

import java.util.ArrayList;

/**
 * Created by MagicBean on 2016/02/24 15:15:00
 */
public class SystemNotificationListAdapter extends RecyclerView.Adapter<SystemNotificationListAdapter.ViewHolder> {
    public ArrayList<SystemNotificationEntity> datas = null;

    public SystemNotificationListAdapter(ArrayList<SystemNotificationEntity> datas) {
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.system_notification_list_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        SystemNotificationEntity notification = datas.get(position);
        holder.mTitle.setText(notification.title);
        holder.mTime.setText(notification.dateTime);
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
        public TextView mTitle, mTime;

        public ViewHolder(View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.notification_title);
            mTime = (TextView) view.findViewById(R.id.notification_time);
        }
    }

    private OnRecyclerViewItemClickListener mOnItemClickLitener;

    public void setOnItemClickLitener(OnRecyclerViewItemClickListener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
