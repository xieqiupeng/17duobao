package com.zhiri.bear.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.zhiri.bear.R;
import com.zhiri.bear.utils.DisplayUtils;

/**
 * Created by MagicBean on 2016/02/25 14:14:58
 */
public class CustomTextView extends TextView {

    private boolean mCheck;
    private boolean mToogleIcon;

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCheck(boolean check) {
        this.mCheck = check;
        mToogleIcon = check;
        if (check) {
            setTextColor(getResources().getColor(R.color.colorRed));
        } else {
            setTextColor(getResources().getColor(R.color.color_light_Black));
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCheck) {
            Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(getResources().getColor(R.color.colorRed));
            canvas.drawRect(0, getHeight() - DisplayUtils.dip2px(getContext(), 2), getWidth(), getHeight(), mPaint);
        }
    }
}
