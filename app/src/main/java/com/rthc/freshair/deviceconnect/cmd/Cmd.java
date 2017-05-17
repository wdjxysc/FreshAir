package com.rthc.freshair.deviceconnect.cmd;

import android.util.Log;

import com.rthc.freshair.util.BytesTool;
import com.rthc.freshair.util.CRC16;
import com.rthc.freshair.util.FileTool;
import com.rthc.freshair.util.StringTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Administrator on 2016/8/24.
 *
 * @author wdjxysc
 */
public class Cmd {

    /**
     * 报文ID   7字节 BCD
     */
    public static long SER_ID = 0x00;


    /**
     * 根据map组装要发送的命令
     *
     * @param map 对象封装命令
     * @return 返回命令byte数组
     */
    public static byte[] AssembleCmd(HashMap map) {
        DataBytes dataBytes = new DataBytes();

        int dataId = (Integer) map.get(KEY_DATA_TYPE);
        String deviceCode = map.get(KEY_DEVICE_ID).toString();

        String cmdStr = "";
        String dataStr = "";
        String length = "";

        SER_ID++;
        if (dataId == DataConst.DATA_TYPE_SET_WIFI_PARAM) {
            int auth_type = (Integer) map.get(KEY_AUTH_TYPE);
            int encry_type = (Integer) map.get(KEY_ENCRY_TYPE);
            String ssid = map.get(KEY_WIFI_SSID).toString();
            String key = map.get(KEY_WIFI_KEY).toString();

            byte[] ssidBytes = ssid.getBytes();
            byte[] keyBytes = key.getBytes();

            dataStr = String.format("%02X", auth_type).toUpperCase()
                    + String.format("%02X", encry_type).toUpperCase()
                    + StringTool.padRight(BytesTool.Bytes2HexString(ssidBytes, ssidBytes.length), 64, '0').toUpperCase()
                    + StringTool.padRight(BytesTool.Bytes2HexString(keyBytes, keyBytes.length), 128, '0').toUpperCase();

        } else if (dataId == DataConst.DATA_TYPE_HAND_SHAKE) {

        } else if (dataId == DataConst.DATA_TYPE_DEVICE_SET_TIME) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            Date date = (Date) map.get(KEY_DEVICE_TIME);
            dataStr = simpleDateFormat.format(date);
        } else if (dataId == DataConst.DATA_TYPE_DEVICE_RESET) {

        } else if (dataId == DataConst.DATA_TYPE_DEVICE_VER) {

        } else if (dataId == DataConst.DATA_TYPE_UPDATE_CONTRACT) {
            int updateSer = 0x0;
            int hwVer = (Integer) map.get(KEY_UPDATE_HW_VER);
            int swVer = (Integer) map.get(KEY_UPDATE_SW_VER);
            long fileSize = (Long) map.get(KEY_UPDATE_FILE_SIZE);
            int frameSizeType = (Integer) map.get(KEY_UPDATE_FRAME_SIZE);

            frameSizeType = 7;//暂时固定为128字节
            /*5 => 32字节
            6 => 64字节
            7 => 128字节
            8 => 256字节
            9 => 512 字节*/

            dataStr = StringTool.reversalHexString(String.format("%04X", updateSer))
                    + StringTool.padLeft(hwVer + "", 4, '0')
                    + StringTool.padLeft(swVer + "", 4, '0')
                    + StringTool.reversalHexString(String.format("%08X", fileSize))
                    + String.format("%02X", frameSizeType);

        } else if (dataId == DataConst.DATA_TYPE_UPDATE_DATA) {
            int updateSer = (Integer) map.get(KEY_UPDATE_FRAME_SER);
            dataStr = StringTool.reversalHexString(String.format("%04X", updateSer))
                    + map.get(KEY_UPDATE_FRAME_DATA);
        } else if (dataId == DataConst.DATA_TYPE_UPDATE_ADDRESS) {
            int updateSer = (Integer) map.get(KEY_UPDATE_FRAME_SER);
            long updateAddress = (Long) map.get(KEY_UPDATE_ADDRESS);
            dataStr = StringTool.reversalHexString(String.format("%04X", updateSer))
                    + StringTool.reversalHexString(String.format("%08X", updateAddress));
        } else if (dataId == DataConst.DATA_TYPE_UPDATE_CHECK) {
            int updateSer = (Integer) map.get(KEY_UPDATE_FRAME_SER);
            long checkSum = (Long) map.get(KEY_UPDATE_CHECKSUM);

            dataStr = StringTool.reversalHexString(String.format("%04X", updateSer))
                    + StringTool.reversalHexString(String.format("%08X", checkSum));
        } else if (dataId == DataConst.DATA_TYPE_UPDATE_STOP) {

        } else if (dataId == DataConst.DATA_TYPE_CURRENT_FRAME_SER) {

        }

