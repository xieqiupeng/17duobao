package com.zhiri.bear.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zhiri.bear.R;
import com.zhiri.bear.ui.adapter.ShoppingCarListAdapter;
import com.zhiri.bear.utils.TextUtil;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;

/**
 * Created by MagicBean on 2016/03/10 20:20:46
 */
public class OrderQuantityView extends LinearLayout implements View.OnClickListener, View.OnFocusChangeListener {

    private EditText mQuantityEdit;
    private ImageView mPlusBtn, mMinusBtn;
    public int min = 1;
    public int max = 1000;
    private OnQuantityChangeListener mListener;
    private WeakReference<ShoppingCarListAdapter> mAdapter;
    private int mUnit = 1;

    public OrderQuantityView(Context context) {
        super(context);
        initializeView();
    }

    public OrderQuantityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }

    public OrderQuantityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView();
    }

    private void initializeView() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        inflate(getContext(), R.layout.order_quantity_layout, this);
        mMinusBtn = (ImageView) findViewById(R.id.minus_btn);
        mPlusBtn = (ImageView) findViewById(R.id.plus_btn);
        mQuantityEdit = (EditText) findViewById(R.id.goods_quantity_edit);
        mMinusBtn.setOnClickListener(this);
        mPlusBtn.setOnClickListener(this);
        mQuantityEdit.setOnFocusChangeListener(this);
    }

    public EditText getQuantityEdit() {
        return mQuantityEdit;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setQuantity(int quantity) {
        if (quantity > max) {
            mQuantityEdit.setText(max + "");
        } else if (quantity < min) {
            mQuantityEdit.setText(quantity + "");
        } else if (quantity >= min && quantity <= max) {
            mQuantityEdit.setText(quantity + "");
        }
    }

    public String getQuantity() {
        return mQuantityEdit.getText().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.minus_btn:
                changeMinus();
                break;
            case R.id.plus_btn:
                changePlus();
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            ShoppingCarListAdapter adapter = mAdapter.get();
            if (adapter != null) {
                if (adapter.mCurrentEdit != null) {
                    EditText before = adapter.mCurrentEdit.get();
                    if (before != null) {
                        before.clearFocus();
                    }
                }
                adapter.mCurrentEdit = new WeakReference<EditText>(mQuantityEdit);
            }
        } else {
            String s = mQuantityEdit.getText().toString();
            if (TextUtil.isValidate(s)) {
                int temp = Integer.parseInt(s.toString());
                BigDecimal decimal = new BigDecimal(Math.floor(temp / min));
                int number = decimal.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                number = number * min;
                if (number >= min && number <= max) {
                    mQuantityEdit.setText(number + "");
                    if (mListener != null) {
                        mListener.onQuantityChange(number);
                    }
                } else if (number > max) {
                    mQuantityEdit.setText(max + "");
                    if (mListener != null) {
                        mListener.onQuantityChange(max);
                    }
                } else if (number < min) {
                    mQuantityEdit.setText(min + "");
                    if (mListener != null) {
                        mListener.onQuantityChange(min);
                    }
                }
            } else {
                mQuantityEdit.setText(min + "");
                if (mListener != null) {
                    mListener.onQuantityChange(min);
                }
            }
        }
    }

    private void changeMinus() {
        String quantityStr = mQuantityEdit.getText().toString();
        if (TextUtil.isValidate(quantityStr)) {
            int quantity = Integer.parseInt(quantityStr);
            int newQuantity = quantity - min;
            if (newQuantity >= min) {
                mQuantityEdit.setText(newQuantity + "");
                if (mListener != null) {
                    mListener.onQuantityChange(newQuantity);
                }
            }
        }
    }

    private void changePlus() {
        String quantityStr = mQuantityEdit.getText().toString();
        if (TextUtil.isValidate(quantityStr)) {
            int quantity = Integer.parseInt(quantityStr);
            int newQuantity = quantity + min;
            if (newQuantity <= max) {
                mQuantityEdit.setText(newQuantity + "");
                if (mListener != null) {
                    mListener.onQuantityChange(newQuantity);
                }
            }
        }
    }

    public void setAdapter(ShoppingCarListAdapter adapter) {
        this.mAdapter = new WeakReference<ShoppingCarListAdapter>(adapter);
    }

    public void setDefaultNumber(int unit) {
        this.mUnit = unit;
    }

    public interface OnQuantityChangeListener {
        void onQuantityChange(int quantity);
    }

    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.mListener = listener;
    }
}
