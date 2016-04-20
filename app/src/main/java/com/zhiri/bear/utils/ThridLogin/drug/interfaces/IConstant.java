package com.zhiri.bear.utils.ThridLogin.drug.interfaces;

/**
 * @author：BoBoMEe Created at 2016/1/4.
 */
public interface IConstant {
    //==========================================================================
    //weixin
    //appid
    //请同时修改  androidmanifest.xml里面，.PayActivityd里的属性<data android:scheme="wxb4ba3c02aa476ea1"/>为新设置的appid
    public static String APP_ID = "wx47214887960b7089";

    public static String WEIXIN_APP_SECRET = "c4e6874610b2e68f8265973db902a9e8";


    //==========================================================================
    //QQ
    public static String TENCENT_APP_ID = "1105134189";


    //=========================================================================
    // weibo
    public static String WB_APP_KEY = "1510346764";
    public static String REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";
    public static String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";


    String WEIXINLOGINOBSERVER = "com.zhiri.bear.utils.ThridLogin.drug.wxapi.WEIXINLOGINOBSERVER";
    String WEIXINLOGINERROR = "com.zhiri.bear.utils.ThridLogin.drug.wxapi.WEIXINLOGINERROR";
}
