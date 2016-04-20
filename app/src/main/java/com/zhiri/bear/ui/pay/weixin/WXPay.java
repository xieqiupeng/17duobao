package com.zhiri.bear.ui.pay.weixin;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhiri.bear.ui.pay.httpclient.BasicNameValuePair;
import com.zhiri.bear.ui.pay.httpclient.NameValuePair;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class WXPay {

    private static IWXAPI msgApi;


    public static IWXAPI initializeWeiXinPay(Context context) {
        if (msgApi == null) {
            msgApi = WXAPIFactory.createWXAPI(context, null);
            msgApi.registerApp(Constants.APP_ID);
        }
        return msgApi;
    }

    public static void startPay(String prepay_id) {
        PayReq req = new PayReq();
        req.appId = Constants.APP_ID;
        req.partnerId = Constants.MCH_ID;
        req.prepayId = prepay_id;
        req.packageValue = "Sign=WXPay";
        req.nonceStr = genNonceStr();
        req.timeStamp = String.valueOf(genTimeStamp());


        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

        req.sign = genAppSign(signParams);

        Logger.i("orion", signParams.toString());
        msgApi.registerApp(Constants.APP_ID);
        msgApi.sendReq(req);

    }

    private static String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private static long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    private static String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constants.API_KEY);
        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        Logger.i("orion", appSign);
        return appSign;
    }
}
