package com.zhiri.bear.utils.ThridLogin.drug.interfaces;

/**
 * Created by bobomee on 2016/3/25.
 */
public interface ILoginCallback<TOKEN, INFO> {

    void tokeCallBack(TOKEN token);

    void infoCallBack(INFO info);

    void cancelErrorCallBack();
}
