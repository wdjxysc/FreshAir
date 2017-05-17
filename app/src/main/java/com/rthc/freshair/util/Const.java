package com.rthc.freshair.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Administrator on 2016/6/8.
 */
public class Const {



    public static final String TAG = "RTHC";

    /**
     * 日期时间格式
     */
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    /**
     * 日期格式
     */
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    /**
     * 时间格式
     */
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());


    /**
     * 下载的设备升级文件存放目录
     */
    public static final String UPDATE_FILE_PATH = FileTool.getSDPath() + "/puair/update";
}
