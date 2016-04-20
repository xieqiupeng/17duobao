package com.zhiri.bear.ui.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zhiri.bear.R;
import com.zhiri.bear.models.LuckUser;
import com.zhiri.bear.ui.grab.GrabDetailsActivity;
import com.zhiri.bear.views.BaseBannerAdapter;
import com.zhiri.bear.views.VerticalBannerView;

import java.util.List;

/**
 * Created by MagicBean on 2016/02/25 16:16:33
 */
public class GrabVerticalBannerViewAdapter extends BaseBannerAdapter<LuckUser> {

    private List<LuckUser> mDatas;

    public GrabVerticalBannerViewAdapter(List<LuckUser> datas) {
        super(datas);
    }

    @Override
    public void setItem(final View view, final LuckUser luckUser) {
        TextView userName = (TextView) view.findViewById(R.id.luck_user_name);
        TextView luckTime = (TextView) view.findViewById(R.id.luck_time);
        TextView luckGoodsName = (TextView) view.findViewById(R.id.luck_goods);
        userName.setText(luckUser.name);
        luckTime.setText(luckUser.time + "获得");
        luckGoodsName.setText(luckUser.goodsName);
        //你可以增加点击事件
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), GrabDetailsActivity.class);
                intent.putExtra("goodsId", luckUser.goodsId);
                intent.putExtra("grabId", luckUser.robGoodsId);
                intent.putExtra("type", 1);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public View getView(VerticalBannerView parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.grab_vertical_banner_item, parent, false);
    }
}
