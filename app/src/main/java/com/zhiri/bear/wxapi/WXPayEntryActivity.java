package com.zhiri.bear.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.orhanobut.logger.Logger;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhiri.bear.event.PayEvent;
import com.zhiri.bear.ui.pay.RechargeStatusActivity;
import com.zhiri.bear.ui.pay.weixin.Constants;
import com.zhiri.bear.utils.SharePreHelper;
import com.zhiri.bear.utils.T;

import de.greenrobot.event.EventBus;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Logger.i(TAG, "onPayFinish, errCode = " + resp.errCode);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            //0支付成功,-2取消支付，-1支付失败
            switch (resp.errCode) {
                case -1:
                    T.showShort(getApplicationContext(), "支付失败");
                    break;
                case -2:
                    T.showShort(getApplicationContext(), "取消支付");
                    break;
                case 0:
                    if (SharePreHelper.getIns().isFromPay()) {
                        EventBus.getDefault().post(new PayEvent(resp.errCode, resp.errStr));
                        SharePreHelper.getIns().setFromPay(false);
                    } else {
                        T.showShort(getApplicationContext(), "支付成功");
                        Intent intent = new Intent(WXPayEntryActivity.this, RechargeStatusActivity.class);
                        startActivity(intent);
                    }
                    break;
                default:
                    break;
            }
        }
        finish();
    }
}