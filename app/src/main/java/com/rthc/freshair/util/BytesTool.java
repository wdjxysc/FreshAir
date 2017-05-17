package com.rthc.freshair.util;

import android.util.Log;

import com.rthc.freshair.deviceconnect.cmd.DataBytes;

/**
 * Created by Administrator on 2016/8/24.
 *
 * @author wdjxysc
 */
public class BytesTool {

    /**
     * byte转换换为byte数组
     *
     * @param b
     * @return
     */
    public static byte[] ByteToBytes(byte b) {
        return new byte[]{b};
    }


    /**
     * 合并两个byte数组
     *
     * @param byte_1
     * @param byte_2
     * @return
     */
    public static byte[] combineBytes(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    /**
     * 反转一个byte数组
     *
     * @param bytes
     * @return
     */
    public static byte[] ReversalBytes(byte[] bytes) {
        byte[] resultBytes = new byte[bytes.length];

        for (int i = 0; i < bytes.length; i++) {
            resultBytes[i] = bytes[bytes.length - 1 - i];
        }

        return resultBytes;
    }

    /**
     * Bytes2HexString
     *
     * @param b
     * @param size
     * @return
     */
    public static String Bytes2HexString(byte[] b, int size) {
        String ret = "";
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    public static byte[] HexString2Bytes(String src) {
        int len = src.length() / 2;
        byte[] ret = new byte[len];
        byte[] tmp = src.getBytes();

        for (int i = 0; i < len; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    /* byte[] Int */
    public static int bytesToInt(byte[] bytes) {
        int addr = bytes[0] & 0xFF;
        addr |= ((bytes[1] << 8) & 0xFF00);
        addr |= ((bytes[2] << 16) & 0xFF0000);
        addr |= ((bytes[3] << 25) & 0xFF000000);
        return addr;

    }

    /* Int byte[] */
    public static byte[] intToByte(int i) {
        byte[] abyte0 = new byte[4];
        abyte0[0] = (byte) (0xff & i);
        abyte0[1] = (byte) ((0xff00 & i) >> 8);
        abyte0[2] = (byte) ((0xff0000 & i) >> 16);
        abyte0[3] = (byte) ((0xff000000 & i) >> 24);
        return abyte0;
    }


    /**
     * 和校验
     *
     * @param data 需要校验的数据
     * @return 校验结果
     */
    public static byte CheckSum(byte[] data) {
        byte sum = 0;

        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }

        return (byte) (sum % 256);
    }

    public static void main(String[] args) {

        DataBytes dataBytes = new DataBytes();
        dataBytes.appendBytes(new byte[]{0x12, 0x34, 0x56});
        dataBytes.replaceBytes(1, new byte[]{(byte) 0x89, 0x78});
        dataBytes.subBytes(1,3);

        String ssss = Bytes2HexString(dataBytes.getData(),dataBytes.getData().length);
        Log.i("afasfsadf", "main: " + ssss);
    }
}
