package com.zhiri.bear.db;

import android.database.Cursor;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.SQLStatement;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.orhanobut.logger.Logger;
import com.zhiri.bear.App;
import com.zhiri.bear.db.models.GoodsModel;
import com.zhiri.bear.models.User;
import com.zhiri.bear.utils.TextUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by MagicBean on 2016/03/10 18:18:13
 */
public class SpCarDbHelper {

    private static SpCarDbHelper db;
    private LiteOrm mLiteOrm;
    private String mDbName;
    private String mXX;

    private SpCarDbHelper() {

    }

    public static SpCarDbHelper getInst() {
        if (db == null) {
            synchronized (SpCarDbHelper.class) {
                db = new SpCarDbHelper();
            }
        }
        return db;
    }


    public void initDb(String dbName) {
        this.mDbName = "user_" + dbName + ".db";
        this.mXX = "user_" + dbName;
        if (mLiteOrm != null) {
            mLiteOrm.close();
        }
        mLiteOrm = LiteOrm.newSingleInstance(App.getInst(), mDbName);
        mLiteOrm.setDebugged(true); // open the log
        Logger.i("initDb->->->" + mDbName);
    }

    private void checkNull() {
        if (mLiteOrm == null) {
            if (TextUtil.isValidate(mDbName)) {
                initDb(mDbName);
            } else {
                User user = App.getInst().getUser();
                if (user != null) {
                    initDb(user.id + "");
                } else {
                    throw new NullPointerException("db not init");
                }
            }
        }
    }

    public int updateGoodsTest(GoodsModel goods) {
        checkNull();
        int state = 0;
        try {
            boolean result = tabIsExist();
            if (result) {
                SQLStatement sql = mLiteOrm.createSQLStatement("UPDATE _GRAB_LOCAL_CACHE SET " + GoodsModel.KEY_QUANTITY + " = "
                                + GoodsModel.KEY_QUANTITY + "+"
                                + goods.quantity + " WHERE "
                                + GoodsModel.KEY_GRABID + " =? "
                        , new String[]{goods.grabId + ""});
                Logger.i("update sql :" + sql.sql);
                state = sql.execUpdate(mLiteOrm.getWritableDatabase());
                Logger.i("execUpdate :" + state);
            } else {
                saveGoods(goods);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return state;
    }

    /**
     * 判断数据中是否存在某张表
     *
     * @return
     */
    private boolean tabIsExist() {
        Cursor cursor = mLiteOrm.getReadableDatabase().rawQuery("select count(*) as c from sqlite_master where type='table' and name ='_grab_local_cache'", null);
        boolean result = false;
        while (cursor.moveToNext()) {
            int count = cursor.getInt(0);
            if (count > 0) {
                result = true;
            }
            Logger.i("all tables-->" + count);
        }
        return result;
    }

    public void test1() {
        checkNull();
        ArrayList<GoodsModel> temps = mLiteOrm.query(GoodsModel.class);
        if (TextUtil.isValidate(temps)) {
            for (GoodsModel temp : temps) {
                Logger.i("query :" + temp.toString());
            }
        }

    }

    public  void updateGoods(GoodsModel goods) {
        checkNull();
        ArrayList<GoodsModel> temps = mLiteOrm.query(new QueryBuilder<GoodsModel>(GoodsModel.class).where(GoodsModel.KEY_GRABID + "=?", new String[]{goods.grabId + ""}));
        if (TextUtil.isValidate(temps)) {
            GoodsModel tempGoods = temps.get(0);
            tempGoods.quantity = goods.quantity;
            mLiteOrm.save(tempGoods);
        } else {
            mLiteOrm.save(goods);
        }
    }

    public void saveGoods(GoodsModel goods) {
        checkNull();
        ArrayList<GoodsModel> temps = mLiteOrm.query(new QueryBuilder<GoodsModel>(GoodsModel.class).where(GoodsModel.KEY_GRABID + "=?", new String[]{goods.grabId + ""}));
        if (TextUtil.isValidate(temps)) {
            GoodsModel tempGoods = temps.get(0);
            tempGoods.quantity += (goods.quantity);
            mLiteOrm.save(tempGoods);
        } else {
            mLiteOrm.save(goods);
        }
//        test1();
    }

    public ArrayList<GoodsModel> getAllGoods() {
        checkNull();
        return mLiteOrm.query(GoodsModel.class);
    }

    public void deleteGoodsById(int grabId) {
        checkNull();
        mLiteOrm.delete(new WhereBuilder(GoodsModel.class).equals(GoodsModel.KEY_GRABID, grabId));
    }

    public void deleteGoodsByGrabId(int grabId) {
        checkNull();
        mLiteOrm.delete(new WhereBuilder(GoodsModel.class).equals(GoodsModel.KEY_GRABID, grabId));
    }

    public void deleteAllGoods(ArrayList<GoodsModel> deleteGoods) {
        checkNull();
        mLiteOrm.delete(deleteGoods);
    }

    public void saveAllGoods(ArrayList<GoodsModel> goodsModels) {
        checkNull();
        mLiteOrm.insert(goodsModels);
    }

    public LiteOrm getDb() {
        checkNull();
        return mLiteOrm;
    }

    public boolean getGoodsNumber(int goodsId) {
        checkNull();
        ArrayList<GoodsModel> temps = mLiteOrm.query(new QueryBuilder<GoodsModel>(GoodsModel.class).where(GoodsModel.KEY_GRABID + "=?", new String[]{goodsId + ""}));
        if (TextUtil.isValidate(temps)) {
            return true;
        }
        return false;
    }

    public void clear() {
//        mLiteOrm.delete(GoodsModel.class);
        ArrayList<GoodsModel> temps = getAllGoods();
        if (TextUtil.isValidate(temps)) {
            for (GoodsModel temp : temps) {
                deleteGoodsByGrabId(temp.grabId);
            }
        }
//        Cursor cursor = mLiteOrm.getWritableDatabase().rawQuery("DELETE FROM '_grab_local_cache'", null);
        test1();
    }
}
