package com.rthc.freshair.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.rthc.freshair.MyApplication;
import com.rthc.freshair.R;
import com.rthc.freshair.activity.settings.DeviceSettingsActivity;
import com.rthc.freshair.bean.HistoryData;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DataAllActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    SwipeRefreshLayout mSwipeLayout;

    ScrollView scrollView;

    View pm25DataLayout;
    View pm10DataLayout;
    View hchoDataLayout;
    View tvocDataLayout;

    TextView timeTextView;
    TextView deviceNameTextView;

    TextView dataTextView;
    TextView dataTitleTextView;
    TextView qltyTextView;
    TextView humidityTextView;
    TextView temperatureTextView;

    Button pm25Btn;
    Button pm10Btn;
    Button hchoBtn;
    Button tvocBtn;


    Button last1HourBtn;
    Button last24HourBtn;

    RcLineChart rcLineChart;

    ImageView imageView;


    Intent intent;

    Context context;

    String sn;//设备号
    String name;//设备名称

    float lastPm25 = 0, lastPm10 = 0, lastHcho = 0, lastTvoc = 0, lastTemperature = 0, lastHumidity = 0;

    int REQUEST_CODE_DELETE_DEVICE = 1;


    int[] colorGridLineY;

    List<HistoryData> historyDataList = new ArrayList<>();

    String lineTitle = "";
    Float[] valueAxisX = {0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f, 11f, 12f};
    Float[] valueAxisY = {0f, 50f, 150f, 250f, 350f, 420f, 600f};

    String[] valueStrAxisX = {"", "", "", "", "", "", "", "", "", "", "", "", ""};
    String[] valueStrAxisY = {"0", "50", "150", "250", "350", "420", "600"};

    List<Float> dataXList = new ArrayList<Float>();
    List<String> dataStrXList = new ArrayList<String>();

    List<Float> dataYList = new ArrayList<Float>();
    List<String> dataStrYList = new ArrayList<String>();

    Float[] dataX = {0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f};
    Float[] dataY = {0f, 14f, 20f, 30f, 80f, 50f, 60f, 56f, 80f, 23f};

    String[] dataStrX = {"0----", "1----", "2----", "3----", "4----", "5----", "6----", "7----", "8----", "9----"};
    String[] dataStrY = {"0----", "1----", "2----", "3----", "4----", "5----", "6----", "7----", "8----", "9----"};

    //当前选择显示的数据时间跨度
    HistoryDataType timeType = HistoryDataType.TYPE_1;

    public enum HistoryDataType {
        TYPE_1,
        TYPE_24
    }

    //当前选择显示的数据类型
    AirDataType selectedType = AirDataType.Pm25;

    public enum AirDataType {
        Pm25,
        Pm10,
        Hcho,
        Tvoc
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_all);

        context = this;

        initView();

        initData();

        showData(0, 0, 0, 0, 0, 0);
    }


    public void back(View view) {
        this.finish();
    }

    public void startDeviceSetting(View view) {
        intent = new Intent(getApplication(), DeviceSettingsActivity.class);
        intent.putExtra("sn", sn);
        startActivityForResult(intent, REQUEST_CODE_DELETE_DEVICE);
    }


    void initView() {

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);

        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeColors(R.color.base_color_gray_blue);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        pm25DataLayout = findViewById(R.id.pm25DataLayout);
        pm10DataLayout = findViewById(R.id.pm10DataLayout);
        hchoDataLayout = findViewById(R.id.hchoDataLayout);
        tvocDataLayout = findViewById(R.id.tvocDataLayout);

        timeTextView = (TextView) findViewById(R.id.timeTextView);
        deviceNameTextView = (TextView) findViewById(R.id.deviceNameTextView);

        dataTextView = (TextView) findViewById(R.id.dataTextView);
        dataTitleTextView = (TextView) findViewById(R.id.dataTitleTextView);
        qltyTextView = (TextView) findViewById(R.id.qltyTextView);
        humidityTextView = (TextView) findViewById(R.id.humidityTextView);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);

