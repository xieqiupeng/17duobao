package com.zhiri.bear.net;


import android.os.Message;

import com.zhiri.bear.models.AppUpdateInfo;
import com.zhiri.bear.models.AtestAnnounce;
import com.zhiri.bear.models.GoodsTypeDetails;
import com.zhiri.bear.models.GoodsTypeListEntity;
import com.zhiri.bear.models.GrabEntity;
import com.zhiri.bear.models.HotWords;
import com.zhiri.bear.models.RechargeResult;
import com.zhiri.bear.models.RobGoods;
import com.zhiri.bear.models.RobGoodsList;
import com.zhiri.bear.models.SystemNotificationEntity;
import com.zhiri.bear.models.SystemSetting;
import com.zhiri.bear.models.User;
import com.zhiri.bear.ui.user.ProfileSettingActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by MagicBean on 2016/01/13 10:10:37
 */
public interface ApiService {

    /**
     * 登录获取验证码
     *
     * @param params
     * @return
     */
    @GET("login/sms/verification")
    Call<HttpResponse> loginVerCode(@QueryMap HashMap<String, Object> params);

    /**
     * 登录
     *
     * @param params
     * @return
     */
    @GET("appuser/register")
    Call<HttpResponse<User>> register(@QueryMap HashMap<String, Object> params);

    /**
     * 微信支付
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("wechat/payment")
    Call<HttpResponse<RechargeResult>> paymentByWechat(@FieldMap HashMap<String, Object> params);

    /**
     * 支付宝支付
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("alipay/payment")
    Call<HttpResponse<RechargeResult>> paymentByAliPay(@FieldMap HashMap<String, Object> params);

    /**
     * 登录
     *
     * @param params
     * @return
     */
    @GET("appuser/login")
    Call<HttpResponse<User>> login(@QueryMap HashMap<String, Object> params);

    /**
     * 第三方登录
     *
     * @param params
     * @return
     */
    @GET("appuser/other/login")
    Call<HttpResponse<User>> threeLogin(@QueryMap HashMap<String, Object> params);

    /**
     * 首页夺宝
     *
     * @param params
     * @return
     */
    @GET("index")
    Call<HttpResponse<GrabEntity>> index(@QueryMap HashMap<String, Object> params);

    /**
     * 夺宝
     *
     * @param params
     * @return
     */
    @GET("robgoods")
    Call<HttpResponse<RobGoods>> robgoods(@QueryMap HashMap<String, Object> params);

    /**
     * 猜你喜欢
     *
     * @param params
     * @return
     */
    @GET("robgoods/guess/you/like")
    Call<HttpResponse<RobGoods>> guessYouLike(@QueryMap HashMap<String, Object> params);

    /**
     * 退出登录
     *
     * @param params
     * @return
     */
    @GET("appuser/logout")
    Call<HttpResponse> logout(@QueryMap HashMap<String, Object> params);

    /**
     * 刷新用户信息
     *
     * @param params
     * @return
     */
    @GET("appuser/details")
    Call<HttpResponse<User>> refreshUserInfo(@QueryMap HashMap<String, Object> params);

    /**
     * 分类详情列表
     *
     * @param params
     * @return
     */
    @GET("getByGoodsType")
    Call<HttpResponse<GoodsTypeListEntity>> getByGoodsType(@QueryMap HashMap<String, Object> params);

    /**
     * 头像上传
     *
     * @param photo
     * @return
     */
    @POST("resource/upload")
    Call<HttpResponse<ProfileSettingActivity.UploadImageResult>> uploadFacePic(@Body RequestBody photo);

    /**
     * 消息中心
     *
     * @param params
     * @return
     */
    @GET("usercenter/messages")
    Call<HttpResponse<ArrayList<Message>>> message(@QueryMap Map<String, Object> params);

    /**
     * 用户信息修改
     *
     * @param params
     * @return
     */
    @GET("appuser/modify")
    Call<HttpResponse> modifyUserInfo(@QueryMap Map<String, Object> params);

    /**
     * 注册
     *
     * @param params
     * @return
     */
    @GET("user/register")
    Call<HttpResponse<String>> register(@QueryMap Map<String, String> params);

    /**
     * 搜索热词
     *
     * @param params
     * @return
     */
    @GET("hotwords")
    Call<HttpResponse<HotWords.BaseHotWords>> hotWords(@QueryMap Map<String, Object> params);

    /**
     * 搜索
     *
     * @param params
     * @return
     */
    @GET("search/robgoods")
    Call<HttpResponse<GoodsTypeDetails>> search(@QueryMap Map<String, Object> params);

    /**
     * 清单列表
     *
     * @param params
     * @return
     */
    @GET("appuser/detailed/list")
    Call<HttpResponse<RobGoodsList>> shoppingCarList(@QueryMap Map<String, Object> params);

    /**
     * 注册推送 token
     *
     * @param params
     * @return
     */
    @GET("pushtoken/upload")
    Call<HttpResponse> registerPushtokn(@QueryMap Map<String, Object> params);

    /**
     * 支付订单
     *
     * @param params
     * @return
     */
    @GET("robgoods/buy")
    Call<String> payOrder(@QueryMap Map<String, Object> params);

    /**
     * 中奖信息
     *
     * @param params
     * @return
     */
    @GET("luck_users")
    Call<HttpResponse<String>> luckUsers(@QueryMap Map<String, Object> params);

    /**
     * 检测更新
     *
     * @return
     */
    @GET("checkUpdate")
    Call<HttpResponse<AppUpdateInfo>> checkUpdate(@QueryMap Map<String, Object> params);

    /**
     * 获取系统通知
     *
     * @param params
     * @return
     */
    @GET("appuser/getPushInfo")
    Call<HttpResponse<SystemNotificationEntity.SystemNotification>> getNotfication(@QueryMap Map<String, Object> params);

    /**
     * 获取首页通知
     *
     * @param params
     * @return
     */
    @GET("appuser/pushInfoEnable")
    Call<HttpResponse<SystemNotificationEntity.SystemNotification>> getHomeNotitication(@QueryMap Map<String, Object> params);

    /**
     * 系统设置
     *
     * @param params
     * @return
     */
    @GET("getSettings")
    Call<HttpResponse<SystemSetting>> getSettings(@QueryMap Map<String, Object> params);

    /**
     * 最新揭晓
     *
     * @param params
     * @return
     */
    @GET("robgoods/lottery/soon")
    Call<HttpResponse<AtestAnnounce.AtestAnnounceEntity>> getAtestAnnounce(@QueryMap Map<String, Object> params);

    /**
     * 获取开奖结果
     *
     * @param params
     * @return
     */
    @GET("robgoods/lottery/results")
    Call<HttpResponse<AtestAnnounce>> getLotteryResult(@QueryMap Map<String, Object> params);
}
