package com.rthc.freshair.activity;

import android.util.Log;

import com.rthc.freshair.deviceconnect.CmdManager;
import com.rthc.freshair.deviceconnect.cmd.Cmd;
import com.rthc.freshair.deviceconnect.cmd.DataConst;
import com.rthc.freshair.deviceconnect.handler.ConnectHandler;
import com.rthc.freshair.util.Const;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * Created by Administrator on 2016/7/7.
 */
public class Swap {
    public static void main(String[] args){

        byte[] bytes = new byte[]{0x23,0x56,0x68,(byte) 0x89};
        byte[] result = Cmd.filterByte(bytes,(byte) DataConst.DATA_HEAD);


        CmdManager cmdManager = new CmdManager();
        cmdManager.connect(new ConnectHandler() {
            @Override
            public void failed() {
                Log.i(Const.TAG, "failed: ");
            }

            @Override
            public void success() {
                Log.i(Const.TAG, "success: ");
            }
        });
        cmdManager.setDeviceWifiParam(3,3,"dsadfsa","123456","20011606000006");
        //authCode 3, encryCode 3,ssid "dsadfsa",key "123456",deviceid "20011606000006"
        //687C00309601002001160600000600000000000001620003036473616466736100000000000000000000000000000000000000000000000000313233343536000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000A8516

//        DeviceConnect deviceConnect = new DeviceConnect();
//        deviceConnect.startConnectToDevice();


//        startClient();


//        swap111();
    }


    public static void startClient(){
        try {
            Socket socket = new Socket("127.0.0.1", 8888);

            Log.i("socket",socket.getPort() + "");
            while (true){
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void swap111(){
        Integer a = 1,b = 2;
        System.out.println( a + "," + b);
        swap(a, b);
        System.out.println( a + "," + b);
    }

    public static void swap(Integer i1,Integer i2){
        Class<?> sss = Swap.class;

        Method[] list = sss.getMethods();

        try {
            Field field = Integer.class.getDeclaredField("value");
            field.setAccessible(true);
            int temp = i1.intValue();
            try {
                field.set(i1,i2.intValue());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            try {
                field.set(i2,new Integer(temp));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }
}
