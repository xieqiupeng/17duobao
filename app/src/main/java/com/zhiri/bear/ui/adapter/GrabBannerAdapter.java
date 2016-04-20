package com.zhiri.bear.ui.adapter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zhiri.bear.models.Banner;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.ui.grab.BannerWebViewActivity;
import com.zhiri.bear.ui.grab.GoodsCategoryListActivity;
import com.zhiri.bear.ui.grab.GrabDetailsActivity;
import com.zhiri.bear.utils.TextUtil;
import com.zhiri.bear.widgets.rollviewpager.adapter.StaticPagerAdapter;

import java.util.ArrayList;

/**
 * Created by MagicBean on 2016/02/25 16:16:04
 */
public class GrabBannerAdapter extends StaticPagerAdapter {

    public ArrayList<Banner> datas = null;

    public GrabBannerAdapter(ArrayList<Banner> datas) {
        this.datas = datas;
    }

    @Override
    public View getView(final ViewGroup container, int position) {
        ImageView imageView = new ImageView(container.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        final Banner banner = datas.get(position);
        String url = Api.getImageUrl(banner.fileKey);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtil.isValidate(banner.address)) {
                    if (banner.address.startsWith("http://")) {
                        Intent intent = new Intent(container.getContext(), BannerWebViewActivity.class);
                        intent.putExtra("url", banner.address);
                        intent.putExtra("title", banner.title);
                        container.getContext().startActivity(intent);
                    } else if (banner.address.startsWith("category://")) {
                        Intent intent1 = new Intent(container.getContext(), GoodsCategoryListActivity.class);
                        int start = banner.address.lastIndexOf("/");
                        int end = banner.address.lastIndexOf(";");
                        String typeId = banner.address.substring(start + 1, end);
                        intent1.putExtra("goodsTypeName", banner.address.substring(end + 1, banner.address.length()));
                        if (TextUtils.isDigitsOnly(typeId)) {
                            intent1.putExtra("goodsTypeId", Integer.parseInt(typeId));
                            container.getContext().startActivity(intent1);
                        }
                    } else if (banner.address.startsWith("robdetail://")) {
                        Intent intent1 = new Intent(container.getContext(), GrabDetailsActivity.class);
                        int start = banner.address.lastIndexOf("/");
                        String id = banner.address.substring(start + 1, banner.address.length());
                        if (TextUtils.isDigitsOnly(id)) {
                            intent1.putExtra("goodsId", Integer.parseInt(id));
                            intent1.putExtra("grabId", Integer.parseInt(id));
                            container.getContext().startActivity(intent1);
                        }
                    }
                }
            }
        });
        Glide.with(container.getContext()).load(url).into(imageView);
        return imageView;
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

}
