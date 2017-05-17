package com.rthc.freshair.deviceconnect;

import android.util.Log;

import com.rthc.freshair.deviceconnect.cmd.Cmd;
import com.rthc.freshair.deviceconnect.cmd.DataConst;
import com.rthc.freshair.deviceconnect.cmd.ParseData;
import com.rthc.freshair.deviceconnect.handler.ConnectHandler;
import com.rthc.freshair.deviceconnect.handler.UpdateHandler;
import com.rthc.freshair.deviceconnect.socket.DeviceConnect;
import com.rthc.freshair.util.BytesTool;
import com.rthc.freshair.util.Const;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/8/25.
 *
 * @author wdjxysc
 */
public class CmdManager {

    DeviceConnect deviceConnect;
    int timeout = 5000;

    public CmdManager() {
        deviceConnect = new DeviceConnect();
    }

    /**
     * 设置wifi参数
     *
     * @param authCode  密码类型
     * @param encryCode 加密方式
     * @param ssid      wifi名称
     * @param key       wifi密码
     * @return
     */
    public boolean setDeviceWifiParam(int authCode, int encryCode, String ssid, String key, String deviceId) {
        boolean b = false;
        HashMap resultMap = new HashMap();

        HashMap map = new HashMap();
        map.put(Cmd.KEY_DATA_TYPE, DataConst.DATA_TYPE_SET_WIFI_PARAM);
        map.put(Cmd.KEY_AUTH_TYPE, authCode);
        map.put(Cmd.KEY_ENCRY_TYPE, encryCode);
        map.put(Cmd.KEY_DEVICE_ID, deviceId);
        map.put(Cmd.KEY_WIFI_SSID, ssid);
        map.put(Cmd.KEY_WIFI_KEY, key);
        byte[] sendData = Cmd.AssembleCmd(map);

        Log.i("hehe", "setDeviceWifiParam: " + BytesTool.Bytes2HexString(sendData,sendData.length));

        if (sendData != null && sendData.length != 0) {
            byte[] recvData = deviceConnect.sendAndReceiveData(sendData, timeout);
            if (recvData != null) {
                resultMap = ParseData.ParseDataToMap(recvData);
                if (resultMap != null && resultMap.size() != 0) {
                    b = (Integer) resultMap.get(Cmd.KEY_RESULT) == 1;
                } else {
                    Log.i(Const.TAG, "setDeviceWifiParam: 返回数据解析失败");
                }
            } else {
                Log.i(Const.TAG, "setDeviceWifiParam: 返回数据为空");
            }
        } else {
            Log.i(Const.TAG, "setDeviceWifiParam: 获取数据失败");
        }

        return b;
    }

    public boolean deviceReset(String deviceId) {
        boolean b = false;
        HashMap resultMap = new HashMap();

        HashMap map = new HashMap();
        map.put(Cmd.KEY_DATA_TYPE, DataConst.DATA_TYPE_DEVICE_RESET);
        map.put(Cmd.KEY_DEVICE_ID, deviceId);
        byte[] sendData = Cmd.AssembleCmd(map);

        if (sendData != null && sendData.length != 0) {
            byte[] recvData = deviceConnect.sendAndReceiveData(sendData, timeout);
            if (recvData != null) {
                resultMap = ParseData.ParseDataToMap(recvData);
                if (resultMap != null && resultMap.size() != 0) {
                    b = (Integer) resultMap.get(Cmd.KEY_RESULT) == 1;
                } else {
//                    Log.i(Const.TAG, "setDeviceWifiParam: 返回数据解析失败");
                    System.out.println("setDeviceWifiParam: 返回数据解析失败");
                }
            } else {
//                Log.i(Const.TAG, "setDeviceWifiParam: 返回数据为空");
                System.out.println("setDeviceWifiParam: 返回数据为空");
            }
        } else {
//            Log.i(Const.TAG, "setDeviceWifiParam: 获取数据失败");
            System.out.println("setDeviceWifiParam: 获取数据失败");
        }

        return b;
    }

    public HashMap getDeviceVer(String deviceId) {
        HashMap resultMap = new HashMap();

        HashMap map = new HashMap();
        map.put(Cmd.KEY_DATA_TYPE, DataConst.DATA_TYPE_DEVICE_VER);
        map.put(Cmd.KEY_DEVICE_ID, deviceId);
        byte[] sendData = Cmd.AssembleCmd(map);

        if (sendData != null && sendData.length != 0) {
            byte[] recvData = deviceConnect.sendAndReceiveData(sendData, timeout);
            if (recvData != null) {
                resultMap = ParseData.ParseDataToMap(recvData);
                if (resultMap != null && resultMap.size() != 0) {
                    return resultMap;
                } else {
//                    Log.i(Const.TAG, "setDeviceWifiParam: 返回数据解析失败");
                    System.out.println("setDeviceWifiParam: 返回数据解析失败");
                }
            } else {
//                Log.i(Const.TAG, "setDeviceWifiParam: 返回数据为空");
                System.out.println("setDeviceWifiParam: 返回数据为空");
            }
        } else {
//            Log.i(Const.TAG, "setDeviceWifiParam: 获取数据失败");

            System.out.println("setDeviceWifiParam: 获取数据失败");
        }

        return null;
    }


