package com.zhiri.bear.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.igexin.sdk.PushManager;
import com.orhanobut.logger.Logger;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.db.SpCarDbHelper;
import com.zhiri.bear.models.User;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.net.HttpResponse;
import com.zhiri.bear.net.LoginRequestCallback;
import com.zhiri.bear.net.RequestCallback;
import com.zhiri.bear.net.cookie.PersistentCookieStore;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.ui.grab.GeneralProblemActivity;
import com.zhiri.bear.ui.home.HomeActivity;
import com.zhiri.bear.utils.BtnClickUtils;
import com.zhiri.bear.utils.SharePreHelper;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.utils.TextUtil;
import com.zhiri.bear.utils.ThridLogin.drug.interfaces.ILoginCallback;
import com.zhiri.bear.utils.ThridLogin.drug.loginmanager.LoginManager;
import com.zhiri.bear.utils.ThridLogin.drug.loginmanager.QQLogin;
import com.zhiri.bear.utils.ThridLogin.drug.loginmanager.WeiXinLogin;
import com.zhiri.bear.utils.ThridLogin.drug.loginmanager.WeiboLogin;
import com.zhiri.bear.utils.ThridLogin.drug.model.QQInfoModel;
import com.zhiri.bear.utils.ThridLogin.drug.model.QQTokenModel;
import com.zhiri.bear.utils.ThridLogin.drug.model.WeixinInfoModel;
import com.zhiri.bear.utils.ThridLogin.drug.model.WeixinTokenModel;
import com.zhiri.bear.views.CommonDialog;
import com.zhiri.bear.views.CustomCountdownView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Cookie;
import retrofit2.Response;


/**
 * Created by MagicBean on 2016/02/24 11:11:04
 */
public class LoginActivity extends BaseActivity implements TextWatcher {
    @Bind(R.id.phone)
    EditText phone;
    @Bind(R.id.ver_code)
    EditText verCode;
    @Bind(R.id.send_ver_btn)
    CustomCountdownView sendVerBtn;
    @Bind(R.id.login_btn)
    TextView loginBtn;
    private UMShareAPI mShareAPI;
    private String phoneStr;
    public boolean fromHome;
    private CommonDialog commonDialog;
    @Bind(R.id.ip_edit)
    EditText ipEdit;
    @Bind(R.id.agreement_checkbox)
    CheckBox agreement_checkbox;

    public static final int REQUEST_REG = 1001;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void initializeActionBar() {
        super.initializeActionBar();
        getCustomActionBar().setTitleText("登录");
        getCustomActionBar().setLeftText("取消");
        getCustomActionBar().setLeftTextColor(R.color.colorRed);
        getCustomActionBar().setLeftTextVisible(true);
        getCustomActionBar().getLeftText().setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    @Override
    protected void initializeViews() {
        sendVerBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        findViewById(R.id.register_btn).setOnClickListener(this);
        findViewById(R.id.weixin_btn).setOnClickListener(this);
        findViewById(R.id.qq_btn).setOnClickListener(this);
        findViewById(R.id.weibo_btn).setOnClickListener(this);
        findViewById(R.id.agreement_btn).setOnClickListener(this);
        findViewById(R.id.how_17grab_btn).setOnClickListener(this);
        verCode.addTextChangedListener(this);
        mShareAPI = UMShareAPI.get(getApplicationContext());
        findViewById(R.id.save_ip_btn).setOnClickListener(this);
        String ip = SharePreHelper.getIns().getServerUrl();
        if (ip != null && ip.length() > 0) {
            ipEdit.setText(ip);
        }
    }

    @Override
    protected void initializeData() {
        fromHome = getIntent().getBooleanExtra("fromHome", false);
        UserManager.getIns().clearToken();
        App.getInst().setUser(null);
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
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (TextUtil.isValidate(phone.getText().toString()) && TextUtil.isValidate(charSequence.toString())) {
            loginBtn.setEnabled(true);
        } else {
            loginBtn.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_ver_btn:
                getLoginVerification();
                break;
            case R.id.login_btn:
                if (agreement_checkbox.isChecked())
                    doLogin();
                else
                    Toast.makeText(LoginActivity.this, "请先同意用户条款哦", Toast.LENGTH_SHORT).show();
                ;
                break;
            case R.id.register_btn:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, REQUEST_REG);
                break;
            case R.id.weixin_btn:
                if (!BtnClickUtils.isFastDoubleClick() && agreement_checkbox.isChecked()) {
//                    mShareAPI.doOauthVerify(LoginActivity.this, SHARE_MEDIA.WEIXIN, umAuthListener);
                    showLoadingDialog();
                    rigistWeixin();
                    loginWeixin();
                } else
                    Toast.makeText(LoginActivity.this, "请先同意用户条款哦", Toast.LENGTH_SHORT).show();
                break;
            case R.id.qq_btn:
                if (!BtnClickUtils.isFastDoubleClick() && agreement_checkbox.isChecked()) {
//                    mShareAPI.doOauthVerify(LoginActivity.this, SHARE_MEDIA.QQ, umAuthListener);
                    showLoadingDialog();
                    rigistQQ();
                    loginQQ();
                } else
                    Toast.makeText(LoginActivity.this, "请先同意用户条款哦", Toast.LENGTH_SHORT).show();
                break;
            case R.id.weibo_btn:
                if (!BtnClickUtils.isFastDoubleClick() && agreement_checkbox.isChecked()) {
//                    mShareAPI.doOauthVerify(LoginActivity.this, SHARE_MEDIA.SINA, umAuthListener);
                    showLoadingDialog();
                    rigistWeibo();
                    loginWeibo();
                } else
                    Toast.makeText(LoginActivity.this, "请先同意用户条款哦", Toast.LENGTH_SHORT).show();
                break;
            case R.id.save_ip_btn:
                String ip = ipEdit.getText().toString();
                if (TextUtil.isValidate(ip)) {
                    SharePreHelper.getIns().setServerUrl(ip);
                    Api.resetRetrofit();
                    PersistentCookieStore store = new PersistentCookieStore(App.getInst());
                    store.removeAll();
                    T.showShort(getApplicationContext(), "修改成功");
                    finish();
                } else {
                    T.showShort(getApplicationContext(), "服务器地址不能为空");
                }
                break;
            case R.id.agreement_btn:
                GeneralProblemActivity.toIntent(this, "登录", GeneralProblemActivity.SHOW_PRIVACY);
                break;
            case R.id.how_17grab_btn:
                GeneralProblemActivity.toIntent(this, "登录", GeneralProblemActivity.SHOW_17_GRAB);
                break;
        }
    }

