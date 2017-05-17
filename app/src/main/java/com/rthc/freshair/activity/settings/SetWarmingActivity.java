package com.rthc.freshair.activity.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.rthc.freshair.MyApplication;
import com.rthc.freshair.R;
import com.rthc.freshair.net.NetConst;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class SetWarmingActivity extends AppCompatActivity {

    CheckBox checkBox;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_warming);

        mContext = this;

        checkBox =(CheckBox) findViewById(R.id.checkbox);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setWarning(getIntent().getStringExtra("sn"));
            }
        });
    }

    public void back(View view) {
        this.finish();
    }


    void setWarning(String sn){
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_DEV_SET_WARNING;

        OkHttpUtils.get()
                .url(url)
                .addParams("token", MyApplication.USER_TOKEN_CURRENT)
                .addParams("sn", sn)
                .addParams("exceptionNotify", checkBox.isChecked() + "")
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
                            if(result.getInt("status") == NetConst.API_RESULT_SUCCESS){
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
