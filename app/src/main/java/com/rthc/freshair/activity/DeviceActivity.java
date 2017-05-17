package com.rthc.freshair.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rthc.freshair.MyApplication;
import com.rthc.freshair.R;
import com.rthc.freshair.activity.settings.DeviceSettingsActivity;
import com.rthc.freshair.net.NetConst;
import com.rthc.freshair.util.AirQuality;
import com.rthc.freshair.util.Const;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DeviceActivity extends AppCompatActivity {

    View pm25DataLayout;
    View pm10DataLayout;
    View hchoDataLayout;
    View tvocDataLayout;

    TextView timeTextView;
    TextView deviceNameTextView;

    Intent intent;

    Context context;

    String sn;
    String name;

    int REQUEST_CODE_DELETE_DEVICE =  1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        context = this;

        initView();

        initData();
    }

    public void back(View view) {
        this.finish();
    }

    public void startDeviceSetting(View view) {
        intent = new Intent(getApplication(), DeviceSettingsActivity.class);
        intent.putExtra("sn", sn);
        intent.putExtra("name", name);
        startActivityForResult(intent, REQUEST_CODE_DELETE_DEVICE);
    }

    void initView() {
        pm25DataLayout = findViewById(R.id.pm25DataLayout);
        pm10DataLayout = findViewById(R.id.pm10DataLayout);
        hchoDataLayout = findViewById(R.id.hchoDataLayout);
        tvocDataLayout = findViewById(R.id.tvocDataLayout);

        timeTextView = (TextView) findViewById(R.id.timeTextView);
        deviceNameTextView = (TextView) findViewById(R.id.deviceNameTextView);

        pm25DataLayout.setOnClickListener(onClickListener);
        pm10DataLayout.setOnClickListener(onClickListener);
        hchoDataLayout.setOnClickListener(onClickListener);
        tvocDataLayout.setOnClickListener(onClickListener);
    }

    void initData() {

        intent = getIntent();
        sn = intent.getStringExtra("sn");
        name = intent.getStringExtra("name");
        deviceNameTextView.setText(name);

        getLastData(sn);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.pm25DataLayout:
                    intent = new Intent(getApplication(), DataPm25Activity.class);
                    intent.putExtra("sn", sn);
                    startActivity(intent);
                    break;
                case R.id.pm10DataLayout:
                    intent = new Intent(getApplication(), DataPm10Activity.class);
                    intent.putExtra("sn", sn);
                    startActivity(intent);
                    break;
                case R.id.hchoDataLayout:
                    intent = new Intent(getApplication(), DataHchoActivity.class);
                    intent.putExtra("sn", sn);
                    startActivity(intent);
                    break;
                case R.id.tvocDataLayout:
                    intent = new Intent(getApplication(), DataTvocActivity.class);
                    intent.putExtra("sn", sn);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_DELETE_DEVICE && resultCode == RESULT_OK){
            finish();
        }
    }

    void getLastData(String sn) {
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_DEVICE_LAST_DATA;

        OkHttpUtils.get()
                .url(url)
                .addParams("token", MyApplication.USER_TOKEN_CURRENT)
                .addParams("sn", sn)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.i("okhttp", "failed" + id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("okhttp", "success" + id + "    " + response);

                        JSONTokener jsonParser = new JSONTokener(response);
                        JSONObject result;
                        try {
                            result = (JSONObject) jsonParser.nextValue();
                            if (result.getInt("status") == NetConst.API_RESULT_SUCCESS) {
                                JSONObject data = result.getJSONObject("data");
                                long time = data.getLong("time");

                                Date date = new Date(time);

                                String timeStr = Const.DATE_TIME_FORMAT.format(date);
                                timeTextView.setText(timeStr);

                                float pm25 = 0, pm10 = 0, hcho = 0, tvoc = 0;
                                if (!data.getString("pm25").isEmpty())
                                    pm25 = Float.parseFloat(data.getString("pm25"));
                                if (!data.getString("pm10").isEmpty())
                                    pm10 = Float.parseFloat(data.getString("pm10"));
                                if (!data.getString("hcho").isEmpty())
                                    hcho = Float.parseFloat(data.getString("hcho"));
                                if (!data.getString("tvoc").isEmpty())
                                    tvoc = Float.parseFloat(data.getString("tvoc"));

                                showData(pm25, pm10, hcho, tvoc);
                            } else {
                                Toast.makeText(context, result.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 显示数据
     *
     * @param pm25
     * @param pm10
     * @param hcho
     * @param tvoc
     */
    void showData(float pm25, float pm10, float hcho, float tvoc) {
        ArrayList<HashMap> list = new ArrayList<>();
        HashMap<String, Object> map;
        map = new HashMap<>();
        map.put("name", "PM2.5");
        map.put("unit", "ug/m³");
        map.put("value", String.format(Locale.getDefault(), "%.0f", pm25));
        map.put("color", AirQuality.getLvl(AirQuality.AirQualityType.Pm25, pm25).getColor());
        list.add(map);

        map = new HashMap<>();
        map.put("name", "PM10");
        map.put("unit", "ug/m³");
        map.put("value", String.format(Locale.getDefault(), "%.1f", pm10));
        map.put("color", AirQuality.getLvl(AirQuality.AirQualityType.Pm10, pm10).getColor());
        list.add(map);

        map = new HashMap<>();
        map.put("name", "HOHC");
        map.put("unit", "mg/m³");
        map.put("value", String.format(Locale.getDefault(), "%.1f", hcho));
        map.put("color", AirQuality.getLvl(AirQuality.AirQualityType.Hcho, hcho).getColor());
        list.add(map);

        map = new HashMap<>();
        map.put("name", "TVOC");
        map.put("unit", "mg/m³");
        map.put("value", String.format(Locale.getDefault(), "%.1f", tvoc));
        map.put("color", AirQuality.getLvl(AirQuality.AirQualityType.Tvoc, tvoc).getColor());
        list.add(map);


        View[] viewList = {pm25DataLayout, pm10DataLayout, hchoDataLayout, tvocDataLayout};
        for (int i = 0; i < viewList.length; i++) {
            TextView nameTV = (TextView) viewList[i].findViewById(R.id.nameTV);
            TextView unitTV = (TextView) viewList[i].findViewById(R.id.unitTV);
            TextView valueTV = (TextView) viewList[i].findViewById(R.id.valueTV);
            View colorLineView = viewList[i].findViewById(R.id.colorLineView);

            nameTV.setText(list.get(i).get("name").toString());
            unitTV.setText(list.get(i).get("unit").toString());
            valueTV.setText(list.get(i).get("value").toString());
            colorLineView.setBackgroundColor(Integer.valueOf(list.get(i).get("color").toString()));
        }
    }
}