    private void getLoginVerification() {
        phoneStr = phone.getText().toString();
        if (!TextUtil.isValidate(phoneStr)) {
            T.showShort(getApplicationContext(), "请输入手机号");
            return;
        }
        if (!TextUtil.isPhone(phoneStr)) {
            T.showShort(getApplicationContext(), "请输入正确的手机号");
            return;
        }
        sendVerBtn.startTimer();
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        params.put("phone", phoneStr);
        //TODO
        Api.getRetrofit().loginVerCode(params).enqueue(new RequestCallback<HttpResponse>(this) {

            @Override
            public void onResponse(Response<HttpResponse> response) {
                super.onResponse(response);
                //
                Logger.i(response.headers().toString());
            }

            @Override
            public void onSuccess(HttpResponse response) {
                Logger.i("msg:" + response.toString());
                if (!response.isSuccess()) {
                    sendVerBtn.cancel();
                    if (commonDialog == null) {
                        commonDialog = new CommonDialog(LoginActivity.this).setMessageTitle("您还未注册,是否马上注册？").setListener(new CommonDialog.CommonDialogListener() {
                            @Override
                            public void onLeft(CommonDialog commonDialog) {

                            }

                            @Override
                            public void onRight(CommonDialog commonDialog) {
                                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                startActivityForResult(intent, REQUEST_REG);
                            }
                        });
                    }
                    commonDialog.show();
                } else {
                    T.showShort(getApplicationContext(), response.getMessage());
                }
            }

            @Override
            public void onFinish() {

            }
        });
    }

