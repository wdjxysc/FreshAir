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

public class ReportFaultActivity extends AppCompatActivity {
    Context mContext;

    EditText faultEditText;

    String sn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_fault);

        Intent intent = getIntent();
        sn = intent.getStringExtra("sn");

        mContext = this;

        faultEditText = (EditText) findViewById(R.id.faultEditText);
    }

    public void back(View view){
        this.finish();
    }

    public void commit(View view){
        reportFault(faultEditText.getText().toString());
    }


    void reportFault(String faultStr) {
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_REPORT_REPAIR;


        OkHttpUtils.get()
                .url(url)
                .addParams("token", MyApplication.USER_TOKEN_CURRENT)
                .addParams("sn", sn)
                .addParams("badParts", "检测器")
                .addParams("reason", "检测器磨损")
                .addParams("faultLevel", "1")
                .addParams("faultDescription", faultStr)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.i("okhttp", "failed" + id);

                        Toast.makeText(mContext, "提交失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("okhttp", "success" + id + "    " + response);

                        JSONTokener jsonParser = new JSONTokener(response);
                        JSONObject result;
                        try {
                            result = (JSONObject) jsonParser.nextValue();

                            if(result.getInt("status") == NetConst.API_RESULT_SUCCESS){

                            }
                            Toast.makeText(mContext, result.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
    }
}
