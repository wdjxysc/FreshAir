package com.rthc.freshair.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rthc.freshair.MyApplication;
import com.rthc.freshair.R;
import com.rthc.freshair.bean.Area;
import com.rthc.freshair.db.DBManager;
import com.rthc.freshair.net.NetConst;
import com.rthc.freshair.util.Const;
import com.rthc.freshair.util.UtilAndroid;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InstallDeviceActivity extends AppCompatActivity {

    Spinner provinceSpinner;
    Spinner citySpinner;
    Spinner areaSpinner;
    Spinner streetSpinner;

    MyAreaBaseAdapter provinceAdapter;
    MyAreaBaseAdapter cityAdapter;
    MyAreaBaseAdapter areaAdapter;
    MyAreaBaseAdapter streetAdapter;

    List<Area> provinceList = new ArrayList<Area>();
    List<Area> cityList = new ArrayList<Area>();
    List<Area> areaList = new ArrayList<Area>();
    List<Area> streetList = new ArrayList<Area>();

    MyOnItemSelectedListener myOnItemSelectedListener;

    EditText snEditText;
    EditText keyEditText;
//    TextView snTextView;
//    TextView keyTextView;
    TextView typeTextView;

    EditText addressEditText;
    EditText deviceNameEditText;

    Button scanBtn;
    Button snScanBtn;
    Button keyScanBtn;
    Button getNameBtn;
    Button submitBtn;

    final int SCAN_REQUEST_CODE_SN = 1;
    final int SCAN_REQUEST_CODE_KEY = 2;

    DBManager dbManager;

    Context context;

    UtilAndroid.PositionAddress positionAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install_device);

        context = this;

        positionAddress = UtilAndroid.getLocation(this);

        initData();

        initView();
    }
    public void back(View view){
        this.finish();
    }
    void initView() {
        provinceSpinner = (Spinner) findViewById(R.id.provinceSpinner);
        citySpinner = (Spinner) findViewById(R.id.citySpinner);
        areaSpinner = (Spinner) findViewById(R.id.areaSpinner);
        streetSpinner = (Spinner) findViewById(R.id.streetSpinner);

        provinceAdapter = new MyAreaBaseAdapter(this, R.layout.item_spinner, provinceList);
        provinceSpinner.setAdapter(provinceAdapter);
        cityAdapter = new MyAreaBaseAdapter(this, R.layout.item_spinner, cityList);
        citySpinner.setAdapter(cityAdapter);
        areaAdapter = new MyAreaBaseAdapter(this, R.layout.item_spinner, areaList);
        areaSpinner.setAdapter(areaAdapter);
        streetAdapter = new MyAreaBaseAdapter(this, R.layout.item_spinner, streetList);
        streetSpinner.setAdapter(streetAdapter);

        myOnItemSelectedListener = new MyOnItemSelectedListener();
        provinceSpinner.setOnItemSelectedListener(myOnItemSelectedListener);
        citySpinner.setOnItemSelectedListener(myOnItemSelectedListener);
        areaSpinner.setOnItemSelectedListener(myOnItemSelectedListener);
        streetSpinner.setOnItemSelectedListener(myOnItemSelectedListener);

        snEditText = (EditText) findViewById(R.id.snEditText);
        keyEditText = (EditText) findViewById(R.id.keyEditText);
//        snTextView = (TextView) findViewById(R.id.snTextView);
//        keyTextView = (TextView) findViewById(R.id.keyTextView);
        typeTextView = (TextView) findViewById(R.id.typeTextView);

        deviceNameEditText = (EditText) findViewById(R.id.deviceNameEditText);
        addressEditText = (EditText) findViewById(R.id.addressEditText);

        snScanBtn = (Button) findViewById(R.id.snScanBtn);
        keyScanBtn = (Button) findViewById(R.id.keyScanBtn);
        scanBtn = (Button) findViewById(R.id.scanBtn);
        getNameBtn = (Button) findViewById(R.id.getNameBtn);
        submitBtn = (Button) findViewById(R.id.submitBtn);

        snScanBtn.setOnClickListener(onClickListener);
        keyScanBtn.setOnClickListener(onClickListener);
        scanBtn.setOnClickListener(onClickListener);
        getNameBtn.setOnClickListener(onClickListener);
        submitBtn.setOnClickListener(onClickListener);
    }

    void initData() {
        dbManager = MyApplication.getInstance().getDbManager();
        provinceList = dbManager.queryArea("1");
        cityList = dbManager.queryArea(provinceList.get(0).getCode());
        areaList = dbManager.queryArea(cityList.get(0).getCode());
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.scanBtn:
                    Intent intent = new Intent(getApplicationContext(), ScanQrcodeActivity.class);
                    startActivityForResult(intent, SCAN_REQUEST_CODE_SN);
                    break;
                case R.id.getNameBtn:

                    if (addressEditText.getText().toString().isEmpty()) {
                        Toast.makeText(context, "安装地址不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    deviceNameEditText.setText(
                            String.format("%s-%s-01",
                                    addressEditText.getText().toString(),
                                    typeTextView.getText().toString()));
                    break;
                case R.id.submitBtn:
                    Toast.makeText(context, "提交", Toast.LENGTH_SHORT).show();

                    install();

                    break;
                case R.id.snScanBtn:
                    Intent intent1 = new Intent(getApplicationContext(), ScanQrcodeActivity.class);
                    startActivityForResult(intent1, SCAN_REQUEST_CODE_SN);
                    break;
                case R.id.keyScanBtn:
                    Intent intent2 = new Intent(getApplicationContext(), ScanQrcodeActivity.class);
                    startActivityForResult(intent2, SCAN_REQUEST_CODE_KEY);
                    break;
            }
        }
    };

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

    class MyAreaBaseAdapter extends BaseAdapter {
        int layoutId;
        List<Area> data;
        Context context;

        public MyAreaBaseAdapter(Context context, int layoutId, List<Area> data) {
            this.context = context;
            this.layoutId = layoutId;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(getApplicationContext()).inflate(layoutId, null);

            TextView mTextView = (TextView) convertView.findViewById(R.id.textView);
            mTextView.setText(data.get(position).getArea());
            mTextView.setTextColor(Color.DKGRAY);
//            mTextView.setTextColor(Color.RED);
            return convertView;
        }
    }

    class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (parent.getId()) {
                case R.id.provinceSpinner:
                    cityList.clear();
                    cityList.addAll(dbManager.queryArea(provinceList.get(position).code));
                    cityAdapter.notifyDataSetChanged();

                    areaList.clear();
                    areaList.addAll(dbManager.queryArea(cityList.get(0).code));
                    areaAdapter.notifyDataSetChanged();
                    break;
                case R.id.citySpinner:
                    areaList.clear();
                    areaList.addAll(dbManager.queryArea(cityList.get(position).code));
                    areaAdapter.notifyDataSetChanged();
                    break;
                case R.id.areaSpinner:
                    //查询街道使用淘宝物流接口
                    //streetList = dbManager.queryArea(areaList.get(position).code);
                    getStreet(provinceList.get(provinceSpinner.getSelectedItemPosition()).getCode(),
                            cityList.get(citySpinner.getSelectedItemPosition()).getCode(),
                            areaList.get(areaSpinner.getSelectedItemPosition()).getCode());
                    break;
                case R.id.streetSpinner:
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }


    /**
     * 使用淘宝物流接口 根据省市区编号获取街道列表
     *
     * @param provinceCode
     * @param cityCode
     * @param areaCode
     */
    public void getStreet(String provinceCode, String cityCode, String areaCode) {

        String str = NetConst.URL_TAOBAO_WULIU;

        //使用了OkHttpUtils 封装类
        OkHttpUtils.get()
                .url(str)
                .addParams("l1", provinceCode)
                .addParams("l2", cityCode)
                .addParams("l3", areaCode)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.i("okhttp", "failed" + id);
                        Toast.makeText(context, "获取街道数据失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("okhttp", "success" + id);
                        String json = response.substring(response.indexOf("(") + 1, response.lastIndexOf(")"));

                        JSONTokener jsonParser = new JSONTokener(json);
                        JSONObject result;
                        try {
                            result = (JSONObject) jsonParser.nextValue();
                            JSONArray list = result.getJSONArray("result");

                            streetList.clear();
                            for (int i = 0; i < list.length(); i++) {

                                JSONArray item = list.getJSONArray(i);
                                Area area = new Area();
                                area.setCode(item.get(0).toString());
                                area.setArea(item.get(1).toString());
                                area.setParentId(item.get(2).toString());
                                area.setPinyin(item.get(3).toString());

                                streetList.add(area);
                            }
                            streetAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    HashMap getLocation(){
        HashMap map = new HashMap();
        map.put("provinceCode",provinceList.get(provinceSpinner.getSelectedItemPosition()).code);
        map.put("provinceName",provinceList.get(provinceSpinner.getSelectedItemPosition()).area);
        map.put("cityCode",cityList.get(citySpinner.getSelectedItemPosition()).code);
        map.put("cityName",cityList.get(citySpinner.getSelectedItemPosition()).area);
        map.put("areaCode",areaList.get(areaSpinner.getSelectedItemPosition()).code);
        map.put("areaName",areaList.get(areaSpinner.getSelectedItemPosition()).area);
        map.put("streetCode",streetList.size()==0? null:streetList.get(streetSpinner.getSelectedItemPosition()).code);
        map.put("streetName",streetList.size()==0? null:streetList.get(streetSpinner.getSelectedItemPosition()).area);
        map.put("lastAddress",addressEditText.getText().toString());
        HashMap mapPosition = new HashMap();
        mapPosition.put("latitude", positionAddress.getLatitude());
        mapPosition.put("longitude", positionAddress.getLongitude());
        map.put("position",mapPosition);
        return map;
    }


    public void install(){
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_INSTALL_DEV;

        JSONObject jsonObject = new JSONObject(getLocation());
        Log.i(Const.TAG, "install: " + jsonObject.toString());

        OkHttpUtils.get()
                .url(url)
                .addParams("token", MyApplication.USER_TOKEN_CURRENT)
                .addParams("sn", snEditText.getText().toString())
                .addParams("key",keyEditText.getText().toString())
                .addParams("deviceName",deviceNameEditText.getText().toString())
                .addParams("location",jsonObject.toString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.i("okhttp", "failed" + id);

//                        Toast.makeText(mContext,"获取失败",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("okhttp", "success" + id + "    " + response);

                        JSONTokener jsonParser = new JSONTokener(response);
                        JSONObject result;
                        try {
                            result = (JSONObject) jsonParser.nextValue();
                            if(result.getInt("status") == NetConst.API_RESULT_SUCCESS){
//                                JSONObject data = result.getJSONObject("data");
//                                JSONObject dataList = data.getJSONObject("data");
//                                reportList.clear();
//                                for(int i = 0;i<dataList.length();i++){
//                                    JSONObject jsonObject = dataList.getJSONObject(i);
//                                    HashMap<String,Object> map = new HashMap<String,Object>();
//                                    map.put("sn",jsonObject.getString("sn"));
//                                    map.put("reason",jsonObject.getString("reason"));
//                                    map.put("date",jsonObject.getLong("date"));
//                                    map.put("isRepaired",jsonObject.getBoolean("isRepaired"));
//                                    map.put("reportId",jsonObject.getString("reportId"));
//                                    reportList.add(map);
//                                }
                            }else {

                            }
                            Toast.makeText(context, result.getString("message"),Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
