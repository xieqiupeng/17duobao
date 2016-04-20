package com.zhiri.bear.ui.pay;

import android.content.Intent;
import android.view.View;

import com.zhiri.bear.R;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.ui.home.HomeActivity;

/**
 * Created by MagicBean on 2016/02/26 16:16:53
 */
public class RechargeStatusActivity extends BaseActivity {
    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_rechage_status);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setTitleText("充值");
        getCustomActionBar().setLeftText("充值");
//        getCustomActionBar().setLeftTextColor(R.color.colorRed);
        getCustomActionBar().setLeftTextVisible(true);
    }

    @Override
    protected void initializeViews() {
        findViewById(R.id.just_play).setOnClickListener(this);
    }

    @Override
    protected void initializeData() {

    }

    @Override
    public void onActionBarClick(View view) {
        super.onActionBarClick(view);
        switch (view.getId()) {
            case R.id.actionbar_left_txt:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.just_play:
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("show_position", 0);
                intent.putExtra("refreshUserInfo", true);
                startActivity(intent);
                finish();
                break;
        }
    }
}