//        pm25DataLayout.setOnClickListener(onClickListener);
//        pm10DataLayout.setOnClickListener(onClickListener);
//        hchoDataLayout.setOnClickListener(onClickListener);
//        tvocDataLayout.setOnClickListener(onClickListener);


        rcLineChart = (RcLineChart) findViewById(R.id.rcLineChart);
        setChartParam(selectedType, timeType);

        imageView = (ImageView) findViewById(R.id.imageView);

        last1HourBtn = (Button) findViewById(R.id.last1HourBtn);
        last24HourBtn = (Button) findViewById(R.id.last24HourBtn);

        last1HourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                last1HourBtn.setBackgroundColor(Color.WHITE);
                last1HourBtn.setTextColor(getResources().getColor(R.color.orange_dark));
                last24HourBtn.setBackgroundColor(getResources().getColor(R.color.base_color_gray_blue_dark));
                last24HourBtn.setTextColor(Color.WHITE);
                getHisData(sn, HistoryDataType.TYPE_1);
            }
        });
        last24HourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                last1HourBtn.setBackgroundColor(getResources().getColor(R.color.base_color_gray_blue_dark));
                last1HourBtn.setTextColor(Color.WHITE);
                last24HourBtn.setBackgroundColor(Color.WHITE);
                last24HourBtn.setTextColor(getResources().getColor(R.color.orange_dark));
                getHisData(sn, HistoryDataType.TYPE_24);
            }
        });

        pm25Btn = (Button) findViewById(R.id.pm25Btn);
        pm10Btn = (Button) findViewById(R.id.pm10Btn);
        hchoBtn = (Button) findViewById(R.id.hchoBtn);
        tvocBtn = (Button) findViewById(R.id.tvocBtn);

        pm25Btn.setOnClickListener(onClickListener);
        pm10Btn.setOnClickListener(onClickListener);
        hchoBtn.setOnClickListener(onClickListener);
        tvocBtn.setOnClickListener(onClickListener);
    }


    void initData() {

        intent = getIntent();
        sn = intent.getStringExtra("sn");
        name = intent.getStringExtra("name");
        deviceNameTextView.setText(name);
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
                case R.id.pm25Btn:
                    chooseDataType(AirDataType.Pm25);
                    break;
                case R.id.pm10Btn:
                    chooseDataType(AirDataType.Pm10);
                    break;
                case R.id.hchoBtn:
                    chooseDataType(AirDataType.Hcho);
                    break;
                case R.id.tvocBtn:
                    chooseDataType(AirDataType.Tvoc);
                    break;
            }

            setChartParam(selectedType, timeType);
            setChartData(historyDataList, selectedType);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DELETE_DEVICE && resultCode == RESULT_OK) {
            finish();
        }
    }

    void choosePm25(){
        pm25Btn.setBackgroundColor(Color.WHITE);
        pm25Btn.setTextColor(getResources().getColor(R.color.orange_dark));
        pm10Btn.setBackgroundColor(getResources().getColor(R.color.base_color_gray_blue_dark));
        pm10Btn.setTextColor(Color.WHITE);
        hchoBtn.setBackgroundColor(getResources().getColor(R.color.base_color_gray_blue_dark));
        hchoBtn.setTextColor(Color.WHITE);
        tvocBtn.setBackgroundColor(getResources().getColor(R.color.base_color_gray_blue_dark));
        tvocBtn.setTextColor(Color.WHITE);

        dataTextView.setText(String.format(Locale.getDefault(), "%.1f", lastPm25));
        dataTitleTextView.setText("PM2.5");
        qltyTextView.setText(AirQuality.getLvl(AirQuality.AirQualityType.Pm25, lastPm25).getLvlStr());

        imageView.setImageResource(R.mipmap.reference_pm25);
    }

    void choosePm10(){
        pm10Btn.setBackgroundColor(Color.WHITE);
        pm10Btn.setTextColor(getResources().getColor(R.color.orange_dark));
        pm25Btn.setBackgroundColor(getResources().getColor(R.color.base_color_gray_blue_dark));
        pm25Btn.setTextColor(Color.WHITE);
        hchoBtn.setBackgroundColor(getResources().getColor(R.color.base_color_gray_blue_dark));
        hchoBtn.setTextColor(Color.WHITE);
        tvocBtn.setBackgroundColor(getResources().getColor(R.color.base_color_gray_blue_dark));
        tvocBtn.setTextColor(Color.WHITE);

        dataTextView.setText(String.format(Locale.getDefault(), "%.0f", lastPm10));
        dataTitleTextView.setText("PM10");
        qltyTextView.setText(AirQuality.getLvl(AirQuality.AirQualityType.Pm10, lastPm10).getLvlStr());

        imageView.setImageResource(R.mipmap.reference_pm10);
    }

    void chooseHcho(){
        hchoBtn.setBackgroundColor(Color.WHITE);
        hchoBtn.setTextColor(getResources().getColor(R.color.orange_dark));
        pm10Btn.setBackgroundColor(getResources().getColor(R.color.base_color_gray_blue_dark));
        pm10Btn.setTextColor(Color.WHITE);
        pm25Btn.setBackgroundColor(getResources().getColor(R.color.base_color_gray_blue_dark));
        pm25Btn.setTextColor(Color.WHITE);
        tvocBtn.setBackgroundColor(getResources().getColor(R.color.base_color_gray_blue_dark));
        tvocBtn.setTextColor(Color.WHITE);

        dataTextView.setText(String.format(Locale.getDefault(), "%.2f", lastHcho));
        dataTitleTextView.setText("HCHO");
        qltyTextView.setText(AirQuality.getLvl(AirQuality.AirQualityType.Hcho, lastHcho).getLvlStr());

        imageView.setImageResource(R.mipmap.reference_hcho);
    }

    void chooseTvoc(){
        tvocBtn.setBackgroundColor(Color.WHITE);
        tvocBtn.setTextColor(getResources().getColor(R.color.orange_dark));
        pm10Btn.setBackgroundColor(getResources().getColor(R.color.base_color_gray_blue_dark));
        pm10Btn.setTextColor(Color.WHITE);
        hchoBtn.setBackgroundColor(getResources().getColor(R.color.base_color_gray_blue_dark));
        hchoBtn.setTextColor(Color.WHITE);
        pm25Btn.setBackgroundColor(getResources().getColor(R.color.base_color_gray_blue_dark));
        pm25Btn.setTextColor(Color.WHITE);

        dataTextView.setText(String.format(Locale.getDefault(), "%.2f", lastTvoc));
        dataTitleTextView.setText("TVOC");
        qltyTextView.setText(AirQuality.getLvl(AirQuality.AirQualityType.Tvoc, lastTvoc).getLvlStr());

        imageView.setImageResource(R.mipmap.reference_tvoc);
    }

    void chooseDataType(AirDataType airDataType){
        selectedType = airDataType;
        switch (airDataType){
            case Pm25:
                choosePm25();
                break;
            case Pm10:
                choosePm10();
                break;
            case Hcho:
                chooseHcho();
                break;
            case Tvoc:
                chooseTvoc();
                break;
        }
    }

    /**
     * 获取指定设备的最近一组数据
     *
     * @param sn
     */
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
                        mSwipeLayout.setRefreshing(false);
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
                                if(data.length() == 0)
                                    Toast.makeText(DataAllActivity.this, "无数据", Toast.LENGTH_SHORT).show();
                                long time = data.getLong("time");

                                Date date = new Date(time);

                                String timeStr = Const.DATE_TIME_FORMAT.format(date);
                                timeTextView.setText(timeStr);

                                float pm25 = 0, pm10 = 0, hcho = 0, tvoc = 0, temperature = 0, humidity = 0;
                                if (!data.getString("pm25").isEmpty())
                                    pm25 = Float.parseFloat(data.getString("pm25"));
                                if (!data.getString("pm10").isEmpty())
                                    pm10 = Float.parseFloat(data.getString("pm10"));
                                if (!data.getString("hcho").isEmpty())
                                    hcho = Float.parseFloat(data.getString("hcho"));
                                if (!data.getString("tvoc").isEmpty())
                                    tvoc = Float.parseFloat(data.getString("tvoc"));
                                if (!data.getString("temperature").isEmpty())
                                    temperature = Float.parseFloat(data.getString("temperature"));