        cmdStr = StringTool.padLeft(Integer.toHexString(DataConst.DATA_HEAD).toUpperCase(), 2, '0')
                + "0000"
                + StringTool.padLeft(String.format("%02X", dataId), 4, '0')
                + StringTool.padLeft(String.format("%02X", DataConst.DATA_SEND_FLAG_SLAVE), 2, '0')
                + StringTool.padLeft(String.format("%02X", DataConst.DATA_REQUEST_FLAG_REQUEST), 2, '0')
                + StringTool.padLeft(deviceCode, 14, '0')
                + StringTool.padLeft(SER_ID + "", 14, '0')
                + StringTool.reversalHexString(StringTool.padLeft(Integer.toHexString(dataStr.length() / 2).toUpperCase(), 4, '0'))
                + dataStr
                + "0000"
                + StringTool.padLeft(Integer.toHexString(DataConst.DATA_TAIL).toUpperCase(), 2, '0');

        //加入长度
        cmdStr = cmdStr.substring(0, 2)
                + StringTool.reversalHexString(String.format("%04X", cmdStr.length() / 2))
                + cmdStr.substring(6, cmdStr.length());
        //计算CRC
        int crc16 = CRC16.calcCrc16(BytesTool.HexString2Bytes(cmdStr.substring(0, cmdStr.length() - 6)));

        cmdStr = cmdStr.substring(0, cmdStr.length() - 6)
                + StringTool.reversalHexString(String.format("%04X", crc16))
                + cmdStr.substring(cmdStr.length() - 2, cmdStr.length());

