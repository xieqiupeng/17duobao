package com.zhiri.bear.views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.orhanobut.logger.Logger;

public class KeyboardResizeLayout extends LinearLayout {

    private OnKeyboardResizeListener mListener;
    private static final int KEYBOARD_OPEN = 0;
    private static final int KEYBOARD_CLOSE = 1;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case KEYBOARD_CLOSE:
                    mListener.OnKeyboardChange(false);
                    break;
                case KEYBOARD_OPEN:
                    mListener.OnKeyboardChange(true);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public KeyboardResizeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public KeyboardResizeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardResizeLayout(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Logger.i("h:" + h + "--oldh:" + oldh);
        if (mListener != null && oldh > 0) {
            if (h < oldh) {
                mHandler.obtainMessage(KEYBOARD_OPEN).sendToTarget();
            } else {
                mHandler.obtainMessage(KEYBOARD_CLOSE).sendToTarget();
            }
        }
    }

    public interface OnKeyboardResizeListener {
        public void OnKeyboardChange(boolean isOpen);
    }

    public void setOnKeyboardResizeListener(OnKeyboardResizeListener listener) {
        this.mListener = listener;
    }
}
