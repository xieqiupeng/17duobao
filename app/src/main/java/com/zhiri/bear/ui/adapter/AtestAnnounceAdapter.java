package com.zhiri.bear.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhiri.bear.R;
import com.zhiri.bear.models.AtestAnnounce;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.ui.home.TestActivity;
import com.zhiri.bear.utils.ImageLoader;

import java.util.List;
import java.util.Map;

import cn.iwgang.countdownview.CountdownView;

/**
 * Created by MagicBean on 2016/02/24 15:15:00
 */
public class AtestAnnounceAdapter extends RecyclerView.Adapter<AtestAnnounceAdapter.ViewHolder> {
    public List<AtestAnnounce> datas = null;
    private Map<Integer, TestActivity.MyCustomCountDownTimer> mCustomCountDownTimers;

    public AtestAnnounceAdapter(List<AtestAnnounce> datas, Map<Integer, TestActivity.MyCustomCountDownTimer> customCountDownTimers) {
        this.datas = datas;
        this.mCustomCountDownTimers = customCountDownTimers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.atest_announce_recyclerview_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        AtestAnnounce curItemInfo = datas.get(position);
        int curID = curItemInfo.getCountId();
        viewHolder.cvCountdownView.setTag(curID);
        viewHolder.cvCountdownView.setTag(R.id.tag_id, viewHolder.getAdapterPosition());
        long countdown = curItemInfo.getCountdown();
        if (countdown > 0) {
            if (mCustomCountDownTimers.containsKey(curID)) {
                TestActivity.MyCustomCountDownTimer curMyCustomCountDownTimer = mCustomCountDownTimers.get(curID);
                curMyCustomCountDownTimer.refView(viewHolder.cvCountdownView, this, mCustomCountDownTimers);
            }
            viewHolder.goodsTobeUnveiled.setVisibility(View.VISIBLE);
            viewHolder.cvCountdownView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.cvCountdownView.allShowZero();
            if (curItemInfo.status != 5) {// 报错
                viewHolder.goodsTobeUnveiled.setVisibility(View.VISIBLE);
                viewHolder.goodsTobeUnveiled.setText("正在揭晓");
                viewHolder.errorLy.setVisibility(View.VISIBLE);
                viewHolder.cvCountdownView.setVisibility(View.GONE);
                viewHolder.labelOneTitle.setText("非常抱歉，");
                viewHolder.labelOneContent.setText("");
                viewHolder.labelTwoTitle.setText("数据通信故障");
                viewHolder.labelTwoContent.setText("");
                viewHolder.labelThreeTitle.setText("正在恢复中……");
                viewHolder.labelThreeContent.setText("");
            } else {
                if (curItemInfo.appUserInfoDto == null) {// 报错
                    viewHolder.goodsTobeUnveiled.setVisibility(View.VISIBLE);
                    viewHolder.goodsTobeUnveiled.setText("正在揭晓");
                    viewHolder.errorLy.setVisibility(View.VISIBLE);
                    viewHolder.cvCountdownView.setVisibility(View.GONE);
                    viewHolder.labelOneTitle.setText("非常抱歉，");
                    viewHolder.labelOneContent.setText("");
                    viewHolder.labelTwoTitle.setText("数据通信故障");
                    viewHolder.labelTwoContent.setText("");
                    viewHolder.labelThreeTitle.setText("正在恢复中……");
                    viewHolder.labelThreeContent.setText("");
                } else {//开奖成功
                    viewHolder.goodsTobeUnveiled.setVisibility(View.GONE);
                    viewHolder.errorLy.setVisibility(View.VISIBLE);
                    viewHolder.cvCountdownView.setVisibility(View.GONE);
                    viewHolder.labelOneTitle.setText("获得用户：");
                    viewHolder.labelOneContent.setText(curItemInfo.appUserInfoDto.name);
                    viewHolder.labelTwoTitle.setText("参与人次：");
                    viewHolder.labelTwoContent.setText(curItemInfo.appUserInfoDto.buyNumber + "");
                    viewHolder.labelThreeTitle.setText("幸运号码：");
                    viewHolder.labelThreeContent.setText(curItemInfo.luckyNo);
                }
            }
        }
        ImageLoader.loadImage(viewHolder.itemView.getContext(), Api.getImageUrl(curItemInfo.goodsImageFileKey), viewHolder.goodsImage);
        viewHolder.goodsName.setText(curItemInfo.name);
        viewHolder.goodsNumber.setText("期　　号： " + curItemInfo.issueNumber);
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView goodsImage;
        private final CountdownView cvCountdownView;
        private final TextView goodsName, goodsNumber, goodsTobeUnveiled;
        private final LinearLayout errorLy;
        private final TextView labelOneTitle, labelOneContent;
        private final TextView labelTwoTitle, labelTwoContent;
        private final TextView labelThreeTitle, labelThreeContent;

        public ViewHolder(View view) {
            super(view);
            cvCountdownView = (CountdownView) view.findViewById(R.id.cv_countdownView);
            goodsImage = (ImageView) view.findViewById(R.id.goods_img);
            goodsName = (TextView) view.findViewById(R.id.goods_name);
            goodsNumber = (TextView) view.findViewById(R.id.number_txt);
            goodsTobeUnveiled = (TextView) view.findViewById(R.id.to_be_unveiled);
            errorLy = (LinearLayout) view.findViewById(R.id.error_ly);

            labelOneTitle = (TextView) view.findViewById(R.id.label_one_title);
            labelOneContent = (TextView) view.findViewById(R.id.label_one_content);
            labelTwoTitle = (TextView) view.findViewById(R.id.label_two_title);
            labelTwoContent = (TextView) view.findViewById(R.id.label_two_content);
            labelThreeTitle = (TextView) view.findViewById(R.id.label_three_title);
            labelThreeContent = (TextView) view.findViewById(R.id.label_three_content);
        }
    }
}
