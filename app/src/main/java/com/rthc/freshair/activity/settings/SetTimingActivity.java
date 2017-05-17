package com.rthc.freshair.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.rthc.freshair.MyApplication;
import com.rthc.freshair.R;
import com.rthc.freshair.net.NetConst;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetTimingActivity extends AppCompatActivity {

    final int REQUEST_CODE_ON_OFF_TIME = 1;

    TextView time1TextView;
    TextView time2TextView;
    TextView time3TextView;
    TextView time4TextView;

    CheckBox checkBox1;
    CheckBox checkBox2;
    CheckBox checkBox3;
    CheckBox checkBox4;


    ArrayList<HashMap<String, Object>> timeList = new ArrayList<>();

    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_timing);

        mContext = this;

        initData();
        initView();
    }

    void initData() {
        for (int i = 0; i < 4; i++) {
            HashMap<String,Object> map = new HashMap<>();
            map.put("onTime","08:00:00");
            map.put("offTime","08:00:00");

            timeList.add(map);
        }
    }

    void initView() {
        time1TextView = (TextView) findViewById(R.id.time1TextView);
        time2TextView = (TextView) findViewById(R.id.time2TextView);
        time3TextView = (TextView) findViewById(R.id.time3TextView);
        time4TextView = (TextView) findViewById(R.id.time4TextView);

        checkBox1 = (CheckBox) findViewById(R.id.checkbox1);
        checkBox2 = (CheckBox) findViewById(R.id.checkbox2);
        checkBox3 = (CheckBox) findViewById(R.id.checkbox3);
        checkBox4 = (CheckBox) findViewById(R.id.checkbox4);
    }


    public void back(View view) {
        this.finish();
    }

    public void setTime1(View view) {
        Intent intent = new Intent(this, SetTimingSubActivity.class);
        intent.putExtra("onTime", timeList.get(0).get("onTime").toString());
        intent.putExtra("offTime", timeList.get(0).get("offTime").toString());
        intent.putExtra("index", 1);
        startActivityForResult(intent, REQUEST_CODE_ON_OFF_TIME);
    }

    public void setTime2(View view) {
        Intent intent = new Intent(this, SetTimingSubActivity.class);
        intent.putExtra("onTime", timeList.get(1).get("onTime").toString());
        intent.putExtra("offTime", timeList.get(1).get("offTime").toString());
        intent.putExtra("index", 2);
        startActivityForResult(intent, REQUEST_CODE_ON_OFF_TIME);
    }

    public void setTime3(View view) {
        Intent intent = new Intent(this, SetTimingSubActivity.class);
        intent.putExtra("onTime", timeList.get(2).get("onTime").toString());
        intent.putExtra("offTime", timeList.get(2).get("offTime").toString());
        intent.putExtra("index", 3);
        startActivityForResult(intent, REQUEST_CODE_ON_OFF_TIME);
    }

    public void setTime4(View view) {
        Intent intent = new Intent(this, SetTimingSubActivity.class);
        intent.putExtra("onTime", timeList.get(3).get("onTime").toString());
        intent.putExtra("offTime", timeList.get(3).get("offTime").toString());
        intent.putExtra("index", 4);
        startActivityForResult(intent, REQUEST_CODE_ON_OFF_TIME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ON_OFF_TIME) {
            if (resultCode == RESULT_OK) {
                String onTime = data.getStringExtra("onTime");
                String offTime = data.getStringExtra("offTime");
                int index = data.getIntExtra("index", 0);
                timeList.get(index).put("onTime",onTime);
                timeList.get(index).put("offTime",offTime);
                switch (index) {
                    case 1:
                        time1TextView.setText(String.format("%s-%s",onTime,offTime));
                        break;
                    case 2:
                        time2TextView.setText(String.format("%s-%s",onTime,offTime));
                        break;
                    case 3:
                        time3TextView.setText(String.format("%s-%s",onTime,offTime));
                        break;
                    case 4:
                        time4TextView.setText(String.format("%s-%s",onTime,offTime));
                        break;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    List getTimingFromView(){
        List<Map> list = new ArrayList<>();

        HashMap<String,Object> map = new HashMap<>();
        map.put("state",checkBox1.isChecked());
        map.put("onTime", time1TextView.getText().toString().split("-")[0]);
        map.put("offTime", time1TextView.getText().toString().split("-")[1]);
        list.add(map);

        map = new HashMap<>();
        map.put("state",checkBox2.isChecked());
        map.put("onTime", time2TextView.getText().toString().split("-")[0]);
        map.put("offTime", time2TextView.getText().toString().split("-")[1]);
        list.add(map);

        map = new HashMap<>();
        map.put("state",checkBox3.isChecked());
        map.put("onTime", time3TextView.getText().toString().split("-")[0]);
        map.put("offTime", time3TextView.getText().toString().split("-")[1]);
        list.add(map);

        map = new HashMap<>();
        map.put("state",checkBox4.isChecked());
        map.put("onTime", time4TextView.getText().toString().split("-")[0]);
        map.put("offTime", time4TextView.getText().toString().split("-")[1]);
        list.add(map);

        return list;
    }


    public void commitSet(View view){
        //TODO 提交更改 同步到服务器
        Intent intent = getIntent();
        String sn = intent.getStringExtra("sn");
        setTiming(sn);
    }

    void setTiming(String sn){
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_DEV_SET_TIMING;

        JSONArray jsonArray = new JSONArray(getTimingFromView());

        OkHttpUtils.get()
                .url(url)
                .addParams("token", MyApplication.USER_TOKEN_CURRENT)
                .addParams("sn", sn)
                .addParams("timing", jsonArray.toString())
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
