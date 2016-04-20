package com.zhiri.bear.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by MagicBean on 2016/03/18 18:18:24
 */
public class CustomEditView extends EditText {

    public CustomEditView(Context context) {
        super(context);
    }

    public CustomEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
//        setSelection(getText().length());
//        setInputType(EditorInfo.TYPE_NUMBER_FLAG_SIGNED);
    }
}