        dataBytes.appendBytes(BytesTool.HexString2Bytes(cmdStr));
        return dataBytes.getData();
    }


    public static class DeviceUpdate {


        public int frameLength = 128;

        public String fileNamePart = "AirChk";

        public String fileName;

        public String softwareVersion = "0001";
        public String hardwareVersion = "0001";
        public long fileSize = 0;

        public String fileDirectory = "/puair/update/";


        public DeviceUpdate() throws Exception {

            String wholePath = FileTool.getSDPath() + fileDirectory;
//        String wholePath = FileTool.getExtSDCardPath().get(0) + fileDirectory;

            FileTool.createDir(wholePath);

            File[] files = FileTool.getFiles(wholePath);

            if (files.length == 0) {
                throw new Exception("升级文件不存在");
            }

            if (files.length != 0) {
                fileName = files[0].getAbsolutePath();
            }

            getInfo();
        }


        private void getInfo() {
//        String str = "/storage/sdcard0/switchbox_1000_1000_cryp.bin";

            String binFileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());

            String postfix = binFileName.split("\\.")[1];
            String name = binFileName.split("\\.")[0];

            try {
                hardwareVersion = name.split("_")[1];
                softwareVersion = name.split("_")[2];
            } catch (Exception ex) {
                Log.i("wdj", ex.getMessage());
            }


            fileSize = FileTool.getSize(fileName);

        }


        /**
         * 获取升级bin文件 转换为命令组
         *
         * @return 命令组
         */
        public ArrayList<byte[]> GetUpdateDataListFromFile() {
            ArrayList<byte[]> datalist = new ArrayList<byte[]>();

            try {
                FileInputStream fin = new FileInputStream(fileName);
                int length = fin.available();
                byte[] buffer = new byte[length];
                fin.read(buffer);

                int offset = 0;
                while (offset < buffer.length) {
                    byte[] data = new byte[]{};
                    if (buffer.length - offset >= frameLength) {
                        data = new byte[frameLength];
                        System.arraycopy(buffer, offset, data, 0, frameLength);
                    } else {
                        data = new byte[buffer.length - offset];
                        System.arraycopy(buffer, offset, data, 0, buffer.length - offset);
                    }
                    offset += frameLength;
                    datalist.add(data);
                }

                fin.close();
            } catch (IOException ex) {
                Log.i("ex", ex.getMessage());
            }

            return datalist;
        }


        /**
         * 获取设备升级数据列表
         *
         * @return
         */
        public ArrayList<byte[]> getUpdateDataList(String deviceId) {
            ArrayList<byte[]> updateDataList = new ArrayList<>();

            int updateSer = 0;

            int frameSizeCode = 7;//128字节

            HashMap map = new HashMap();
            map.put(KEY_DATA_TYPE, DataConst.DATA_TYPE_UPDATE_CONTRACT);
            map.put(KEY_DEVICE_ID,deviceId);
            map.put(KEY_UPDATE_FRAME_SER, updateSer);
            map.put(KEY_UPDATE_HW_VER, Integer.parseInt(hardwareVersion));
            map.put(KEY_UPDATE_SW_VER, Integer.parseInt(softwareVersion));
            map.put(KEY_UPDATE_FILE_SIZE, fileSize);
            map.put(KEY_UPDATE_FRAME_SIZE, frameSizeCode);
            updateDataList.add(AssembleCmd(map));
            updateSer++;



            long fileSumCheck = 0;
            ArrayList<byte[]> binDataList = GetUpdateDataListFromFile();

            for (int i = 0; i < binDataList.size(); i++) {
                map.clear();
                map.put(KEY_DATA_TYPE, DataConst.DATA_TYPE_UPDATE_DATA);
                map.put(KEY_UPDATE_FRAME_DATA, BytesTool.Bytes2HexString(binDataList.get(i), binDataList.get(i).length));
                map.put(KEY_DEVICE_ID,deviceId);
                map.put(KEY_UPDATE_FRAME_SER, updateSer);

                fileSumCheck += sum(binDataList.get(i));

                updateDataList.add(AssembleCmd(map));
                updateSer++;
            }


            map.clear();
            map.put(KEY_DEVICE_ID,deviceId);
            map.put(KEY_DATA_TYPE, DataConst.DATA_TYPE_UPDATE_CHECK);
            map.put(KEY_UPDATE_FRAME_SER, updateSer);
            map.put(KEY_UPDATE_CHECKSUM, fileSumCheck);
            updateDataList.add(AssembleCmd(map));

            map.clear();
            map.put(KEY_DEVICE_ID,deviceId);
            map.put(KEY_DATA_TYPE, DataConst.DATA_TYPE_DEVICE_RESET);
            updateDataList.add(AssembleCmd(map));

            return updateDataList;
        }


        private long sum(byte[] data) {
            long result = 0;

            if (data != null) {
                for (int i = 0; i < data.length; i++) {
                    result += (data[i] & 0xff);
                }
            }

            return result;
        }
    }

    /**
     * 到b为开始 过滤掉前面的字节
     * @param bytes
     * @param b
     * @return
     */
    public static byte[] filterByte(byte[] bytes, byte b){
        byte[] result= null;
        for (int i =0;i<bytes.length;i++){
            if(bytes[i] == b){
                result = new byte[bytes.length-i];
                System.arraycopy(bytes,i,result,0,bytes.length-i);
                return result;
            }
        }
        return null;
    }


    public static final String KEY_DATA_TYPE = "KEY_DATA_TYPE";
    public static final String KEY_DATA_ID = "KEY_DATA_ID";
    public static final String KEY_DEVICE_ID = "KEY_DEVICE_ID";
    public static final String KEY_AUTH_TYPE = "KEY_AUTH_TYPE";
    public static final String KEY_ENCRY_TYPE = "KEY_ENCRY_TYPE";
    public static final String KEY_WIFI_SSID = "KEY_WIFI_SSID";
    public static final String KEY_WIFI_KEY = "KEY_WIFI_KEY";
    public static final String KEY_DEVICE_TIME = "KEY_DEVICE_TIME";//设置设备时间

    public static final String KEY_HW_VER = "KEY_HW_VER";//设备硬件版本
    public static final String KEY_SW_VER = "KEY_SW_VER";//设备软件版本

    public static final String KEY_UPDATE_HW_VER = "KEY_UPDATE_HW_VER";//设备硬件版本
    public static final String KEY_UPDATE_SW_VER = "KEY_UPDATE_SW_VER";//设备软件版本
    public static final String KEY_UPDATE_FRAME_SER = "KEY_UPDATE_FRAME_SER";//升级帧序号
    public static final String KEY_UPDATE_FRAME_DATA = "KEY_UPDATE_FRAME_DATA";//升级帧数据
    public static final String KEY_UPDATE_FILE_SIZE = "KEY_UPDATE_FILE_SIZE";//升级文件长度
    public static final String KEY_UPDATE_FRAME_SIZE = "KEY_UPDATE_FRAME_SIZE";//升级每帧数据长度

    public static final String KEY_UPDATE_ADDRESS = "KEY_UPDATE_ADDRESS";//新固件存储地址
    public static final String KEY_UPDATE_CHECKSUM = "KEY_UPDATE_CHECKSUM";//新固件存储地址


    public static final String KEY_ERR_MESSAGE = "KEY_ERR_MESSAGE";
    public static final String KEY_RESULT = "KEY_RESULT";//0:失败 1:成功
    public static final String KEY_RESPONSE_CODE = "KEY_RESPONSE_CODE";
    public static final String KEY_RESPONSE_MESSAGE = "KEY_RESPONSE_MESSAGE";
    public static final String KEY_CURRENT_SER = "KEY_CURRENT_SER";//


    public static void main(String[] args) {
        HashMap map = new HashMap();
        map.put(Cmd.KEY_DATA_TYPE, DataConst.DATA_TYPE_SET_WIFI_PARAM);
        map.put(Cmd.KEY_AUTH_TYPE, 1);
        map.put(Cmd.KEY_ENCRY_TYPE, 1);
        map.put(Cmd.KEY_WIFI_SSID, "hahaha");
        map.put(Cmd.KEY_WIFI_KEY, "123456");

        byte[] data = AssembleCmd(map);
        Log.i("hehe", "main: " + BytesTool.Bytes2HexString(data,data.length));
    }

}
