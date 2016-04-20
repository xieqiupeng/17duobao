package com.zhiri.bear.views;

import android.app.Dialog;
import android.content.Context;

import com.zhiri.bear.R;

/**
 * Created by MagicBean on 2016/03/02 10:10:06
 */
public class LoadingDialog extends Dialog {
    public LoadingDialog(Context context) {
        super(context, R.style.dialogTheme3);
        initializeView();
    }

    private void initializeView() {
        setContentView(R.layout.loading_dialog_layout);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
