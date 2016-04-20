package com.zhiri.bear.push;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.orhanobut.logger.Logger;
import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.models.PushMessageEntity;
import com.zhiri.bear.net.Api;
import com.zhiri.bear.net.HttpParamsHelper;
import com.zhiri.bear.net.HttpResponse;
import com.zhiri.bear.ui.base.BaseActivity;
import com.zhiri.bear.ui.home.HomeActivity;
import com.zhiri.bear.ui.user.UserManager;
import com.zhiri.bear.ui.user.WinRecordWebViewActivity;
import com.zhiri.bear.utils.JsonParser;
import com.zhiri.bear.utils.TextUtil;

import java.util.HashMap;

import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MagicBean on 2016/03/09 21:21:40
 */
public class PushMessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Logger.i("pushmessage:" + bundle.toString());
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");
                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");
                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));

                if (payload != null) {
                    try {
                        String data = new String(payload);
                        PushMessageEntity pushMessageEntity = JsonParser.deserializeByJson(data, PushMessageEntity.class);
                        Logger.i("PushMessageReceiver receiver payload : " + data);
                        if (pushMessageEntity != null) {
                            Intent intent1 = new Intent();
                            if (pushMessageEntity.type == 0) {//中奖
                                intent1.setClass(context, WinRecordWebViewActivity.class);
                                Activity activity = App.getInst().getCurrentShowActivity();
                                if (activity != null) {
                                    BaseActivity mActivity = (BaseActivity) activity;
                                    mActivity.showLuckDialog(pushMessageEntity.issueNumber,pushMessageEntity.name);
                                }
                            } else if (pushMessageEntity.type == 1) {//发货
//                                intent1.setClass(context, HomeActivity.class);
                            } else {
                                intent1.setClass(context, HomeActivity.class);
                            }
                            sendNotification(context, pushMessageEntity.name, pushMessageEntity.message, intent1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                registerToServer(cid);
                Logger.i("PushMessageReceiver receiver cid : " + cid);
                break;
            case PushConsts.THIRDPART_FEEDBACK:
                /*
                 * String appid = bundle.getString("appid"); String taskid =
                 * bundle.getString("taskid"); String actionid = bundle.getString("actionid");
                 * String result = bundle.getString("result"); long timestamp =
                 * bundle.getLong("timestamp");
                 *
                 * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo", "taskid = " +
                 * taskid); Log.d("GetuiSdkDemo", "actionid = " + actionid); Log.d("GetuiSdkDemo",
                 * "result = " + result); Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                 */
                break;
            default:
                break;
        }
    }

    private void sendNotification(Context context, String title, String msg, Intent intent) {
        int requestCode = (int) SystemClock.uptimeMillis();
        NotificationManager mNotificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
//                .setSmallIcon()
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.logo)
                .setDefaults(Notification.DEFAULT_ALL | Notification.DEFAULT_SOUND)
                .setContentTitle(title)
//				.setStyle(new NotificationCompat.BigTextStyle().bigText(entity.content))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
//		mBuilder.setPriority(Notification.PRIORITY_HIGH);
//		mBuilder.setOngoing(true);
        mNotificationManager.notify(requestCode, mBuilder.build());
    }

    private void registerToServer(String token) {
        if (!TextUtil.isValidate(token)) {
            return;
        }
        if (UserManager.getIns().getUser() == null) return;
        HashMap<String, Object> params = HttpParamsHelper.createParams();
        params.put("pushToken", token);
        Api.getRetrofit().registerPushtokn(params).enqueue(new Callback<HttpResponse>() {
            @Override
            public void onResponse(Response<HttpResponse> response) {
                Logger.i("register pushtoken :" + response.toString());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}
