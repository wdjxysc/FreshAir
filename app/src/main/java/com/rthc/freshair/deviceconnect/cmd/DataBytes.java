package com.rthc.freshair.deviceconnect.cmd;

import com.rthc.freshair.util.BytesTool;

/**
 * Created by Administrator on 2016/8/24.
 *
 * @author wdjxysc
 */
public class DataBytes {
    byte[] data;

    public DataBytes() {
        data = new byte[0];
    }

    public byte[] getData(){
        return data;
    }

    /**
     * 在已有byte数组后添加byte数组
     *
     * @param bytes
     */
    public DataBytes appendBytes(byte[] bytes) {
        data = BytesTool.combineBytes(data, bytes);
        return this;
    }

    /**
     * 设置已有byte数组对应位置的值为新的数组值
     * @param start
     * @param bytes
     * @return
     */
    public DataBytes replaceBytes(int start, byte[] bytes) {
        for (int i = start; i < start + bytes.length; i++) {
            data[i] = bytes[i - start];
        }
        return this;
    }

    public DataBytes subBytes(int start, int end){
        byte[] bytes = new byte[end-start];
        for (int i = start;i<end;i++){
            bytes[i-start] = data[i];
        }
        data = bytes;

        return this;
    }

}
