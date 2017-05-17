package com.rthc.freshair.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rthc.freshair.MyApplication;
import com.rthc.freshair.R;
import com.rthc.freshair.net.NetConst;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class SetDeviceNameActivity extends AppCompatActivity {

    Context mContext;

    EditText newNameEditText;
    TextView nameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_device_name);
        mContext = this;

        newNameEditText = (EditText) findViewById(R.id.newNameEditText);
        nameTV = (TextView) findViewById(R.id.nameTV);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        nameTV.setText(name);
    }

    public void back(View view){
        this.finish();
    }

    public void setDeviceName(View view){
        Intent intent = getIntent();
        String sn = intent.getStringExtra("sn");

        if(sn != null)
        setName(sn);
    }

    void setName(String sn){
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_DEV_SET_NAME;

        OkHttpUtils.get()
                .url(url)
                .addParams("token", MyApplication.USER_TOKEN_CURRENT)
                .addParams("sn", sn)
                .addParams("newName", newNameEditText.getText().toString())
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


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        Toast.makeText(mContext, "设置成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
    }
}