    private void doLogin() {
        String code = verCode.getText().toString();
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        params.put("phone", phoneStr);
        params.put("verification", code);
        Api.getRetrofit().login(params).enqueue(new RequestCallback<HttpResponse<User>>(this) {
            @Override
            public void onSuccess(HttpResponse<User> response) {
                Logger.i("msg:" + response.toString());
                if (response.isSuccess()) {
                    User user = response.getDataFrist();
                    if (user != null) {
                        App.getInst().setUser(user);
                        UserManager.getIns().saveUserInfo(user);
                        SpCarDbHelper.getInst().initDb(user.id + "");
                        PushManager.getInstance().initialize(LoginActivity.this.getApplicationContext());
//                        startActivity(HomeActivity.class);
//                        finish();
                        onBackPressed();
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

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Logger.i(data.toString());
            mShareAPI.getPlatformInfo(LoginActivity.this, platform, userInfoListener);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(getApplicationContext(), "取消授权", Toast.LENGTH_SHORT).show();
        }
    };

    private UMAuthListener userInfoListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int i, Map<String, String> map) {
            Logger.i("user info" + map.toString());
            if (platform == SHARE_MEDIA.WEIXIN) {
                if (TextUtil.isValidate(map.get("openid"))) {
                    doThreeLogin("WECHAT", map.get("nickname"), map.get("openid"), map.get("headimgurl"));
                } else {
                    T.showShort(getApplicationContext(), "获取用户信息失败,请重试！");
                }
            } else if (platform == SHARE_MEDIA.SINA) {
                doThreeLogin("MICROBLOG", map.get("screen_name"), map.get("uid"), map.get("profile_image_url"));
            } else if (platform == SHARE_MEDIA.QQ) {
                doThreeLogin("QQ", map.get("screen_name"), map.get("openid"), map.get("profile_image_url"));
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int i, Throwable throwable) {
            Toast.makeText(getApplicationContext(), "获取用户信息失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int i) {
            Toast.makeText(getApplicationContext(), "获取用户信息失败", Toast.LENGTH_SHORT).show();
        }
    };


    private void doThreeLogin(String loginType, String name, String identifier, String faceImageUrl) {
        SharePreHelper.getIns().setShouldShowNotification(false);
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        params.put("loginType", loginType);
        params.put("name", name);
        params.put("identifier", identifier);
        params.put("faceImageUrl", faceImageUrl);
        if (Looper.myLooper() == null)
            Looper.prepare();
        Api.getRetrofit().threeLogin(params).enqueue(new LoginRequestCallback<HttpResponse<User>>(this) {
            @Override
            public void onResponse(Response<HttpResponse<User>> response) {
                super.onResponse(response);
                //
                Log.e("url", response.toString());
            }

            @Override
            public void onSuccess(HttpResponse<User> response) {
                Logger.i("msg:" + response.toString());
                if (response.isSuccess()) {
                    User user = response.getDataFrist();
                    if (user != null) {
                        App.getInst().setUser(user);
                        UserManager.getIns().saveUserInfo(user);
                        SpCarDbHelper.getInst().initDb(user.id + "");
                        PushManager.getInstance().initialize(LoginActivity.this.getApplicationContext());
//                        startActivity(HomeActivity.class);
//                        finish();
                        onBackPressed();
                    }
                }
                closeLoadingDialog();
            }

            @Override
            public void onFinish() {
                PersistentCookieStore store = new PersistentCookieStore(App.getInst());
                for (Cookie cookie : store.getCookies()) {
                    Logger.i("cookies:" + cookie);
                }
            }
        });
        Looper.loop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        mShareAPI.onActivityResult(requestCode, resultCode, data);
        if (qqLogin != null && qqLogin.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        if (weiboLogin != null && weiboLogin.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        if (requestCode == REQUEST_REG && resultCode == RESULT_OK) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (fromHome) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("show_position", 0);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (commonDialog != null) {
            commonDialog.dismiss();
        }
        mShareAPI = null;
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    //====================================QQ====================================================

    private LoginManager qqLogin;

    private void rigistQQ() {
        if (qqLogin == null) {
            qqLogin = new LoginManager(this, QQLogin.class, new ILoginCallback() {
                @Override
                public void tokeCallBack(Object o) {
                    QQTokenModel qqTokenModel = (QQTokenModel) o;
                }

                @Override
                public void infoCallBack(Object o) {
                    QQInfoModel qqInfoModel = (QQInfoModel) o;
                    doThreeLogin("QQ", qqInfoModel.nickname, qqInfoModel.openId, qqInfoModel.figureurl_qq_2);
                }

                @Override
                public void cancelErrorCallBack() {
                    closeLoadingDialog();
                }
            });
        }

    }

    private void loginQQ() {
        qqLogin.doLogin();
    }

    //====================================weibo====================================================
    private LoginManager weiboLogin;

    private void rigistWeibo() {
        if (weiboLogin == null) {
            weiboLogin = new LoginManager(this, WeiboLogin.class, new ILoginCallback() {
                @Override
                public void tokeCallBack(Object o) {
                    Oauth2AccessToken mAccessToken = (Oauth2AccessToken) o;
                }

                @Override
                public void infoCallBack(Object o) {
                    com.zhiri.bear.utils.ThridLogin.drug.sinaapi.User user = (com.zhiri.bear.utils.ThridLogin.drug.sinaapi.User) o;
                    doThreeLogin("MICROBLOG", user.screen_name, user.id, user.profile_image_url);

                }

                @Override
                public void cancelErrorCallBack() {
                    closeLoadingDialog();
                }
            });
        }

    }

    private void loginWeibo() {
        weiboLogin.doLogin();
    }

    //====================================weixin=======================================================

    private LoginManager weixinLogin;

    // 注册到微信
    private void rigistWeixin() {
        if (weiboLogin == null) {
            weixinLogin = new LoginManager(this, WeiXinLogin.class, new ILoginCallback() {
                @Override
                public void tokeCallBack(Object o) {
                    WeixinTokenModel tokenModel = (WeixinTokenModel) o;
                }

                @Override
                public void infoCallBack(Object o) {
                    WeixinInfoModel infoModel = (WeixinInfoModel) o;
                    if (infoModel == null || infoModel.nickname == null || infoModel.openid == null || infoModel.headimgurl == null) {
                        Toast.makeText(LoginActivity.this, "获取授权信息失败，请重试！", Toast.LENGTH_LONG);
                        return;
                    }
                    doThreeLogin("WECHAT", infoModel.nickname, infoModel.openid, infoModel.headimgurl);
                }

                @Override
                public void cancelErrorCallBack() {
                    closeLoadingDialog();
                }
            });
        }

    }


    // 微信登陆请求
    private void loginWeixin() {
        weixinLogin.doLogin();
    }

    //=============================================================================================
}
