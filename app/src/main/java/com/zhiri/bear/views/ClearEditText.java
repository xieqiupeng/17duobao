package com.zhiri.bear.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zhiri.bear.R;

/**
 * Created by MagicBean on 2016/03/18 11:11:59
 */
public class ClearEditText extends LinearLayout implements TextWatcher, View.OnClickListener {
    private EditText clearEdit;
    private ImageView clearClose;

    public ClearEditText(Context context) {
        super(context);
        initializeView();
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView();
    }


    private void initializeView() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        inflate(getContext(), R.layout.clear_edit_text_layout, this);
        clearEdit = (EditText) findViewById(R.id.clear_edit);
        clearClose = (ImageView) findViewById(R.id.clear_close_iv);
        clearEdit.addTextChangedListener(this);
//        clearClose.setOnClickListener(this);
//        clearEdit.setOnClickListener(this);
    }

    public void setEditViewVale(String txt) {
        clearEdit.setText(txt);
        clearEdit.setSelection(clearEdit.getText().length());
    }


    public EditText getEditView() {
        return clearEdit;
    }

    public void clearEditView() {
        clearEdit.setText("");
    }

    public ImageView getClearCloseBtn() {
        return clearClose;
    }

    public String getEditViewVaule() {
        return clearEdit.getText().toString();
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().length() > 0) {
            clearClose.setVisibility(VISIBLE);
        } else {
            clearClose.setVisibility(GONE);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_edit:
                break;
            case R.id.clear_close_iv:
//                clearEdit.setText("");
                break;
        }
    }
}
