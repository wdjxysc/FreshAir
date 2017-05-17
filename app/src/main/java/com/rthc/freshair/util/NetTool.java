package com.rthc.freshair.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Administrator on 2016/7/28.
 *
 * @author wdjxysc
 */
public class NetTool {

    /**
     * 根据url获取图片
     * @param path
     * @return
     * @throws IOException
     */
    public static Bitmap getBitmap(String path) throws IOException {

        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if(conn.getResponseCode() == 200){
            InputStream inputStream = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }
        return null;
    }


    /**
     * 获取当前ssid的加密模式
     * @param context
     * @param ssid
     */
    public static int getCipherType(Context context, String ssid) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        List list = wifiManager.getScanResults();

        for (Object scResult : list) {

            ScanResult scanResult = (ScanResult) scResult;
            if (!TextUtils.isEmpty(scanResult.SSID) && scanResult.SSID.equals(ssid)) {
                String capabilities = scanResult.capabilities;
                Log.i("hefeng", "capabilities=" + capabilities);

                if (!TextUtils.isEmpty(capabilities)) {

                    if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                        Log.i("hefeng", "wpa");

                    } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                        Log.i("hefeng", "wep");
                    } else {
                        Log.i("hefeng", "no");
                    }
                }
            }
        }
        return -1;
    }



    /**
     * These values are matched in string arrays -- changes must be kept in sync
     */
    static final int SECURITY_NONE = 0;
    static final int SECURITY_WEP = 1;
    static final int SECURITY_PSK = 2;
    static final int SECURITY_EAP = 3;

    public static int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }

    static final int CIPHER_NONE = 0;
    static final int CIPHER_WEP_H = 1;
    static final int CIPHER_TKIP = 2;
    static final int CIPHER_AES = 3;

    public static int getCipher(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.GroupCipher.CCMP)) {
            return CIPHER_AES;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.GroupCipher.TKIP)) {
            return CIPHER_TKIP;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.GroupCipher.WEP104)) {
            return CIPHER_WEP_H;
        }
        return 0;
    }
}
