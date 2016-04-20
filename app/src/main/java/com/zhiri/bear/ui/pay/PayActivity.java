package com.zhiri.bear.ui.pay;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.orhanobut.logger.Logger;
import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.db.SpCarDbHelper;
import com.zhiri.bear.event.PayEvent;
import com.zhiri.bear.models.RechargeResult;
import com.zhiri.bear.models.RobGoodsList;
import com.zhiri.bear.models.User;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.net.HttpResponse;
import com.zhiri.bear.net.RequestCallback;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.ui.base.CommonBaseAdapter;
import com.zhiri.bear.ui.base.ViewHolder;
import com.zhiri.bear.ui.pay.alipay.PayResult;
import com.zhiri.bear.ui.pay.weixin.WXPay;
import com.zhiri.bear.ui.user.LoginActivity;
import com.zhiri.bear.ui.user.UserManager;
import com.zhiri.bear.update.CheckUpdateUtil;
import com.zhiri.bear.utils.BtnClickUtils;
import com.zhiri.bear.utils.SharePreHelper;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.utils.TextUtil;
import com.zhiri.bear.views.CheckableRelativeLayout;
import com.zhiri.bear.views.NoScrollListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by MagicBean on 2016/02/26 16:16:53
 */
public class PayActivity extends BaseActivity {

    @Bind(R.id.expanded_img)
    ImageView expandedImg;
    @Bind(R.id.pay_all_price)
    TextView payAllPrice;
    @Bind(R.id.goods_list_header)
    TextView goodsListHeader;
    @Bind(R.id.goods_list_view)
    NoScrollListView goodsListView;
    @Bind(R.id.blanche_checkbox)
    CheckBox blancheCheckbox;
    @Bind(R.id.blanche_pay_amount)
    TextView blanchePayAmount;
    @Bind(R.id.balance_pay_item)
    CheckableRelativeLayout balancePayItem;
    @Bind(R.id.weixin_pay_amount)
    TextView weixinPayAmount;
    @Bind(R.id.weixin_pay_item)
    CheckableRelativeLayout weixinPayItem;
    @Bind(R.id.zfb_pay_amount)
    TextView zfbPayAmount;
    @Bind(R.id.zfb_pay_item)
    CheckableRelativeLayout zfbPayItem;
    @Bind(R.id.next_btn)
    TextView nextBtn;
    @Bind(R.id.blanche_pay_label)
    TextView blanche_pay_label;
    @Bind(R.id.divider_view)
    View divider_view;
    private ArrayList<RobGoodsList> mDatas = new ArrayList<>();
    private double price;
    private double surplusPrice;

