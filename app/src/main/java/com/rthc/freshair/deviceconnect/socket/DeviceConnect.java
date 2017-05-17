package com.rthc.freshair.deviceconnect.socket;

import android.util.Log;

import com.rthc.freshair.deviceconnect.cmd.Cmd;
import com.rthc.freshair.deviceconnect.cmd.DataConst;
import com.rthc.freshair.deviceconnect.handler.ConnectHandler;
import com.rthc.freshair.util.BytesTool;
import com.rthc.freshair.util.Const;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Administrator on 2016/8/25.
 *
 * @author wdjxysc
 */
public class DeviceConnect {

        String ipStr = "8.8.8.1";
//    String ipStr = "127.0.0.1";
//    String ipStr = "192.168.0.56";

    int port = 8080;

    Socket socket;

    InputStream inputStream;

    OutputStream outputStream;

    boolean connect = false;

    byte[] sendData = new byte[0];
    byte[] receiveData = new byte[0];

    int nowDataLength = 0;

    int readSleepTime = 1;
    int readTimeOut = 1000;
    int oneDataReadTimes = 0;

    public void startConnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ipStr, port);

                    connect = true;
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                    while (connect) {
                        try {
                            Thread.sleep(readSleepTime);
                            oneDataReadTimes++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        int receiveSize = inputStream.available();

                        if (receiveSize < 10) continue;
                        System.out.println(receiveSize);
                        byte[] result = new byte[receiveSize];

                        int realSize = inputStream.read(result);
                        System.out.println(realSize);
                        if (receiveData.length == 0 && result[0] == DataConst.DATA_HEAD) {
                            //读到数据头
                            nowDataLength = result[1] & 0xff + (result[2] & 0xff) * 256;
                        }

                        receiveData = BytesTool.combineBytes(receiveData, result);

                        System.out.println("run: socket receive" + nowDataLength + "   " + BytesTool.Bytes2HexString(receiveData, receiveData.length));

                        if (nowDataLength == receiveData.length && receiveData[receiveData.length - 1] == DataConst.DATA_TAIL) {
                            //收到一条完整数据

                            System.out.println("run: socket receive 完整数据----" + BytesTool.Bytes2HexString(receiveData, receiveData.length));
                            receiveData = new byte[0];
                            oneDataReadTimes = 0;
                        } else {
                            if (oneDataReadTimes > readTimeOut / readSleepTime) {
                                //超时 舍弃掉这条数据
                                System.out.println("run: bad data ----" + BytesTool.Bytes2HexString(receiveData, receiveData.length));
                                receiveData = new byte[0];
                                oneDataReadTimes = 0;
                            }

                            continue;
                        }
                    }

                    inputStream.close();
                    outputStream.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 只负责连接
     */
    public void startConnectToDevice(final ConnectHandler connectHandler) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    try {
                        socket = new Socket();
                        socket.connect(new InetSocketAddress(ipStr, port), 3000);
                    } catch (IOException e) {

                        if (connectHandler != null) connectHandler.failed();
                        e.printStackTrace();
                        return;
                    }

                    connect = true;
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();

                    if (connectHandler != null) connectHandler.success();
                    while (socket.isConnected()) {

                    }

                    connect = false;
                    if (connectHandler != null) connectHandler.failed();

                    inputStream.close();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 断开连接
     */
    public void disconnectDevice() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
            connect = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送并接收数据
     *
     * @param bytes
     * @return
     */
    public byte[] sendAndReceiveData(byte[] bytes, int timeout) {
        byte[] resultData = null;

        try {
            outputStream.write(bytes);
            Log.i(Const.TAG, "sendData--"+ bytes.length +"--: " + BytesTool.Bytes2HexString(bytes, bytes.length));
            outputStream.flush();

            int sleepTime = 200;
            int maxTimes = timeout / sleepTime;

            for (int i = 0; i < maxTimes; i++) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int inSize = inputStream.available();

                if (inSize < 10) continue;
                Log.i(Const.TAG, "sendAndReceiveData: " + inSize);
                byte[] result = new byte[inSize];

                int realSize = inputStream.read(result);


                if (receiveData.length == 0 ) {
                    result = Cmd.filterByte(result,(byte) DataConst.DATA_HEAD);

                    //读到数据头
                    nowDataLength = result[1] & 0xff + (result[2] & 0xff) * 256;
                }

                receiveData = BytesTool.combineBytes(receiveData, result);

                Log.i(Const.TAG, "receiveData--"+ receiveData.length +"--: " + BytesTool.Bytes2HexString(receiveData, receiveData.length));

                if (nowDataLength == receiveData.length && receiveData[receiveData.length - 1] == DataConst.DATA_TAIL) {
                    //收到一条完整数据
//                    System.out.println("run: socket receive 完整数据 ----" + BytesTool.Bytes2HexString(receiveData, receiveData.length));
                    resultData = receiveData;
                    receiveData = new byte[0];
                    return resultData;
                } else {
                    if (i == maxTimes - 1) {
                        //超时 舍弃掉这条数据
//                        System.out.println("run: bad data 数据长度出错 ----" + BytesTool.Bytes2HexString(receiveData, receiveData.length));
                        Log.i(Const.TAG, "sendAndReceiveData: 数据长度出错 舍弃");
                        receiveData = new byte[0];
                        return null;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
