package com.zhiri.bear.utils.ThridLogin.drug.loginmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhiri.bear.utils.ThridLogin.drug.interfaces.IConstant;
import com.zhiri.bear.utils.ThridLogin.drug.interfaces.ILogin;
import com.zhiri.bear.utils.ThridLogin.drug.interfaces.ILoginCallback;
import com.zhiri.bear.utils.ThridLogin.drug.model.QQInfoModel;
import com.zhiri.bear.utils.ThridLogin.drug.model.QQTokenModel;
import com.zhiri.bear.utils.ThridLogin.drug.util.GsonUtil;

import org.json.JSONObject;

/**
 * Created by bobomee on 2016/3/25.
 */
public class QQLogin implements ILogin, IConstant,ObserverManager.MRObserver {

    private final int INFO = 0;
    private final int TOKEN = 1;
    private static Tencent mTencent;
    private BaseUiListener baseUiListener;

    private Activity activity;
    private ILoginCallback iLoginCallback;

    public QQLogin() {
        ObserverManager.getInstance().removeObservers(WEIXINLOGINERROR);
        ObserverManager.getInstance().addObserver(WEIXINLOGINERROR,this);
    }

    @Override
    public void notify(String name, Object sender, Object data) {
        if(name.equals(WEIXINLOGINERROR)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (null != iLoginCallback) iLoginCallback.cancelErrorCallBack();
                }
            }).start();
        }
    }

    class BaseUiListener implements IUiListener {

        private int type;
        private Context context;

        //V2.0版本，参数类型由JSONObject 改成了Object,具体类型参考api文档
        public BaseUiListener(Context context, int i) {
            this.context = context;
            this.type = i;
        }

        @Override
        public void onComplete(Object o) {
            JSONObject object = (JSONObject) o;
            doComplete(object);
        }

        //在这里可以做一些登录成功的处理
        protected void doComplete(JSONObject values) {

            if (type == TOKEN) {
                QQTokenModel qqTokenModel = GsonUtil.jsonToBean(values.toString(), QQTokenModel.class);
                if (null != iLoginCallback) iLoginCallback.tokeCallBack(qqTokenModel);
                mTencent.setAccessToken(qqTokenModel.access_token,qqTokenModel.expires_in);
                mTencent.setOpenId(qqTokenModel.openid);
                UserInfo userInfo = new UserInfo(context, mTencent.getQQToken());
                userInfo.getUserInfo(new BaseUiListener(activity, INFO));
            } else {

                QQInfoModel qqInfoModel = GsonUtil.jsonToBean(values.toString(), QQInfoModel.class);
                qqInfoModel.openId =  mTencent.getOpenId();
                if (null != iLoginCallback) iLoginCallback.infoCallBack(qqInfoModel);
            }

        }

        //在这里可以做登录失败的处理
        @Override
        public void onError(UiError e) {

        }

        //在这里可以做登录被取消的处理
        @Override
        public void onCancel() {
            ObserverManager.getInstance().notify(WEIXINLOGINERROR, this, null);
        }
    }


    @Override
    public void doLogin(Activity activity, ILoginCallback callback) {

        //instance
        this.activity = activity;
        this.iLoginCallback = callback;

        baseUiListener = new BaseUiListener(activity, TOKEN);
        mTencent = Tencent.createInstance(TENCENT_APP_ID, activity);

        //login
        login();

    }

    private void login() {
        mTencent.login(activity, "all", baseUiListener);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN  && null != iLoginCallback) {
            mTencent.handleLoginData(data, baseUiListener);
            return true;
        }
        return false;
    }

    @Override
    public void OnDestory() {

    }
}