    private CheckableRelativeLayout mCurrentCheckableLy;
    private TextView mCurrentTextView;
    private User user;
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_pay);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setTitleText("支付订单");
        getCustomActionBar().setLeftText("清单");
        getCustomActionBar().setLeftTextColor(R.color.colorRed);
        getCustomActionBar().setLeftTextVisible(true);
    }

    @Override
    protected void initializeViews() {
        SharePreHelper.getIns().setFromPay(false);
        WXPay.initializeWeiXinPay(getApplicationContext());
        nextBtn.setOnClickListener(this);
        expandedImg.setOnClickListener(this);
        weixinPayItem.setOnClickListener(this);
        zfbPayItem.setOnClickListener(this);
//        caclePrice(weixinPayItem, weixinPayAmount);
        mCurrentCheckableLy = weixinPayItem;
        mCurrentTextView = weixinPayAmount;
        blancheCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    surplusPrice = 0;
                    user = App.getInst().getUser();
                    if (user.balance >= price) {
                        blancheCheckbox.setChecked(true);
                        blanchePayAmount.setText(price + " 元");
                        weixinPayAmount.setText("0.0 元");
                        zfbPayAmount.setText("0.0 元");
                        weixinPayItem.setChecked(false);
                        zfbPayItem.setChecked(false);
                        weixinPayItem.setEnabled(false);
                        zfbPayItem.setEnabled(false);
                    } else {
                        surplusPrice = (price - user.balance);
                        blanchePayAmount.setText(user.balance + "  元");
                        BigDecimal bigDecimal = new BigDecimal(surplusPrice);
                        surplusPrice = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        mCurrentTextView.setText(surplusPrice + " 元");
                        mCurrentCheckableLy.setChecked(true);
                        weixinPayItem.setEnabled(true);
                        zfbPayItem.setEnabled(true);
                    }
                } else {
                    blanchePayAmount.setText(0.0 + " 元");
                    surplusPrice = price;
                    mCurrentTextView.setText(price + " 元");
                    mCurrentCheckableLy.setChecked(true);
                    weixinPayItem.setEnabled(true);
                    zfbPayItem.setEnabled(true);
                }
            }
        });
    }

    @Override
    protected void initializeData() {
        goodsListView.setAdapter(mAdapter);
        price = getIntent().getDoubleExtra("price", 0);
        payAllPrice.setText(price + " 元");
        ArrayList<RobGoodsList> goods = getIntent().getParcelableArrayListExtra("goods");
        if (TextUtil.isValidate(goods)) {
            mDatas.addAll(goods);
            mAdapter.notifyDataSetChanged();
        }
        refreshUserData();
        EventBus.getDefault().register(this);
    }

    private void refreshUserData() {
        user = App.getInst().getUser();
        if (user != null) {
            HashMap<String, Object> params = HttpParamsHelper.createParams();
            params.put("token", App.getInst().getUser().token);
            Api.getRetrofit().refreshUserInfo(params).enqueue(new RequestCallback<HttpResponse<User>>(this) {

                @Override
                public void onSuccess(HttpResponse<User> response) {
                    Logger.i("refreshUserInfo msg:" + response.toString());
                    if (response.isSuccess()) {
                        User user = response.getDataFrist();
                        if (user != null) {
                            App.getInst().setUser(user);
                            UserManager.getIns().saveUserInfo(response.getDataFrist());
                            if (user != null) {
                                blanche_pay_label.setText("余额支付(余额：" + user.balance + "元)");
                                if (user.balance <= 0) {
                                    blancheCheckbox.setEnabled(false);
                                } else {
                                    blancheCheckbox.setEnabled(true);
                                }
                            }
                            caclePrice(weixinPayItem, weixinPayAmount);
                        }
                    } else {
                        T.showShort(getApplicationContext(), response.getMessage());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    super.onFailure(t);
                }

                @Override
                public void onFinish() {

                }
            });
        } else {
            Bundle bundle = new Bundle();
            bundle.putBoolean("fromHome", true);
            startActivity(LoginActivity.class, bundle);
        }
    }

    private void caclePrice(CheckableRelativeLayout view, TextView textView) {
        User user = App.getInst().getUser();
        if (user != null) {
            if (blancheCheckbox.isChecked()) {
                surplusPrice = 0;
                user = App.getInst().getUser();
                if (user.balance >= price) {
                    blancheCheckbox.setChecked(true);
                    blanchePayAmount.setText(price + " 元");
                    weixinPayAmount.setText("0.0 元");
                    zfbPayAmount.setText("0.0 元");
                    blancheCheckbox.setEnabled(true);
                    weixinPayItem.setChecked(false);
                    zfbPayItem.setChecked(false);
                    weixinPayItem.setEnabled(false);
                    zfbPayItem.setEnabled(false);
                }
                else if (user.balance == 0) {
                    blancheCheckbox.setChecked(false);
                    blancheCheckbox.setEnabled(false);
                    blanchePayAmount.setText(user.balance + " 元");
                    surplusPrice = (price - user.balance);
                    BigDecimal bigDecimal = new BigDecimal(surplusPrice);
                    surplusPrice = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    mCurrentTextView.setText(surplusPrice + " 元");
                    mCurrentCheckableLy.setChecked(true);
                    weixinPayItem.setEnabled(true);
                    zfbPayItem.setEnabled(true);
                }
                else {
                    surplusPrice = (price - user.balance);
                    blanchePayAmount.setText(user.balance + "  元");
                    BigDecimal bigDecimal = new BigDecimal(surplusPrice);
                    surplusPrice = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    textView.setText(surplusPrice + " 元");
                    view.setChecked(true);
                    weixinPayItem.setEnabled(true);
                    zfbPayItem.setEnabled(true);
                }
            } else {
                blanchePayAmount.setText(0.0 + " 元");
                surplusPrice = price;
                textView.setText(price + " 元");
                view.setChecked(true);
                weixinPayItem.setEnabled(true);
                zfbPayItem.setEnabled(true);
            }
            mCurrentTextView = textView;
            mCurrentCheckableLy = view;
        }
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
    protected void onRestart() {
        super.onRestart();
        caclePrice(weixinPayItem, weixinPayAmount);
        if (user != null) {
            blanche_pay_label.setText("余额支付(余额：" + user.balance + "元)");
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
                        if (blancheCheckbox.isChecked()) {
                            if (surplusPrice > 0) {
                                if (weixinPayItem.isChecked()) {
                                    byWechat(surplusPrice + "");
                                } else {
                                    byAlipay(surplusPrice + "");
                                }
                            } else {
                                submitOrder();
                            }

                        } else {
                            if (surplusPrice > 0) {
                                if (weixinPayItem.isChecked()) {
                                    byWechat(surplusPrice + "");
                                } else {
                                    byAlipay(surplusPrice + "");
                                }
                            }
                        }
                    }
                }
                break;
            case R.id.weixin_pay_item:
                caclePrice(weixinPayItem, weixinPayAmount);
                zfbPayItem.setChecked(false);
                zfbPayAmount.setText("0.0 元");
                break;
            case R.id.zfb_pay_item:
                caclePrice(zfbPayItem, zfbPayAmount);
                weixinPayItem.setChecked(false);
                weixinPayAmount.setText("0.0 元");
                break;
            case R.id.expanded_img:
                if (goodsListHeader.getVisibility() == View.VISIBLE) {
                    goodsListHeader.setVisibility(View.GONE);
                    goodsListView.setVisibility(View.GONE);
                    divider_view.setVisibility(View.GONE);
                    rotateArrow(true);
                } else {
                    goodsListHeader.setVisibility(View.VISIBLE);
                    goodsListView.setVisibility(View.VISIBLE);
                    divider_view.setVisibility(View.VISIBLE);
                    rotateArrow(false);
                }
                break;
        }
    }


    private void rotateArrow(boolean expand) {
        float pivotX = expandedImg.getWidth() / 2f;
        float pivotY = expandedImg.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (expand) {
            fromDegrees = 180f;
            toDegrees = 360f;
        } else {
            fromDegrees = 0f;
            toDegrees = 180f;
        }
        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        animation.setDuration(100);
        animation.setFillAfter(true);
        expandedImg.startAnimation(animation);
    }

    private void submitOrder() {
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        if (TextUtil.isValidate(mDatas)) {
            try {
                JSONArray jsonArray = new JSONArray();
                JSONObject object;
                for (RobGoodsList mData : mDatas) {
                    object = new JSONObject();
                    object.put(mData.id + "", mData.buyCount);
                    jsonArray.put(object);
                }
                params.put("param", jsonArray.toString());
                Api.getRetrofit().payOrder(params).enqueue(new RequestCallback<String>(this, false) {
                    @Override
                    public void onSuccess(String response) {
                        if (TextUtil.isValidate(response)) {
                            try {
                                JSONObject object1 = new JSONObject(response);
                                if (object1.has("code")) {
                                    int code = object1.getInt("code");
                                    if (code == 401 || code == 403) {//401 token过期 403禁止登录
                                        App.getInst().toLogin();
                                    } else if (code == 90001 || code == 90002) {//90001 必须更新  // 90002 有更新
                                        CheckUpdateUtil.checkNewVersion();
                                    } else {
                                        SharePreHelper.getIns().savePayResultInfo(response);
                                        startActivity(PayStatusActivity.class);
                                        SpCarDbHelper.getInst().clear();
                                        UserManager.getIns().getGoodsMap().clear();
                                        finish();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFinish() {

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 微信支付
     *
     * @param amount
     */
    private void byWechat(String amount) {
        SharePreHelper.getIns().setShouldShowNotification(false);
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        params.put("amount", amount);
        Api.getRetrofit().paymentByWechat(params).enqueue(new RequestCallback<HttpResponse<RechargeResult>>(this, false) {
            @Override
            public void onSuccess(HttpResponse<RechargeResult> response) {
                Logger.i("msg:" + response.toString());
                if (response.isSuccess()) {
                    RechargeResult temp = response.getDataFrist();
                    if (temp != null) {
                        SharePreHelper.getIns().setFromPay(true);
                        WXPay.startPay(temp.prepay_id);
                    }
                } else {
                    T.showShort(getApplicationContext(), response.getMessage());
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
        SharePreHelper.getIns().setShouldShowNotification(false);
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        params.put("amount", amount);
        Api.getRetrofit().paymentByAliPay(params).enqueue(new RequestCallback<HttpResponse<RechargeResult>>(this, false) {
            @Override
            public void onSuccess(HttpResponse<RechargeResult> response) {
                Logger.i("msg:" + response.toString());
                if (response.isSuccess()) {
                    RechargeResult temp = response.getDataFrist();
                    if (temp != null) {
                        payAlipay(temp.payInfo);
                    }
                } else {
                    T.showShort(getApplicationContext(), response.getMessage());
                }
            }

            @Override
            public void onFinish() {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void wechatSuccess(PayEvent payEvent) {
        if (payEvent.code == 0) {
            submitOrder();
        }
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
                        submitOrder();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”
                        // 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(PayActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(PayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(PayActivity.this, "检查结果为：" + msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }
    };
    private Runnable payRunnable;

    private void payAlipay(final String payInfo) {

        payRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PayActivity.this);
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

    private CommonBaseAdapter<RobGoodsList> mAdapter = new CommonBaseAdapter<RobGoodsList>(mDatas) {
        @Override
        public View getAdapterViewAtPosition(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.pay_order_goods_item, parent, false);
            }
            RobGoodsList goods = (RobGoodsList) getItem(position);
            TextView name = ViewHolder.get(convertView, R.id.goods_name);
            TextView goodsNumber = ViewHolder.get(convertView, R.id.goods_count);
            name.setText(goods.name);
            goodsNumber.setText(goods.buyCount + "");
            return convertView;
        }
    };

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
