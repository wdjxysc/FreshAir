package com.rthc.freshair.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.rthc.freshair.MyApplication;
import com.rthc.freshair.R;
import com.rthc.freshair.activity.ReportFaultActivity;
import com.rthc.freshair.net.NetConst;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class DeviceSettingsActivity extends AppCompatActivity {

    String sn;
    String name;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_settings);

        mContext = this;

        Intent intent = getIntent();
        sn = intent.getStringExtra("sn");
        name = intent.getStringExtra("name");

//        downLoadFile("");
    }

    public void back(View view) {
        this.finish();
    }

    public void netSetting(View view) {
        Intent intent = new Intent(this, SetDeviceNetActivity.class);
        intent.putExtra("sn", sn);
        startActivity(intent);
    }

    public void setDeviceName(View view) {
        Intent intent = new Intent(this, SetDeviceNameActivity.class);
        intent.putExtra("sn", sn);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    public void setTiming(View view) {
        Intent intent = new Intent(this, SetTimingActivity.class);
        intent.putExtra("sn", sn);
        startActivity(intent);
    }

    public void setWarming(View view) {
        Intent intent = new Intent(this, SetWarmingActivity.class);
        intent.putExtra("sn", sn);
        startActivity(intent);
    }

    public void setNetInfo(View view) {
        Intent intent = new Intent(this, NetInfoActivity.class);
        intent.putExtra("sn", sn);
        startActivity(intent);
    }

    public void reportRepair(View view) {
        Intent intent = new Intent(this, ReportFaultActivity.class);
        intent.putExtra("sn", sn);
        startActivity(intent);
    }

    public void checkHardwareUpdate(View view) {
        Intent intent = new Intent(this, DeviceUpdateActivity.class);
        intent.putExtra("sn", sn);
        startActivity(intent);
    }

    public void deleteDevice(View view) {
        delete(sn);
    }

    void delete(String sn) {
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_DEV_DELETE;

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
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {

                            }
                            Toast.makeText(mContext, result.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }



}
