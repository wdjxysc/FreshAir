package com.rthc.freshair.activity.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rthc.freshair.MyApplication;
import com.rthc.freshair.R;
import com.rthc.freshair.deviceconnect.CmdManager;
import com.rthc.freshair.deviceconnect.handler.ConnectHandler;
import com.rthc.freshair.deviceconnect.handler.UpdateHandler;
import com.rthc.freshair.net.NetConst;
import com.rthc.freshair.util.Const;
import com.rthc.freshair.util.FileTool;
import com.rthc.freshair.util.ToastTool;
import com.rthc.freshair.util.WifiAdmin;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;

import okhttp3.Call;

public class DeviceUpdateActivity extends AppCompatActivity {
    Context mContext;

    Thread translateThr;

    String deviceId;

    CmdManager cmdManager;

    boolean isConnect;

    WifiAdmin wifiAdmin;

    WifiManager wifiMgr;

    Button updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_update);
        updateBtn = (Button) findViewById(R.id.updateBtn);

        mContext = this;


        wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiAdmin = new WifiAdmin(this);
        cmdManager = new CmdManager();

        String sn = getIntent().getStringExtra("sn");
        String binUrl = getIntent().getStringExtra("bin");

        checkHardwareVersion(sn);
    }

    void checkHardwareVersion(String sn) {
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_DEV_CHECK_VERSION;

        OkHttpUtils.get()
                .url(url)
                .addParams("token", MyApplication.USER_TOKEN_CURRENT)
                .addParams("sn", sn)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.i("okhttp", "failed" + id);

                        Toast.makeText(mContext, "设置失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("okhttp", "success" + id + "    " + response);

                        JSONTokener jsonParser = new JSONTokener(response);
                        JSONObject result;
                        try {
                            result = (JSONObject) jsonParser.nextValue();

                            if (result.getInt("status") == NetConst.API_RESULT_SUCCESS) {

                                final JSONObject data = result.getJSONObject("data");
                                if (data.getBoolean("haveNewVersion")) {
                                    Toast.makeText(mContext, "有新版本", Toast.LENGTH_SHORT).show();
                                    //TODO 下载硬件升级文件并对设备进行升级...

                                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                                    dialog.setTitle("检测到硬件新版本");
                                    dialog.setMessage("检测到硬件新版本，是否下载升级文件？");
                                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                            try {
                                                downLoadFile(data.getString("bin"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Log.i(Const.TAG, "取消下载");
                                        }
                                    });
                                    dialog.show();

                                } else {
                                    Toast.makeText(mContext, "已是最新版本", Toast.LENGTH_SHORT).show();
                                }

                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    void downLoadFile(String url) {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgress(0);
        dialog.setTitle("进度条窗口");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.i(Const.TAG, "取消");
                OkHttpUtils.getInstance().cancelTag(this);//取消以Activity.this作为tag的请求
            }
        });
        dialog.setMax(100);
        dialog.show();

        url = "https://raw.githubusercontent.com/getlantern/lantern-binaries/master/lantern-installer-beta.apk";

        String[] list = url.split("/");
        String fileName = list[list.length - 1];

        String path = Const.UPDATE_FILE_PATH;

        FileTool.deleteDictionaryFile(path);

        OkHttpUtils.get().
                url(url).
                tag(this).
                build().
                execute(new FileCallBack(Const.UPDATE_FILE_PATH, fileName) {

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        Log.i(Const.TAG, progress + "");
                        super.inProgress(progress, total, id);
                        dialog.setProgress((int) (progress * 100));
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialog.dismiss();
                        if (call.isCanceled()) {
                            Toast.makeText(DeviceUpdateActivity.this, "取消下载", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DeviceUpdateActivity.this, "下载出错", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        Log.i(Const.TAG, response.getName() + response.getPath() + response.length());
                        dialog.dismiss();
                    }
                });
    }




    ProgressDialog updateProgressDialog;

    public void update(View view) {

        if(!isConnect) {
            Toast.makeText(mContext, "设备未连接请重新连接", Toast.LENGTH_SHORT).show();
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
                        ToastTool.ShowToast((Activity) mContext, "升级成功 设备正在重启...");
                        updateProgressDialog.dismiss();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
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
                        ToastTool.ShowToast((Activity) mContext, "升级失败:" + message);
                        updateProgressDialog.dismiss();
                    }

                    @Override
                    public void timeout() {
                        Log.i(Const.TAG, "timeout: ");
                        ToastTool.ShowToast((Activity) mContext, "超时");
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

    public void back(View view) {
        finish();
    }

    public void getSettingWifiPage(View view) {
        Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
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
        Toast.makeText(mContext, "尝试连接设备...", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(mContext, "连接设备失败", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(mContext, "连接设备成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).start();
    }
}
