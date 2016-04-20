package com.zhiri.bear.ui.pay;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.orhanobut.logger.Logger;
import com.zhiri.bear.R;
import com.zhiri.bear.models.RechargeResult;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.net.HttpResponse;
import com.zhiri.bear.net.RequestCallback;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.ui.pay.alipay.PayResult;
import com.zhiri.bear.ui.pay.weixin.WXPay;
import com.zhiri.bear.utils.BtnClickUtils;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.utils.TextUtil;
import com.zhiri.bear.views.CheckableRelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by MagicBean on 2016/02/26 16:16:53
 */
public class RechargeActivity extends BaseActivity {
    @Bind(R.id.weixin_pay_item)
    CheckableRelativeLayout weixinPayItem;
    @Bind(R.id.zfb_pay_item)
    CheckableRelativeLayout zfbPayItem;
    @Bind(R.id.next_btn)
    Button nextBtn;
    @Bind(R.id.item_type_20)
    TextView itemType20;
    @Bind(R.id.item_type_50)
    TextView itemType50;
    @Bind(R.id.item_type_100)
    TextView itemType100;
    @Bind(R.id.item_type_200)
    TextView itemType200;
    @Bind(R.id.item_type_500)
    TextView itemType500;
    @Bind(R.id.item_type_custom_amount)
    EditText itemTypeCustomAmount;
    private String customAmount;
    private ArrayList<String> mDatas = new ArrayList<>();
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;
    private ArrayList<TextView> typeViews = new ArrayList<>();
    private TextView mSelectRechargeType;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_rechage);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setTitleText("充值");
        getCustomActionBar().setLeftText("我的");
//        getCustomActionBar().setLeftTextColor(R.color.colorRed);
        getCustomActionBar().setLeftTextVisible(true);
    }

    @Override
    protected void initializeViews() {
        WXPay.initializeWeiXinPay(getApplicationContext());
        nextBtn.setOnClickListener(this);
        weixinPayItem.setOnClickListener(this);
        zfbPayItem.setOnClickListener(this);
        weixinPayItem.setChecked(true);
        mSelectRechargeType = itemType20;
        itemType20.setTextColor(getResources().getColor(R.color.colorRed));
        itemType20.setBackgroundResource(R.drawable.button_red_border_shape);
    }

    @Override
    protected void initializeData() {
        typeViews.add(itemType20);
        typeViews.add(itemType50);
        typeViews.add(itemType100);
        typeViews.add(itemType200);
        typeViews.add(itemType500);
        typeViews.add(itemTypeCustomAmount);
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
            case R.id.next_btn:
                if (!checkNetWork()) return;
                if (checkLogin()) {
                    boolean doubleClick = BtnClickUtils.isFastDoubleClick();
                    if (!doubleClick) {
                        String tempAmount = mSelectRechargeType.getText().toString();
                        if (!TextUtil.isValidate(tempAmount)) {
                            T.showShort(getApplicationContext(), "充值金额不能为空");
                            return;
                        }
                        if (zfbPayItem.isChecked()) {
                            byAlipay(tempAmount);
                        } else {
                            byWechat(tempAmount);
                        }
                    }
                }
                break;
            case R.id.weixin_pay_item:
                weixinPayItem.setChecked(true);
                zfbPayItem.setChecked(false);

                break;
            case R.id.zfb_pay_item:
                zfbPayItem.setChecked(true);
                weixinPayItem.setChecked(false);
                break;
        }
    }

    @OnClick({R.id.item_type_20, R.id.item_type_50, R.id.item_type_100, R.id.item_type_200, R.id.item_type_500, R.id.item_type_custom_amount})
    public void onRechargeTypeClickListener(TextView v) {
        changeView(v);
        if (v instanceof TextView) {
            itemTypeCustomAmount.setText("");
        }
    }

    private void changeView(TextView v) {
        for (TextView typeView : typeViews) {
            if (v == typeView) {
                mSelectRechargeType = v;
                typeView.setTextColor(getResources().getColor(R.color.colorRed));
                typeView.setBackgroundResource(R.drawable.button_red_border_shape);
            } else {
                typeView.setTextColor(getResources().getColor(R.color.colorDivider));
                typeView.setBackgroundResource(R.drawable.button_gray_border_shape);
            }
        }
    }

    /**
     * 微信支付
     *
     * @param amount
     */
    private void byWechat(String amount) {
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        params.put("amount", amount);
        Api.getRetrofit().paymentByWechat(params).enqueue(new RequestCallback<HttpResponse<RechargeResult>>(this,false) {
            @Override
            public void onSuccess(HttpResponse<RechargeResult> response) {
                Logger.i("msg:" + response.toString());
                if (response != null) {
                    if (response.isSuccess()) {
                        RechargeResult temp = response.getDataFrist();
                        if (temp != null) {
                            WXPay.startPay(temp.prepay_id);
                        }
                    }
                }
            }

            @Override
            public void onFinish() {

            }
        });
    }

    /**
     * 支付宝支付
     *
     * @param amount
     */
    private void byAlipay(String amount) {
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        params.put("amount", amount);
        Api.getRetrofit().paymentByAliPay(params).enqueue(new RequestCallback<HttpResponse<RechargeResult>>(this,false) {
            @Override
            public void onSuccess(HttpResponse<RechargeResult> response) {
                Logger.i("msg:" + response.toString());
                if (response != null) {
                    if (response.isSuccess()) {
                        RechargeResult temp = response.getDataFrist();
                        if (temp != null) {
                            payAlipay(temp.payInfo);
                        }
                    }
                }
            }

            @Override
            public void onFinish() {

            }
        });
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    if (TextUtils.equals(resultStatus, "9000")) {
//                        Toast.makeText(RechargeActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        startActivity(RechargeStatusActivity.class);
//                        finish();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”
                        // 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(RechargeActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(RechargeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(RechargeActivity.this, "检查结果为：" + msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };
    private Runnable payRunnable;

    private void payAlipay(final String payInfo) {

        payRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(RechargeActivity.this);
                // 调用支付接口
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
}
