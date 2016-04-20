package com.zhiri.bear.ui.user;

import android.content.SharedPreferences;
import android.util.Base64;

import com.igexin.sdk.PushManager;
import com.zhiri.bear.App;
import com.zhiri.bear.db.models.GoodsModel;
import com.zhiri.bear.models.User;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.net.HttpResponse;
import com.zhiri.bear.utils.JsonParser;
import com.zhiri.bear.utils.TextUtil;

import java.util.HashMap;

import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by MagicBean on 2016/03/05 14:14:53
 */
public class UserManager {
    private static UserManager mUserManager;
    public HashMap<Integer, GoodsModel> goodsMap = new HashMap<>();

    public HashMap<Integer, GoodsModel> getGoodsMap() {
        return goodsMap;
    }

    private boolean isRegisterToServer;

    public void addGoodsModel(GoodsModel goodsModel) {
        if (goodsMap.containsKey(goodsModel.grabId)) {
            GoodsModel temp = goodsMap.get(goodsModel.grabId);
            temp.quantity += goodsModel.quantity;
            goodsMap.put(goodsModel.grabId, temp);
        } else {
            goodsMap.put(goodsModel.grabId, goodsModel);
        }
    }

    public void updateGoodsModel(GoodsModel goodsModel) {
        if (goodsMap.containsKey(goodsModel.grabId)) {
            GoodsModel temp = goodsMap.get(goodsModel.grabId);
            temp.quantity = goodsModel.quantity;
            goodsMap.put(goodsModel.grabId, temp);
        } else {
            goodsMap.put(goodsModel.grabId, goodsModel);
        }
    }

    public void removeGoodsModel(int gradId) {
        if (goodsMap.containsKey(gradId)) {
            goodsMap.remove(gradId);
        }
    }

    private UserManager() {
    }


    public static UserManager getIns() {
        if (mUserManager == null) {
            synchronized (UserManager.class) {
                mUserManager = new UserManager();
            }
        }
        return mUserManager;
    }

    public void logout() {

    }

    /**
     * 清除session
     */
    public void clearToken() {
        SharedPreferences mPreferences = App.getInst().getSharedPreferences("UserInfo", 0);
        mPreferences.edit().clear().commit();
    }

    /**
     * 刷新用户数据
     */
    public synchronized void refreshUserInfo() {
        if (getUser() == null) return;
        if (getUser() != null && !TextUtil.isValidate(getUser().token)) {
            return;
        }
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        params.put("token", getUser().token);
        Api.getRetrofit().refreshUserInfo(params).enqueue(new Callback<HttpResponse<User>>() {
            @Override
            public void onResponse(Response<HttpResponse<User>> response) {
                if (response.isSuccess()) {
                    if (response.body() != null) {
                        //token 过期或者 其他社保登录
                        if (response.body().code == 401 || response.body().code == 403) {
                            App.getInst().toLogin();
                        } else {
                            User user = response.body().getDataFrist();
                            if (user != null) {
                                App.getInst().setUser(user);
                                saveUserInfo(user);
                                if (!isRegisterToServer) {
                                    isRegisterToServer = true;
                                    PushManager.getInstance().initialize(App.getInst().getApplicationContext());
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public synchronized User getUser() {
        SharedPreferences mPreferences = App.getInst().getSharedPreferences("UserInfo", 0);
        String temp = mPreferences.getString("_user", "");
        byte[] base64Bytes = Base64.decode(temp, Base64.DEFAULT);
        String retStr = new String(base64Bytes);
        return JsonParser.deserializeByJson(retStr, User.class);
    }

    public synchronized void saveUserInfo(User user) {
        if (user == null) return;
        SharedPreferences mPreferences = App.getInst().getSharedPreferences("UserInfo", 0);
        String json = JsonParser.serializeToJson(user);
        String temp = new String(Base64.encode(json.getBytes(), Base64.DEFAULT));
        mPreferences.edit().putString("_user", temp).commit();
    }
}
