package com.rthc.freshair;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import com.rthc.freshair.db.DBManager;

/**
 * Created by Administrator on 2016/6/27.
 */
public class MyApplication extends Application {
    private Typeface typeface;
    private static MyApplication _instance;

    public DBManager getDbManager() {
        return dbManager;
    }

    public void setDbManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    private DBManager dbManager;

    /**
     * 当前登录用户 token
     */
    public static String USER_TOKEN_CURRENT = "";

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = (MyApplication) getApplicationContext();
//        typeface = Typeface.createFromAsset(_instance.getAssets(), "font/fz_shuizhu_jian.ttf");//字体文件在assets/font/ 下

        final Context context = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                dbManager = new DBManager(context);
            }
        }).start();

    }

    public static  MyApplication getInstance() {
        return _instance;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }
}
