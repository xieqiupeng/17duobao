package com.zhiri.bear.utils.ThridLogin.drug.loginmanager;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhiri.bear.utils.ThridLogin.drug.interfaces.IConstant;
import com.zhiri.bear.utils.ThridLogin.drug.interfaces.ILogin;
import com.zhiri.bear.utils.ThridLogin.drug.interfaces.ILoginCallback;
import com.zhiri.bear.utils.ThridLogin.drug.model.WeixinInfoModel;
import com.zhiri.bear.utils.ThridLogin.drug.model.WeixinTokenModel;
import com.zhiri.bear.utils.ThridLogin.drug.service.WeixinInfoService;
import com.zhiri.bear.utils.ThridLogin.drug.service.WeixinTokenService;

/**
 * Created by bobomee on 2016/3/25.
 */
public class WeiXinLogin implements ILogin, IConstant, ObserverManager.MRObserver {

    // 微信相关
    public static IWXAPI api;

    private ILoginCallback iLoginCallback;
    public WeiXinLogin() {

        ObserverManager.getInstance().removeObservers(WEIXINLOGINOBSERVER);
        ObserverManager.getInstance().removeObservers(WEIXINLOGINERROR);
        ObserverManager.getInstance().addObserver(WEIXINLOGINOBSERVER, this);
        ObserverManager.getInstance().addObserver(WEIXINLOGINERROR,this);
    }

    @Override
    public void doLogin(Activity activity, ILoginCallback callback) {
        this.iLoginCallback = callback;

        api = WXAPIFactory.createWXAPI(activity, APP_ID);
        if (api.isWXAppInstalled())
        {
            api.registerApp(APP_ID);
            login();
        }
    }

    private void login() {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo";
        api.sendReq(req);//第三方发送消息给微信。
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (iLoginCallback != null) {
            return true;
        }
        return false;
    }

    @Override
    public void OnDestory() {
        ObserverManager.getInstance().removeObserver(this);
    }

    @Override
    public void notify(String name, Object sender, Object data) {

        if (name.equals(WEIXINLOGINOBSERVER)) {

            final String code = (String) data;

            if (TextUtils.isEmpty(code)) return;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    WeixinTokenModel tokenModel = WeixinTokenService.getWeixinToken(code);

                    if (null != iLoginCallback) iLoginCallback.tokeCallBack(tokenModel);

                    if (null != tokenModel) {
                        String accessToken = tokenModel.access_token;
                        String openId = tokenModel.openid;

                        if (!TextUtils.isEmpty(tokenModel.access_token) && !TextUtils.isEmpty(tokenModel.openid)) {
                            WeixinInfoModel infoModel = WeixinInfoService.getWeixinInfo(accessToken, openId);
                            if (null != iLoginCallback) iLoginCallback.infoCallBack(infoModel);
                        }
                    }
                }
            }).start();
        }
        else if(name.equals(WEIXINLOGINERROR)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (null != iLoginCallback) iLoginCallback.cancelErrorCallBack();
                }
            }).start();
        }
    }

}
