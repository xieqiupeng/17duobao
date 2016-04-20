package com.zhiri.bear.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.zhiri.bear.R;
import com.zhiri.bear.ui.user.WinRecordWebViewActivity;

/**
 * Created by MagicBean on 2016/03/12 15:15:21
 */
public class WinningDialog extends Dialog implements View.OnClickListener {

    private TextView numberText;
    private TextView luckGoodsName;

    public WinningDialog(Context context) {
        super(context, R.style.dialogTheme);
        initializeView();
    }


    private void initializeView() {
        setContentView(R.layout.winning_layout);
        findViewById(R.id.notification_close_btn).setOnClickListener(this);
        numberText = (TextView) findViewById(R.id.number_txt);
        luckGoodsName = (TextView) findViewById(R.id.luck_goods_name);
        findViewById(R.id.fl_container).setOnClickListener(this);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);
    }

    public WinningDialog setData(String number, String message) {
        numberText.setText("期号：" + number);
        luckGoodsName.setText(message);
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.notification_close_btn:
                dismiss();
                break;
            case R.id.fl_container:
                getOwnerActivity().startActivity(new Intent(getOwnerActivity(), WinRecordWebViewActivity.class));
                dismiss();
                break;
        }
    }
}
