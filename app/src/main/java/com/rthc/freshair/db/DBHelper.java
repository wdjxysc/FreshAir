package com.rthc.freshair.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2015/9/21.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "freshair.db";
    private static final int DATABASE_VERSION = 1;

    //可以把数据库放在外部存储上
//    private static final String DATABASE_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/joke/" + DATABASE_NAME;

    Context mContext;

    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }


    /**
     * 数据库第一次被创建时onCreate会被调用
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        if (!tabIsExist("v_area", db)) {
            executeAssetsSQL(db, "areas.sql");
            // db.execSQL(sql);
            //System.out.println("创建表");
        }

        db.execSQL("CREATE TABLE IF NOT EXISTS person" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR, " +
                "age INTEGER, " +
                "info TEXT)");


        db.execSQL("CREATE TABLE IF NOT EXISTS METER" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "METER_ID VARCHAR," +       //表ID
                "NET_ID VARCHAR," +         //网络ID
                "METER_SERIAL_NUM VARCHAR," +  //表流水号
                "IS_ACTIVATED INTEGER," +  //是否激活启用 0:未激活 1:已激活
                "INSTALL_DATE DATETIME," +      //安装日期
                "PRODUCTION_DATE DATETIME)"); //生产日期

        db.execSQL("CREATE TABLE IF NOT EXISTS METER_DATA" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "METER_ID VARCHAR," +    //表ID
                "DATA_VALUE FLOAT," +    //表读数
                "DATA_TIME DATETIME," +  //数据时间
                "STATE_VALVE INTEGER," + //表阀门状态 0：关 1：开 2：异常
                "STATE_POWER_3_6_V INTEGER," + //3.6V电源 状态 0：低电压  1：正常
                "STATE_POWER_6_V INTEGER," +   //6V电源 状态 0：低电压  1：正常
                "POWER_VALUE FLOAT," + //电源电压 单位V
                "READ_DATA_TIME DATETIME)");    //抄表时间 抄表员抄表时间

        db.execSQL("CREATE TABLE IF NOT EXISTS HOUSE" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "BUILDINGS_ID VARCHAR," +    //小区编号
                "BUILDINGS_NAME VARCHAR," +  //小区名
                "BAN_ID VARCHAR," +          //楼栋
                "UNIT_ID VARCHAR," +         //单元
                "FLOOR VARCHAR," +           //楼层
                "HOUSE_INDEX VARCHAR," +     //房序
                "HOUSE_NUM VARCHAR," +       //门牌号
                "HOUSEHOLDER_NAME VARCHAR," +  //户主名
                "HOUSE_NET_ID VARCHAR," + //建档时输入的初始netId
                "METER_ID VARCHAR)");          //表ID
    }

    /**
     * 读取数据库文件（.sql），并执行sql语句
     * */
    private void executeAssetsSQL(SQLiteDatabase db, String schemaName) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(mContext.getAssets()
                    .open("schema" + "/" + schemaName)));

            //System.out.println("路径:" + Configuration.DB_PATH + "/" + schemaName);
            String line;
            String buffer = "";
            while ((line = in.readLine()) != null) {
                buffer += line;
                if (line.trim().endsWith(";")) {
                    db.execSQL(buffer.replace(";", ""));
                    buffer = "";
                }
            }
        } catch (IOException e) {
            Log.e("db-error", e.toString());
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                Log.e("db-error", e.toString());
            }
        }
    }


    /**
     * 判断是否存在某一张表
     * @param tabName
     * @param db
     * @return
     */
    public boolean tabIsExist(String tabName, SQLiteDatabase db) {
        boolean result = false;
        if (tabName == null) {
            return false;
        }
        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='" + tabName.trim() + "' ";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }

        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
     *
     * @param db         数据库
     * @param oldVersion 旧版本
     * @param newVersion 新版本
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            /**METER_DATA  增加 字段：
             "STATE_VALVE INTEGER," + //表阀门状态 0：关 1：开 2：异常
             "STATE_POWER_3_6_V INTEGER," + //3.6V电源 状态 0：低电压  1：正常
             "STATE_POWER_6_V INTEGER," +   //6V电源 状态 0：低电压  1：正常
             "POWER_VALUE FLOAT," + //电源电压 单位V
             */

            db.execSQL("alter table METER_DATA rename to METER_DATA_TEMP");
            db.execSQL("CREATE TABLE IF NOT EXISTS METER_DATA" +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "METER_ID VARCHAR," +    //表ID
                    "DATA_VALUE FLOAT," +    //表读数
                    "DATA_TIME DATETIME," +  //数据时间
                    "STATE_VALVE INTEGER," + //表阀门状态 0：关 1：开 2：异常
                    "STATE_POWER_3_6_V INTEGER," + //3.6V电源 状态 0：低电压  1：正常
                    "STATE_POWER_6_V INTEGER," +   //6V电源 状态 0：低电压  1：正常
                    "POWER_VALUE FLOAT," + //电源电压 单位V
                    "READ_DATA_TIME DATETIME)");    //抄表时间 抄表员抄表时间
            db.execSQL("insert into METER_DATA select _id, METER_ID, DATA_VALUE, DATA_TIME, 0, 0, 0, 0, READ_DATA_TIME from METER_DATA_TEMP");

            db.execSQL("drop table METER_DATA_TEMP");

            oldVersion = 2;
        }

        if(oldVersion == 2){
            /**HOUSE  增加 字段：
             "HOUSE_NET_ID VARCHAR," +      //建档时输入的初始netId
             */
            db.execSQL("alter table HOUSE rename to HOUSE_TEMP");
            db.execSQL("CREATE TABLE IF NOT EXISTS HOUSE" +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "BUILDINGS_ID VARCHAR," +    //小区编号
                    "BUILDINGS_NAME VARCHAR," +  //小区名
                    "BAN_ID VARCHAR," +          //楼栋
                    "UNIT_ID VARCHAR," +         //单元
                    "FLOOR VARCHAR," +           //楼层
                    "HOUSE_INDEX VARCHAR," +     //房序
                    "HOUSE_NUM VARCHAR," +       //门牌号
                    "HOUSEHOLDER_NAME VARCHAR," +  //户主名
                    "HOUSE_NET_ID VARCHAR," + //建档时输入的初始netId
                    "METER_ID VARCHAR)");          //表ID
            db.execSQL("insert into HOUSE select " +
                    "_id, BUILDINGS_ID, BUILDINGS_NAME, BAN_ID, UNIT_ID, FLOOR, HOUSE_INDEX, HOUSE_NUM, HOUSEHOLDER_NAME,' ',METER_ID from HOUSE_TEMP");//（注意' '是为新加的字段插入默认值的必须加上，否则就会出错）

            db.execSQL("drop table HOUSE_TEMP");

            oldVersion = 3;
        }
    }


    /**
     * version 1 表结构：
     "CREATE TABLE IF NOT EXISTS HOUSE" +
     "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
     "BUILDINGS_ID VARCHAR," +    //小区编号
     "BUILDINGS_NAME VARCHAR," +  //小区名
     "BAN_ID VARCHAR," +          //楼栋
     "UNIT_ID VARCHAR," +         //单元
     "FLOOR VARCHAR," +           //楼层
     "HOUSE_INDEX VARCHAR," +     //房序
     "HOUSE_NUM VARCHAR," +       //门牌号
     "HOUSEHOLDER_NAME VARCHAR," +  //户主名
     "METER_ID VARCHAR)"

     "CREATE TABLE IF NOT EXISTS METER_DATA" +
     "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
     "METER_ID VARCHAR," +    //表ID
     "DATA_VALUE FLOAT," +    //表读数
     "DATA_TIME DATETIME," +  //数据时间
     "READ_DATA_TIME DATETIME)"


     "CREATE TABLE IF NOT EXISTS METER" +
     "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
     "METER_ID VARCHAR," +       //表ID
     "NET_ID VARCHAR," +         //网络ID
     "METER_SERIAL_NUM VARCHAR," +  //表流水号
     "IS_ACTIVATED INTEGER," +  //是否激活启用 0:未激活 1:已激活
     "INSTALL_DATE DATETIME," +      //安装日期
     "PRODUCTION_DATE DATETIME)"
     *
     */


    /**
     * version 2 表结构：
     "CREATE TABLE IF NOT EXISTS HOUSE" +
     "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
     "BUILDINGS_ID VARCHAR," +    //小区编号
     "BUILDINGS_NAME VARCHAR," +  //小区名
     "BAN_ID VARCHAR," +          //楼栋
     "UNIT_ID VARCHAR," +         //单元
     "FLOOR VARCHAR," +           //楼层
     "HOUSE_INDEX VARCHAR," +     //房序
     "HOUSE_NUM VARCHAR," +       //门牌号
     "HOUSEHOLDER_NAME VARCHAR," +  //户主名
     "METER_ID VARCHAR)"

     "CREATE TABLE IF NOT EXISTS METER_DATA" +
     "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
     "METER_ID VARCHAR," +    //表ID
     "DATA_VALUE FLOAT," +    //表读数
     "DATA_TIME DATETIME," +  //数据时间
     "STATE_VALVE INTEGER," + //表阀门状态 0：关 1：开 2：异常
     "STATE_POWER_3_6_V INTEGER," + //3.6V电源 状态 0：低电压  1：正常
     "STATE_POWER_6_V INTEGER," +   //6V电源 状态 0：低电压  1：正常
     "POWER_VALUE FLOAT," + //电源电压 单位V
     "READ_DATA_TIME DATETIME)"


     "CREATE TABLE IF NOT EXISTS METER" +
     "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
     "METER_ID VARCHAR," +       //表ID
     "NET_ID VARCHAR," +         //网络ID
     "METER_SERIAL_NUM VARCHAR," +  //表流水号
     "IS_ACTIVATED INTEGER," +  //是否激活启用 0:未激活 1:已激活
     "INSTALL_DATE DATETIME," +      //安装日期
     "PRODUCTION_DATE DATETIME)"
     *
     */


    /**
     * version 3 表结构：
     "CREATE TABLE IF NOT EXISTS HOUSE" +
     "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
     "BUILDINGS_ID VARCHAR," +    //小区编号
     "BUILDINGS_NAME VARCHAR," +  //小区名
     "BAN_ID VARCHAR," +          //楼栋
     "UNIT_ID VARCHAR," +         //单元
     "FLOOR VARCHAR," +           //楼层
     "HOUSE_INDEX VARCHAR," +     //房序
     "HOUSE_NUM VARCHAR," +       //门牌号
     "HOUSEHOLDER_NAME VARCHAR," +  //户主名
     "HOUSE_NET_ID VARCHAR," +      //建档时输入的初始netId
     "METER_ID VARCHAR)"

     "CREATE TABLE IF NOT EXISTS METER_DATA" +
     "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
     "METER_ID VARCHAR," +    //表ID
     "DATA_VALUE FLOAT," +    //表读数
     "DATA_TIME DATETIME," +  //数据时间
     "STATE_VALVE INTEGER," + //表阀门状态 0：关 1：开 2：异常
     "STATE_POWER_3_6_V INTEGER," + //3.6V电源 状态 0：低电压  1：正常
     "STATE_POWER_6_V INTEGER," +   //6V电源 状态 0：低电压  1：正常
     "POWER_VALUE FLOAT," + //电源电压 单位V
     "READ_DATA_TIME DATETIME)"


     "CREATE TABLE IF NOT EXISTS METER" +
     "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
     "METER_ID VARCHAR," +       //表ID
     "NET_ID VARCHAR," +         //网络ID
     "METER_SERIAL_NUM VARCHAR," +  //表流水号
     "IS_ACTIVATED INTEGER," +  //是否激活启用 0:未激活 1:已激活
     "INSTALL_DATE DATETIME," +      //安装日期
     "PRODUCTION_DATE DATETIME)"
     *
     */

}