//                                if (!data.getString("humidity").isEmpty())
//                                    humidity = Float.parseFloat(data.getString("humidity"));

                                lastPm25 = pm25;
                                lastPm10 = pm10;
                                lastHcho = hcho;
                                lastTvoc = tvoc;
                                lastTemperature = temperature;
                                lastHumidity = humidity;
                                chooseDataType(selectedType);
                                showData(pm25, pm10, hcho, tvoc, temperature, humidity);
                            } else {
                                Toast.makeText(context, result.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mSwipeLayout.setRefreshing(false);
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
    void showData(float pm25, float pm10, float hcho, float tvoc, float temperature, float humidity) {
        ArrayList<HashMap> list = new ArrayList<>();
        HashMap<String, Object> map;
        map = new HashMap<>();
        map.put("name", "PM2.5");
        map.put("unit", "ug/m³");
        map.put("value", String.format(Locale.getDefault(), "%.1f", pm25));
        map.put("color", AirQuality.getLvl(AirQuality.AirQualityType.Pm25, pm25).getColor());
        list.add(map);

        map = new HashMap<>();
        map.put("name", "PM10");
        map.put("unit", "ug/m³");
        map.put("value", String.format(Locale.getDefault(), "%.1f", pm10));
        map.put("color", AirQuality.getLvl(AirQuality.AirQualityType.Pm10, pm10).getColor());
        list.add(map);

        map = new HashMap<>();
        map.put("name", "HCHO");
        map.put("unit", "mg/m³");
        map.put("value", String.format(Locale.getDefault(), "%.2f", hcho));
        map.put("color", AirQuality.getLvl(AirQuality.AirQualityType.Hcho, hcho).getColor());
        list.add(map);

        map = new HashMap<>();
        map.put("name", "TVOC");
        map.put("unit", "mg/m³");
        map.put("value", String.format(Locale.getDefault(), "%.2f", tvoc));
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

        String pm25Str = String.format(Locale.getDefault(),"%.1f",pm25);
        dataTextView.setText(pm25Str);

        String humidityStr = getResources().getString(R.string.humidity) + ":" + String.format(Locale.getDefault(),"%.0f",humidity) + "%";
        humidityTextView.setText(humidityStr);

        String temperatureStr = getResources().getString(R.string.temperature) + ":" + String.format(Locale.getDefault(),"%.1f",temperature) + "℃";
        temperatureTextView.setText(temperatureStr);
    }

    /**
     * 设置曲线图参数
     *
     * @param airDataType
     * @param timeType
     */
    void setChartParam(AirDataType airDataType, HistoryDataType timeType) {
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


        switch (airDataType) {
            case Pm25:
                valueAxisY = new Float[]{0f, 35f, 75f, 115f, 150f, 250f, 500f, 600f};
                valueStrAxisY = new String[]{"0", "35", "75", "115", "150", "250", "500", "600"};
                lineTitle = "PM2.5";
                break;
            case Pm10:
                valueAxisY = new Float[]{0f, 50f, 150f, 250f, 350f, 420f, 600f};
                valueStrAxisY = new String[]{"0", "50", "150", "250", "350", "420", "600"};
                lineTitle = "PM10";
                break;
            case Hcho:
                valueAxisY = new Float[]{0f, 0.03f, 0.08f, 0.16f, 0.3f, 0.8f};
                valueStrAxisY = new String[]{"0", "0.03", "0.08", "0.16", "0.3", "0.8"};
                lineTitle = "HCHO";
                break;
            case Tvoc:
                valueAxisY = new Float[]{0f, 0.6f, 1.0f, 1.6f};
                valueStrAxisY = new String[]{"0", "0.6", "1.0", "1.6"};
                lineTitle = "TVOC";
                break;
        }

        switch (timeType) {
            case TYPE_1:
                valueAxisX = new Float[]{0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f, 11f, 12f};
                List<Float> axisX1 = new ArrayList<Float>();
                axisX1.add(Float.parseFloat((new Date()).getTime() - 60 * 60 + ""));
                for (int i = 0; i < 12; i++) {
                    axisX1.add(Float.parseFloat((new Date()).getTime() - (11 - i) * 5 * 60 + ""));
                }
                valueAxisX = axisX1.toArray(new Float[]{});
                valueStrAxisX = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", ""};
                break;
            case TYPE_24:
                valueAxisX = new Float[]{0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f, 11f, 12f};
                List<Float> axisX24 = new ArrayList<Float>();
                axisX24.add(Float.parseFloat((new Date()).getTime() - 24 * 60 * 60 + ""));
                for (int i = 0; i < 24; i++) {
                    axisX24.add(Float.parseFloat((new Date()).getTime() - (11 - i) * 1 * 60 * 60 + ""));
                }
                valueAxisX = axisX24.toArray(new Float[]{});
                valueStrAxisX = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", ""};
                break;
        }
        rcLineChart.setParam(lineTitle, colorGridLineY, valueAxisX, valueStrAxisX, valueAxisY, valueStrAxisY);
    }


    /**
     * 从后台获取指定设备历史数据
     *
     * @param sn
     * @param historyDataType
     */
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
//                                if (data.length() < 2) {
//                                    Toast.makeText(context, "数据不足，生成图表失败", Toast.LENGTH_SHORT).show();
//                                    return;
//                                }

                                dataXList.clear();
                                dataStrXList.clear();
                                dataYList.clear();
                                dataStrYList.clear();

                                historyDataList.clear();

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject item = (JSONObject) data.get(i);

                                    HistoryData historyData = new HistoryData();
                                    historyData.setPm25(item.getString("pm25"));
                                    historyData.setPm10(item.getString("pm10"));
                                    historyData.setHcho(item.getString("hcho"));
                                    historyData.setTvoc(item.getString("tvoc"));
                                    historyData.setTemperature(item.getString("temperature"));
                                    historyData.setTime(item.getLong("time"));

                                    historyDataList.add(historyData);
                                }


                                setChartParam(selectedType, timeType);
                                setChartData(historyDataList, selectedType);

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
     * 设置曲线图数据
     *
     * @param historyDataList
     * @param dataType
     */
    void setChartData(List<HistoryData> historyDataList, AirDataType dataType) {
        dataXList.clear();
        dataStrXList.clear();
        dataYList.clear();
        dataStrYList.clear();

        switch (dataType) {
            case Pm25:
                for (int i = 0; i < historyDataList.size(); i++) {
                    HistoryData item = historyDataList.get(i);
                    if (item.getPm25().isEmpty()) {
                        continue;
                    }

                    dataYList.add(Float.parseFloat(item.getPm25()));
                    float value = Float.parseFloat(item.getPm25());
                    dataStrYList.add(String.format(Locale.getDefault(), "%.0f%s", value, AirQuality.getLvl(AirQuality.AirQualityType.Pm25, value).getLvlStr()));
                    dataXList.add(Float.parseFloat(item.getTime().toString()));
                    Date date = new Date(item.getTime());
                    dataStrXList.add(Const.DATE_TIME_FORMAT.format(date));
                }
                break;
            case Pm10:
                for (int i = 0; i < historyDataList.size(); i++) {
                    HistoryData item = historyDataList.get(i);
                    if (item.getPm10().isEmpty()) {
                        continue;
                    }

                    dataYList.add(Float.parseFloat(item.getPm10()));
                    float value = Float.parseFloat(item.getPm10());
                    dataStrYList.add(String.format(Locale.getDefault(), "%.0f%s", value, AirQuality.getLvl(AirQuality.AirQualityType.Pm10, value).getLvlStr()));
                    dataXList.add(Float.parseFloat(item.getTime().toString()));
                    Date date = new Date(item.getTime());
                    dataStrXList.add(Const.DATE_TIME_FORMAT.format(date));
                }
                break;
            case Hcho:
                for (int i = 0; i < historyDataList.size(); i++) {
                    HistoryData item = historyDataList.get(i);
                    if (item.getHcho().isEmpty()) {
                        continue;
                    }

                    dataYList.add(Float.parseFloat(item.getHcho()));
                    float value = Float.parseFloat(item.getHcho());
                    dataStrYList.add(String.format(Locale.getDefault(), "%.1f%s", value, AirQuality.getLvl(AirQuality.AirQualityType.Hcho, value).getLvlStr()));
                    dataXList.add(Float.parseFloat(item.getTime().toString()));
                    Date date = new Date(item.getTime());
                    dataStrXList.add(Const.DATE_TIME_FORMAT.format(date));
                }
                break;
            case Tvoc:
                for (int i = 0; i < historyDataList.size(); i++) {
                    HistoryData item = historyDataList.get(i);
                    if (item.getTvoc().isEmpty()) {
                        continue;
                    }

                    dataYList.add(Float.parseFloat(item.getTvoc()));
                    float value = Float.parseFloat(item.getTvoc());
                    dataStrYList.add(String.format(Locale.getDefault(), "%.1f%s", value, AirQuality.getLvl(AirQuality.AirQualityType.Tvoc, value).getLvlStr()));
                    dataXList.add(Float.parseFloat(item.getTime().toString()));
                    Date date = new Date(item.getTime());
                    dataStrXList.add(Const.DATE_TIME_FORMAT.format(date));
                }
                break;
        }

        dataX = dataXList.toArray(new Float[]{});
        dataY = dataYList.toArray(new Float[]{});
        dataStrX = dataStrXList.toArray(new String[]{});
        dataStrY = dataStrYList.toArray(new String[]{});


        rcLineChart.setData(dataX, dataStrX, dataY, dataStrY);
    }


    @Override
    protected void onResume() {
        mSwipeLayout.setRefreshing(true);
        getLastData(sn);
        super.onResume();
    }

    @Override
    public void onRefresh() {
        getLastData(sn);
    }
}
