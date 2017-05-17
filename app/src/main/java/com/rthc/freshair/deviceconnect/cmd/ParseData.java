package com.rthc.freshair.deviceconnect.cmd;

import com.rthc.freshair.util.BytesTool;
import com.rthc.freshair.util.CRC16;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/8/25.
 *
 * @author wdjxysc
 */
public class ParseData {

    /**
     * 对接收到的数据进行解析，返回map对象
     *
     * @param srcData 串口接收到的byte数组
     * @return map对象
     */
    public static HashMap<String, Object> ParseDataToMap(byte[] srcData) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        if (srcData == null) return map;

        byte[] data = null;
        for (int i = 0; i < srcData.length; i++) {
            if ((srcData[i] & 0xff) == DataConst.DATA_HEAD) {
                data = new byte[srcData.length - i];
                System.arraycopy(srcData, i, data, 0, srcData.length - i);
                break;
            }
        }

        if(data == null) return map;

        if ((data[0] & 0xff) == DataConst.DATA_HEAD && (data[data.length - 1] & 0xff) == DataConst.DATA_TAIL) {
            if (!CRC16.checkCrc16(data, 0, data.length - 1)) {
                System.out.println("crc16校验错误");
                map.put(Cmd.KEY_RESULT, 0);
                map.put(Cmd.KEY_ERR_MESSAGE, "crc16校验错误");
                return map;
            }

            int dataType = ((data[3] & 0xff) << 8) + (data[4] & 0xff);
            map.put(Cmd.KEY_DATA_TYPE, dataType);
            byte[] deviceIdBytes = new byte[7];
            System.arraycopy(data, 7, deviceIdBytes, 0, deviceIdBytes.length);
            String deviceId = BytesTool.Bytes2HexString(deviceIdBytes, deviceIdBytes.length);
            map.put(Cmd.KEY_DEVICE_ID, deviceId);

            byte[] dataIdBytes = new byte[7];
            System.arraycopy(data, 14, dataIdBytes, 0, dataIdBytes.length);
            String dataId = BytesTool.Bytes2HexString(deviceIdBytes, deviceIdBytes.length);
            map.put(Cmd.KEY_DATA_ID, dataId);

            byte[] responseBytes = new byte[]{data[23], data[24]};
            int responseCode = Integer.parseInt(BytesTool.Bytes2HexString(responseBytes, responseBytes.length));
            map.put(Cmd.KEY_RESPONSE_CODE, responseCode);
            map.put(Cmd.KEY_RESPONSE_MESSAGE, getMessageByResponseCode(responseCode));
            if (responseCode == 0) {
                map.put(Cmd.KEY_RESULT, 1);
            } else map.put(Cmd.KEY_RESULT, 0);

            if (dataType == DataConst.DATA_TYPE_SET_WIFI_PARAM) {

            } else if (dataType == DataConst.DATA_TYPE_HAND_SHAKE) {

            } else if (dataType == DataConst.DATA_TYPE_DEVICE_SET_TIME) {

            } else if (dataType == DataConst.DATA_TYPE_DEVICE_RESET) {

            } else if (dataType == DataConst.DATA_TYPE_DEVICE_VER) {
                byte[] hwBytes = new byte[]{data[25], data[26]};
                byte[] swBytes = new byte[]{data[27], data[28]};

                int hwVer = Integer.parseInt(BytesTool.Bytes2HexString(hwBytes, hwBytes.length));
                int swVer = Integer.parseInt(BytesTool.Bytes2HexString(swBytes, swBytes.length));
                map.put(Cmd.KEY_HW_VER, hwVer);
                map.put(Cmd.KEY_SW_VER, swVer);
            } else if (dataType == DataConst.DATA_TYPE_UPDATE_CONTRACT) {

            } else if (dataType == DataConst.DATA_TYPE_UPDATE_DATA) {

            } else if (dataType == DataConst.DATA_TYPE_UPDATE_ADDRESS) {

            } else if (dataType == DataConst.DATA_TYPE_UPDATE_CHECK) {

            } else if (dataType == DataConst.DATA_TYPE_UPDATE_STOP) {

            } else if (dataType == DataConst.DATA_TYPE_CURRENT_FRAME_SER) {
                byte[] currentSerBytes = new byte[]{data[24], data[25]};
                int currentSer = Integer.parseInt(BytesTool.Bytes2HexString(currentSerBytes, currentSerBytes.length));
                map.put(Cmd.KEY_CURRENT_SER, currentSer);
            }

        } else {
            System.out.print("数据错误");
        }

        return map;
    }

    static String getMessageByResponseCode(int code) {
        String str = "";

        switch (code) {
            case 0:
                str = "成功";
                break;
            case 101:
                str = "报文为空";
                break;
            case 102:
                str = "报文解密失败";
                break;
            case 103:
                str = "报文校验失败";
                break;
            case 104:
                str = "报文功能码为空";
                break;
            case 105:
                str = "报文功能码未知，指令不存在";
                break;
            case 106:
                str = "报文域不完整";
                break;
            case 107:
                str = "报文必添域空";
                break;
            case 108:
                str = "报文长度域不能解析为整数";
                break;
            case 109:
                str = "报文长度域数值与报文长度不等";
                break;
            case 110:
                str = "报文记录子域空";
                break;
            case 111:
                str = "报文记录子域中记录数与报文中的记录总数不等";
                break;
            case 112:
                str = "报文记录子域不完整";
                break;
            case 113:
                str = "报文日期、时间有误，不能正确解析";
                break;
            case 114:
                str = "报文ID为空";
                break;
            case 115:
                str = "从站编号为空";
                break;
            case 116:
                str = "BCD码格式转换错误";
                break;
            case 117:
                str = "数值格式转换错误";
                break;
            case 8001:
                str = "新固件硬件版本与设备硬件版本不一致";
                break;
            case 8002:
                str = "新固件软件版本与设备软件版本不一致";
                break;
            case 8003:
                str = "新固件文件长度异常";
                break;
            case 8004:
                str = "新固件存储地址异常";
                break;
            case 8005:
                str = "新固件帧序号异常";
                break;
            case 8006:
                str = "新固件数据校验异常";
                break;

        }

        return str;
    }
}
