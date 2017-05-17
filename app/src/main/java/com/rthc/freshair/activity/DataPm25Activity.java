package com.rthc.freshair.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rthc.freshair.MyApplication;
import com.rthc.freshair.R;
import com.rthc.freshair.net.NetConst;
import com.rthc.freshair.util.AirQuality;
import com.rthc.freshair.util.Const;
import com.rthc.freshair.widget.RcLineChart;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataPm25Activity extends AppCompatActivity {

    Button last1HourBtn;
    Button last24HourBtn;

    RcLineChart rcLineChart;

    private final Context mContext = this;

    int[] colorGridLineY;
    Float[] valueAxisX = {0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f};
    Float[] valueAxisY = {0f, 35f, 75f, 115f, 150f, 250f, 500f, 600f};

    String[] valueStrAxisX = {"", "", "", "", "", "", "", "", "", ""};
    String[] valueStrAxisY = {"0", "35", "75", "115", "150", "250", "500", "600"};
    String[] lvlStrs = {"优", "良", "轻度污染", "中度污染", "重度污染", "严重污染", "爆表"};


    Float[] dataX = {0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f};
    Float[] dataY = {0f, 14f, 20f, 30f, 80f, 50f, 60f, 56f, 80f, 23f};

    String[] dataStrX = {"0----", "1----", "2----", "3----", "4----", "5----", "6----", "7----", "8----", "9----"};
    String[] dataStrY = {"0----", "1----", "2----", "3----", "4----", "5----", "6----", "7----", "8----", "9----"};


    List<Float> dataXList = new ArrayList<Float>();
    List<String> dataStrXList = new ArrayList<String>();

    List<Float> dataYList = new ArrayList<Float>();
    List<String> dataStrYList = new ArrayList<String>();

    String sn;

    public enum HistoryDataType {
        TYPE_1,
        TYPE_24
    }

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_pm25);

        context = this;

        initView();

        initData();
    }

    public void back(View view) {
        this.finish();
    }

    void initData() {

        Intent intent = getIntent();
        sn = intent.getStringExtra("sn");


        getHisData(sn, HistoryDataType.TYPE_1);
    }

    void initView() {
        rcLineChart = (RcLineChart) findViewById(R.id.rcLineChart);
        initChart();

        last1HourBtn = (Button) findViewById(R.id.last1HourBtn);
        last24HourBtn = (Button) findViewById(R.id.last24HourBtn);

        last1HourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                last1HourBtn.setBackgroundColor(Color.WHITE);
                last1HourBtn.setTextColor(getResources().getColor(R.color.orange_dark));
                last24HourBtn.setBackgroundColor(getResources().getColor(R.color.base_color_gray_blue_dark));
                last24HourBtn.setTextColor(Color.WHITE);
                getHisData(sn, DataPm25Activity.HistoryDataType.TYPE_1);
            }
        });
        last24HourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                last1HourBtn.setBackgroundColor(getResources().getColor(R.color.base_color_gray_blue_dark));
                last1HourBtn.setTextColor(Color.WHITE);
                last24HourBtn.setBackgroundColor(Color.WHITE);
                last24HourBtn.setTextColor(getResources().getColor(R.color.orange_dark));
                getHisData(sn, DataPm25Activity.HistoryDataType.TYPE_24);
            }
        });
    }

    void initChart() {

        int[] colorGridLineY = {
                Color.WHITE,
                getResources().getColor(R.color.you),
                getResources().getColor(R.color.liang),
                getResources().getColor(R.color.qingduwuran),
                getResources().getColor(R.color.midwuran),
                getResources().getColor(R.color.heavywuran),
                getResources().getColor(R.color.yanzhongwuran),
                getResources().getColor(R.color.baobiao)};
        this.colorGridLineY = colorGridLineY;

        rcLineChart.setParam("PM2.5", colorGridLineY, valueAxisX, valueStrAxisX, valueAxisY, valueStrAxisY);

        setChartData();
    }

    void setChartData() {
        Date date = new Date();


        for (int i = 0; i < 10; i++) {

            long time = date.getTime() / 1000 + i * 60;
            dataXList.add((float) (date.getTime() / 1000 + i * 60000));
            float value = (float) Math.random() * valueAxisY[valueAxisY.length - 1];
            dataYList.add(value);

            String state = AirQuality.getLvl( AirQuality.AirQualityType.Pm25, value).getLvlStr();
            dataStrYList.add(String.format(Locale.getDefault(), "%.1f " + state, dataYList.get(i)));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            dataStrXList.add(sdf.format(new Date(time * 1000)));
        }

        dataX = dataXList.toArray(new Float[]{});
        dataY = dataYList.toArray(new Float[]{});
        dataStrX = dataStrXList.toArray(new String[]{});
        dataStrY = dataStrYList.toArray(new String[]{});
        valueAxisX = dataXList.toArray(new Float[]{});

        valueAxisX = dataX;

        rcLineChart.setParam("PM2.5", colorGridLineY, valueAxisX, valueStrAxisX, valueAxisY, valueStrAxisY);

        rcLineChart.setData(dataX, dataStrX, dataY, dataStrY);
    }


    void getHisData(String sn, HistoryDataType historyDataType) {
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_DEVICE_HISTORY_DATA;

        OkHttpUtils.get()
                .url(url)
                .addParams("token", MyApplication.USER_TOKEN_CURRENT)
                .addParams("sn", sn)
                .addParams("type", historyDataType == HistoryDataType.TYPE_1 ? "1" : "2")
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
                                JSONArray data = result.getJSONArray("data");

                                Log.i(Const.TAG, "data length:" + data.length());
                                if (data.length() < 2) {
                                    Toast.makeText(DataPm25Activity.this, "数据不足，生成图表失败", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                dataXList.clear();
                                dataStrXList.clear();
                                dataYList.clear();
                                dataStrYList.clear();

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject item = (JSONObject) data.get(i);
                                    if(item.getString("pm25").isEmpty()){
                                        continue;
                                    }
                                    dataYList.add(Float.parseFloat(item.getString("pm25")));
                                    float value = Float.parseFloat(item.getString("pm25"));
                                    dataStrYList.add(String.format(Locale.getDefault(),"%.0f%s", value, AirQuality.getLvl( AirQuality.AirQualityType.Pm25,value).getLvlStr()));
                                    dataXList.add(Float.parseFloat(item.get("time").toString()));
                                    Date date = new Date(item.getLong("time"));
                                    dataStrXList.add(Const.DATE_TIME_FORMAT.format(date));
                                }

                                dataX = dataXList.toArray(new Float[]{});
                                dataY = dataYList.toArray(new Float[]{});
                                dataStrX = dataStrXList.toArray(new String[]{});
                                dataStrY = dataStrYList.toArray(new String[]{});
                                valueAxisX = dataXList.toArray(new Float[]{});

                                valueAxisX = dataX;

                                rcLineChart.setParam("PM2.5", colorGridLineY, valueAxisX, valueStrAxisX, valueAxisY, valueStrAxisY);

                                rcLineChart.setData(dataX, dataStrX, dataY, dataStrY);

                            } else {
                                Toast.makeText(context, result.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }



}