    /**
     * 设置设备时间
     * @param deviceId
     * @param date
     * @return
     */
    public boolean setDeviceTime(String deviceId, Date date){
        boolean b = false;

        HashMap map = new HashMap();
        map.put(Cmd.KEY_DATA_TYPE, DataConst.DATA_TYPE_DEVICE_SET_TIME);
        map.put(Cmd.KEY_DEVICE_ID, deviceId);
        map.put(Cmd.KEY_DEVICE_TIME, date);
        byte[] sendData = Cmd.AssembleCmd(map);


        if (sendData != null && sendData.length != 0) {
            Log.i(Const.TAG, "setDeviceTime: " + BytesTool.Bytes2HexString(sendData,sendData.length));
            byte[] recvData = deviceConnect.sendAndReceiveData(sendData, timeout);
            if (recvData != null) {
                HashMap resultMap = ParseData.ParseDataToMap(recvData);
                if (resultMap != null && resultMap.size() != 0) {
                    b = (Integer) resultMap.get(Cmd.KEY_RESULT) == 1;
                } else {
//                    Log.i(Const.TAG, "setDeviceWifiParam: 返回数据解析失败");
                    System.out.println("setDeviceWifiParam: 返回数据解析失败");
                }
            } else {
//                Log.i(Const.TAG, "setDeviceWifiParam: 返回数据为空");
                System.out.println("setDeviceWifiParam: 返回数据为空");
            }
        } else {
//            Log.i(Const.TAG, "setDeviceWifiParam: 获取数据失败");
            System.out.println("setDeviceWifiParam: 获取数据失败");
        }

        return b;
    }




    UpdateHandler updateHandler;

    /**
     * 盒子固件升级
     *
     * @param updateHandler
     */
    public void updateDevice(UpdateHandler updateHandler, String deviceId) {
        this.updateHandler = updateHandler;

        Cmd.DeviceUpdate deviceUpdate = null;
        try {
            deviceUpdate = new Cmd.DeviceUpdate();
        } catch (Exception e) {
            updateHandler.failed(e.getMessage());
            e.printStackTrace();
            return;
        }

        if (deviceUpdate == null) return;

        HashMap map = getDeviceVer(deviceId);
        if (map != null && map.size() != 0) {
            if(Integer.parseInt(map.get(Cmd.KEY_RESULT).toString())  == 1) {
                int hwVerDevice = (Integer) map.get(Cmd.KEY_HW_VER);
                int swVerDevice = (Integer) map.get(Cmd.KEY_SW_VER);
                if(swVerDevice >= Integer.parseInt(deviceUpdate.softwareVersion)){
                    updateHandler.failed("设备固件版本已是最新版本 " + "--hw--" + hwVerDevice + "--sw--" + swVerDevice);
                    return;
                }
            }else {
                updateHandler.failed("失败");
                return;
            }
        }

        ArrayList<byte[]> arrayList = deviceUpdate.getUpdateDataList(deviceId);

        HashMap resultMap = null;

        if (arrayList == null) return;
        updating = true;
        for (int i = 0; i < arrayList.size(); i++) {

            if(!updating){
                updateHandler.failed("失败,停止升级");
                return;
            }

            byte[] revData;
            for (int j = 0; j < 3; j++) {
                Log.i("wdj", "write package:  " + i);
                revData = deviceConnect.sendAndReceiveData(arrayList.get(i), timeout);

                resultMap = ParseData.ParseDataToMap(revData);

                if (resultMap != null && resultMap.size() != 0) {
                    Log.i("wdj", "receive package: " + BytesTool.Bytes2HexString(revData,revData.length));
                    if (Integer.parseInt(resultMap.get(Cmd.KEY_RESULT).toString()) == 1) {
                        updateHandler.progress(((float) i) / arrayList.size());
                        break;
                    } else {
                        updateHandler.failed("失败");
                        return;
                    }
                } else {
                    Log.i("wdj", "receive package: 空");
                }

                if (j == 2) {
                    Log.i("wdj", "超时");
                    updateHandler.timeout();
                    return;
                }
            }
        }

        updateHandler.success();
    }

    boolean updating = false;
    public void cancelUpdate(){
        updating = false;
    }


    public void connect(ConnectHandler connectHandler) {
        deviceConnect.startConnectToDevice(connectHandler);
    }

    public void close() {
        deviceConnect.disconnectDevice();
    }
}
