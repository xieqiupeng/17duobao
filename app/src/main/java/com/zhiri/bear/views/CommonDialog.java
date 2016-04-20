package com.zhiri.bear.views;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.zhiri.bear.R;

/**
 * Created by MagicBean on 2016/03/12 15:15:21
 */
public class CommonDialog extends Dialog implements View.OnClickListener {
    private TextView mTitle;
    private TextView cancel, sure;
    private CommonDialogListener mListener;
    private View divider;

    public CommonDialog(Context context) {
        super(context, R.style.dialogTheme);
        initializeView();
    }


    private void initializeView() {
        setContentView(R.layout.common_dialog_layout);
        mTitle = (TextView) findViewById(R.id.common_title);
        cancel = (TextView) findViewById(R.id.cancle_btn);
        sure = (TextView) findViewById(R.id.ok_btn);
        divider = findViewById(R.id.divider_view);
        cancel.setOnClickListener(this);
        sure.setOnClickListener(this);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);
    }

    public CommonDialog setMessageTitle(String title) {
        mTitle.setText(title);
        return this;
    }

    public CommonDialog setLeftBtnText(String txt) {
        cancel.setText(txt);
        return this;
    }

    public CommonDialog setRightBtnText(int txt) {
        sure.setText(txt);
        return this;
    }

    public CommonDialog setRightBtnText(String txt) {
        sure.setText(txt);
        return this;
    }

    public TextView getLeftTextView() {
        return cancel;
    }

    public CommonDialog setLeftBtnEnabled(boolean enaled) {
        cancel.setEnabled(enaled);
        return this;
    }

    public CommonDialog setShowLeft(boolean isShow) {
        cancel.setVisibility(isShow ? View.VISIBLE : View.GONE);
        divider.setVisibility(isShow ? View.VISIBLE : View.GONE);
        return this;
    }

    public CommonDialog setListener(CommonDialogListener listener) {
        this.mListener = listener;
        return this;
    }

    public interface CommonDialogListener {
        void onLeft(CommonDialog commonDialog);

        void onRight(CommonDialog commonDialog);

    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.cancle_btn:
                if (mListener != null) {
                    mListener.onLeft(this);
                }
                break;
            case R.id.ok_btn:
                if (mListener != null) {
                    mListener.onRight(this);
                }
                break;
        }
    }
}
