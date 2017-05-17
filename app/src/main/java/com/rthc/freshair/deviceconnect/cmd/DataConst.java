package com.rthc.freshair.deviceconnect.cmd;

/**
 * Created by Administrator on 2016/8/24.
 *
 * @author wdjxysc
 */
public class DataConst {


    public static int DATA_HEAD = 0x68;/*数据头*/
    public static int DATA_TAIL = 0x16;/*数据尾*/

    public static int DATA_TYPE_DEVICE_SET_TIME = 0x3026;//设置设备时间
    public static int DATA_TYPE_DEVICE_RESET = 0x3080;//设备复位
    public static int DATA_TYPE_DEVICE_VER = 0x3082;//查询设备硬件及软件版本信息

    public static int DATA_TYPE_UPDATE_CONTRACT = 0x3085;//升级约定帧
    public static int DATA_TYPE_UPDATE_DATA = 0x3086;//升级数据帧
    public static int DATA_TYPE_UPDATE_ADDRESS = 0x3087;//升级地址帧
    public static int DATA_TYPE_UPDATE_CHECK = 0x3088;//升级数据校验帧
    public static int DATA_TYPE_CURRENT_FRAME_SER = 0x3089;//获取设备帧序号

    public static int DATA_TYPE_UPDATE_STOP = 0x3090;//升级终止帧

    public static int DATA_TYPE_SET_WIFI_PARAM = 0x3096;/*设置WiFi参数*/
    public static int DATA_TYPE_HAND_SHAKE = 0x3095;/*设备连接握手包 设备做AP热点，APK成功连接设备AP后，每隔10S发送一次握手包。*/



    public static int DATA_SEND_FLAG_MASTER = 0;//主站发出
    public static int DATA_SEND_FLAG_SLAVE = 1;//从站发出

    public static int DATA_REQUEST_FLAG_REQUEST = 0;
    public static int DATA_REQUEST_FLAG_RESPONCE = 1;


    public static int DATA_RESPONCE_SUCCESS = 0;//成功响应码




    /*认证方式*/
    public static int AUTH_TYPE_OPEN = 0;
    public static int AUTH_TYPE_SHARED = 1;
    public static int AUTH_TYPE_WPAPSK = 2;
    public static int AUTH_TYPE_WAP2PSK = 3;

    /*加密方式*/
    public static int ENCRY_TYPE_NONE = 0;
    public static int ENCRY_TYPE_WEP_H = 1;
    public static int ENCRY_TYPE_TKIP = 2;
    public static int ENCRY_TYPE_AES = 3;



    /*响应码*/
    public static int DATA_RESPONSE_SUCCESS = 0x0000;



}
