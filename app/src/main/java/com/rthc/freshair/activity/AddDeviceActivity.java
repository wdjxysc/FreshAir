package com.rthc.freshair.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.rthc.freshair.MyApplication;
import com.rthc.freshair.R;
import com.rthc.freshair.net.NetConst;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class AddDeviceActivity extends AppCompatActivity {


    final int SCAN_REQUEST_CODE_SN = 1;
    final int SCAN_REQUEST_CODE_KEY = 2;

    EditText snEditText;
    EditText keyEditText;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        mContext = this;

        initView();
    }

    void initView() {
        snEditText = (EditText) findViewById(R.id.snEditText);
        keyEditText = (EditText) findViewById(R.id.keyEditText);
    }

    public void back(View view) {
        this.finish();
    }

    public void addDevice(View view) {

        if(snEditText.getText().toString().isEmpty()){
            Toast.makeText(this,"sn号不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if(keyEditText.getText().toString().isEmpty()){
            Toast.makeText(this,"key不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        addDevice();
    }

    public void startScanQrCode(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.snBtn:
                intent = new Intent(this, ScanQrcodeActivity.class);
                startActivityForResult(intent, SCAN_REQUEST_CODE_SN);
                break;
            case R.id.keyBtn:
                intent = new Intent(this, ScanQrcodeActivity.class);
                startActivityForResult(intent, SCAN_REQUEST_CODE_KEY);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SCAN_REQUEST_CODE_SN:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("code");
                    snEditText.setText(result);
                }
                break;
            case SCAN_REQUEST_CODE_KEY:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("code");
                    keyEditText.setText(result);
                }
                break;
            default:
                break;
        }
    }

    void addDevice() {
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_ADD_DEVICE;

        OkHttpUtils.get()
                .url(url)
                .addParams("token", MyApplication.USER_TOKEN_CURRENT)
                .addParams("sn", snEditText.getText().toString())
                .addParams("key", keyEditText.getText().toString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.i("okhttp", "failed" + id);

                        Toast.makeText(mContext, "添加设备失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("okhttp", "success" + id + "    " + response);

                        JSONTokener jsonParser = new JSONTokener(response);
                        JSONObject result;
                        try {
                            result = (JSONObject) jsonParser.nextValue();
                            if(result.getInt("status") == NetConst.API_RESULT_SUCCESS){

                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            }

                            Toast.makeText(mContext, result.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
