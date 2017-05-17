package com.rthc.freshair.activity;

import com.rthc.freshair.util.BytesTool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Administrator on 2016/7/27.
 *
 * @author wdjxysc
 */
public class SocketServerTest {

    public static void main(String[] args){

        try {
            System.out.println("start server... ");
            ServerSocket serverSocket = new ServerSocket(8888);

            System.out.println("listening :8888");
            while (true) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final Socket socket = serverSocket.accept();

                System.out.println("new client in " + socket.toString());

                startConnectClientThr(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startConnectClientThr(final Socket socket){
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();

                    while (socket.isConnected()){

                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        int in = inputStream.available();

                        if(in == 0){
                            continue;
                        }

                        byte[] data = new byte[in];
                        int real = inputStream.read(data);

                        System.out.println("real " + BytesTool.Bytes2HexString(data, data.length));
                        System.out.println(new String(data));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
