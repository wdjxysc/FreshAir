package com.rthc.freshair.activity.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.rthc.freshair.R;
import com.rthc.freshair.deviceconnect.CmdManager;
import com.rthc.freshair.deviceconnect.handler.ConnectHandler;
import com.rthc.freshair.deviceconnect.handler.UpdateHandler;
import com.rthc.freshair.util.Const;
import com.rthc.freshair.util.NetTool;
import com.rthc.freshair.util.ToastTool;
import com.rthc.freshair.util.WifiAdmin;

import java.util.Date;
import java.util.List;

public class SetDeviceWifiParamActivity extends AppCompatActivity {
    EditText ssidEditText;
    EditText pwdEditText;

    CmdManager cmdManager;

    boolean isConnect;

    WifiAdmin wifiAdmin;

    WifiManager wifiMgr;

    Context context;

    Thread translateThr;

    String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_device_wifi_param);

        ssidEditText = (EditText) findViewById(R.id.ssidEditText);
        pwdEditText = (EditText) findViewById(R.id.pwdEditText);

        context = this;

        wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        List wifiConfigList = wifiMgr.getConfiguredNetworks();
        if (wifiConfigList != null) {
            int wifiState = wifiMgr.getWifiState();
            WifiInfo info = wifiMgr.getConnectionInfo();
            String wifiId = info != null ? info.getSSID() : null;

            if (wifiId != null) {
                if (wifiId.substring(0, 1).equals("\"") && wifiId.substring(wifiId.length() - 1, wifiId.length()).equals("\"")) {
                    wifiId = wifiId.substring(1, wifiId.length() - 1);
                }
                ssidEditText.setText(wifiId);
            }
        }

        wifiAdmin = new WifiAdmin(this);

        cmdManager = new CmdManager();
    }

    @Override
    protected void onResume() {
        super.onResume();

        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        if (wifiInfo.getSSID().contains("RTHC")) {
            connectDevice();
            String ssid = wifiInfo.getSSID();
            if (ssid.substring(0, 1).equals("\"") && ssid.substring(ssid.length() - 1, ssid.length()).equals("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            deviceId = ssid.split("_")[1];
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(isConnect) {
            cmdManager.close();
            isConnect = false;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isConnect) {
            cmdManager.close();
            isConnect = false;
        }

        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        if (wifiInfo.getSSID().contains("RTHC")) {
            wifiAdmin.disconnectWifi(wifiInfo.getNetworkId());
        }
    }

    public void connectDevice() {
        Toast.makeText(SetDeviceWifiParamActivity.this, "尝试连接设备...", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                cmdManager.connect(new ConnectHandler() {
                    @Override
                    public void failed() {
                        Log.i(Const.TAG, "failed: 连接失败");
                        isConnect = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SetDeviceWifiParamActivity.this, "连接设备失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void success() {
                        Log.i(Const.TAG, "success: 连接成功");
                        isConnect = true;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SetDeviceWifiParamActivity.this, "连接设备成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).start();
    }

    public void back(View view) {
        finish();
    }

    public void getSettingWifiPage(View view) {
        Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }

    public void setBtnOnClick(View view) {
        //TODO 设置设备的网络
        WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        List wifiConfigList = wifiMgr.getConfiguredNetworks();
        if (wifiConfigList == null) {
            Toast.makeText(SetDeviceWifiParamActivity.this, "请打开WiFi并连接设备热点", Toast.LENGTH_SHORT).show();
            return;
        }
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID() : null;
        for (Object config : wifiConfigList) {
            if (((WifiConfiguration) config).SSID.equals(wifiId)) {
                int wifiType = NetTool.getSecurity((WifiConfiguration) config);
                int wifiEncry = NetTool.getCipher((WifiConfiguration) config);
                String str = ((WifiConfiguration) config).preSharedKey;
//                Log.i(Const.TAG, config.toString());
                final String ssid = ssidEditText.getText().toString();
                final String key = pwdEditText.getText().toString();

                if (!isConnect) {
                    Toast.makeText(SetDeviceWifiParamActivity.this, "请连接设备再试", Toast.LENGTH_SHORT).show();
                    return;
                }

//                if(translateThr != null) {
//                    if (translateThr.getState() != Thread.State.TERMINATED) {
//                        Log.i(Const.TAG, "正在通讯，取消当前请求");
//                        return;
//                    }
//                }

                translateThr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean b = cmdManager.setDeviceWifiParam(3, 3, ssid, key, deviceId);
                        if (b) Log.i(Const.TAG, "run: " + "设置成功");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SetDeviceWifiParamActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                translateThr.start();

                break;
            }
        }
    }



    ProgressDialog updateProgressDialog;

    public void update(View view) {
        if(!isConnect) {
            Toast.makeText(SetDeviceWifiParamActivity.this, "设备未连接请重新连接", Toast.LENGTH_SHORT).show();
            return;
        }

        if(translateThr != null) {
            if (translateThr.getState() != Thread.State.TERMINATED) {
                Log.i(Const.TAG, "正在通讯，取消当前请求");
                return;
            }
        }

        updateProgressDialog = new ProgressDialog(this);
        updateProgressDialog.setProgress(0);
        updateProgressDialog.setTitle("升级中...");
        updateProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        updateProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.i(Const.TAG, "取消");
                cmdManager.cancelUpdate();
            }
        });
        updateProgressDialog.setMax(100);
        updateProgressDialog.show();

        translateThr = new Thread(new Runnable() {
            @Override
            public void run() {
                cmdManager.updateDevice(new UpdateHandler() {
                    @Override
                    public void success() {
                        Log.i(Const.TAG, "success: ");
                        ToastTool.ShowToast((Activity) context, "升级成功 设备正在重启...");
                        updateProgressDialog.dismiss();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                dialog.setTitle("升级成功！");
                                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }
                        });
                    }

                    @Override
                    public void failed(String message) {
                        Log.i(Const.TAG, "failed: " + message);
                        ToastTool.ShowToast((Activity) context, "升级失败:" + message);
                        updateProgressDialog.dismiss();
                    }

                    @Override
                    public void timeout() {
                        Log.i(Const.TAG, "timeout: ");
                        ToastTool.ShowToast((Activity) context, "超时");
                        updateProgressDialog.dismiss();
                    }

                    @Override
                    public void progress(float progress) {
                        Log.i(Const.TAG, "progress:  " + progress);
                        updateProgressDialog.setProgress((int)(progress*100));
                    }
                }, deviceId);
            }
        });

        translateThr.start();
    }


    public void setTime(View view){
        if(!isConnect) {
            Toast.makeText(SetDeviceWifiParamActivity.this, "设备未连接请重新连接", Toast.LENGTH_SHORT).show();
            return;
        }

        if(translateThr != null) {
            if (translateThr.getState() != Thread.State.TERMINATED) {
                Log.i(Const.TAG, "正在通讯，取消当前请求");
                return;
            }
        }


        translateThr = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean b = cmdManager.setDeviceTime(deviceId, new Date());
                if (b) Log.i(Const.TAG, "run: " + "设置成功");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SetDeviceWifiParamActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        translateThr.start();
    }
}
