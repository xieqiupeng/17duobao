package com.zhiri.bear.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhiri.bear.utils.ThridLogin.drug.interfaces.IConstant;
import com.zhiri.bear.utils.ThridLogin.drug.loginmanager.ObserverManager;

/**
 * Created by ntop on 15/9/4.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler, IConstant {


    // IWXAPI 是第三方app和微信通信的openapi接口
    public static IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注册微信
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);

        api.handleIntent(getIntent(), this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }

    @Override
    public void onReq(BaseReq arg0) {
    }

    /**
     * 授权登录，成功后会回调该方法
     */
    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                ObserverManager.getInstance().notify(WEIXINLOGINOBSERVER, this, ((SendAuth.Resp) resp).code);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                ObserverManager.getInstance().notify(WEIXINLOGINERROR, this, ((SendAuth.Resp) resp).code);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                ObserverManager.getInstance().notify(WEIXINLOGINERROR, this, ((SendAuth.Resp) resp).code);
                break;
            default:
                ObserverManager.getInstance().notify(WEIXINLOGINERROR, this, ((SendAuth.Resp) resp).code);
                break;
        }
        finish();
    }


}
