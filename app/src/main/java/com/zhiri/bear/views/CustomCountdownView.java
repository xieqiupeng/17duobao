package com.zhiri.bear.views;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by MagicBean on 2016/02/26 14:14:28
 */
public class CustomCountdownView extends TextView {
    private CountDownTimer mCountDownTimer;

    public CustomCountdownView(Context context) {
        super(context);
        initialize();
    }

    public CustomCountdownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CustomCountdownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {

    }

    /**
     * 开始倒计时
     */
    public void startTimer() {
        setEnabled(false);
        mCountDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onFinish() {
                setEnabled(true);
                setText("发送验证码");
            }

            @Override
            public void onTick(long millisUntilFinished) {
                setText("" + millisUntilFinished / 1000 + "秒后重发");
            }
        };
        mCountDownTimer.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        super.onDetachedFromWindow();
    }

    public void cancel() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        setEnabled(true);
        setText("发送验证码");
    }
}
