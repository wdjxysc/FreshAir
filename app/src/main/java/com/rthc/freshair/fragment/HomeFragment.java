package com.rthc.freshair.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rthc.freshair.MyApplication;
import com.rthc.freshair.R;
import com.rthc.freshair.activity.ChooseAddDeviceActivity;
import com.rthc.freshair.activity.DataAllActivity;
import com.rthc.freshair.activity.areaselect.ProvinceSelectActivity;
import com.rthc.freshair.listener.RcListener;
import com.rthc.freshair.net.NetConst;
import com.rthc.freshair.util.AirQuality;
import com.rthc.freshair.util.Const;
import com.rthc.freshair.util.NetTool;
import com.rthc.freshair.util.UtilAndroid;
import com.rthc.freshair.view.DividerItemDecoration;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    int REQUEST_CODE_SELECT_CITY = 1;
    int REQUEST_CODE_DEVICE = 2;

    View view;

    RecyclerView recyclerView;

    DeviceAdapter deviceAdapter;

    List<HashMap<String, Object>> deviceList = new ArrayList<>();

    TextView cityTextView;
    TextView mainDataTextView;
    TextView qltyTextView;
    TextView tmpTextView;
    TextView humTextView;
    TextView pm25TextView;
    TextView vocTextView;
    TextView hchoTextView;

    ImageView bgImageView1;
    ImageView bgImageView2;


    View locationLayout;

    SwipeRefreshLayout mSwipeLayout;



    /**
     * 设备列表页数
     */
    int pageIndex = 0;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initData();
        initView();

        getLocation();

        getFirstPage();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDeviceList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_SELECT_CITY){
            switch (resultCode){
                case Activity.RESULT_OK:
                    cityTextView.setText(data.getStringExtra("city"));
                    getWeather();
                    break;
            }
        }else if(requestCode == REQUEST_CODE_DEVICE){
            switch (resultCode){
                case  Activity.RESULT_OK:
                    getDeviceList();
                    break;
            }
        }
    }

    void initView() {
        cityTextView = (TextView) view.findViewById(R.id.cityTextView);
        mainDataTextView = (TextView) view.findViewById(R.id.mainDataTextView);
        qltyTextView = (TextView) view.findViewById(R.id.qltyTextView);
        tmpTextView = (TextView) view.findViewById(R.id.tmpTextView);
        humTextView = (TextView) view.findViewById(R.id.humTextView);
        pm25TextView = (TextView) view.findViewById(R.id.pm25TextView);
        vocTextView = (TextView) view.findViewById(R.id.vocTextView);
        hchoTextView = (TextView) view.findViewById(R.id.hchoTextView);

        bgImageView1 = (ImageView) view.findViewById(R.id.bgImageView1);
//        bgImageView2 = (ImageView) view.findViewById(R.id.bgImageView2);

        locationLayout = view.findViewById(R.id.locationLayout);
        locationLayout.setOnClickListener(this);

        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.id_swipe_ly);

        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeColors(R.color.base_color_gray_blue);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        //设置adapter
        deviceAdapter = new DeviceAdapter();

        recyclerView.setAdapter(deviceAdapter);
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        deviceAdapter.setMyItemClickListener(new RcListener.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i(Const.TAG, position + "");

                Intent intent = new Intent(getActivity(), DataAllActivity.class);
                intent.putExtra("sn", deviceList.get(position).get("sn").toString());
                intent.putExtra("name", deviceList.get(position).get("name").toString());
                intent.putExtra("onlineState", deviceList.get(position).get("onlineState").toString());
                intent.putExtra("type", deviceList.get(position).get("type").toString());
                startActivity(intent);
            }
        });

        view.findViewById(R.id.newAddBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChooseAddDeviceActivity.class);
                startActivity(intent);
            }
        });
    }

    void initData() {
        for (int i = 0; i < 0; i++) {
            HashMap<String, Object> device = new HashMap<>();
            device.put("name", "检测器" + i);
            device.put("type", "检测器");
            deviceList.add(device);
        }
    }

    void getLocation() {
        String url = NetConst.URL_BAIDU_GEOCODER;

        UtilAndroid.PositionAddress positionAddress = UtilAndroid.getLocation(getActivity());
        if (positionAddress == null || positionAddress.getLatitude() == 0 || positionAddress.getLongitude() == 0) {
            Toast.makeText(getActivity(), "定位失败", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpUtils.get()
                .url(url)
                .tag(this)
                .addParams("ak", NetConst.KYE_BAIDU_SERVER) //因为没有用到SDK, 只是使用的http API, 所以这里使用的是服务ak(key) 下方的mcode也就不需要了
                .addParams("callback", "renderReverse")
                //.addParams("mcode","15:7F:28:DB:DA:F8:1A:38:12:5F:D8:FB:13:00:E2:9C:C7:A3:C9:21;com.rthc.freshair")
//                .addParams("mcode","77:E2:BF:59:68:3C:C8:AE:0C:C0:88:15:7C:7A:C9:2D:EB:09:D2:D3;com.rthc.freshair")
                .addParams("location", positionAddress.getLatitude() + "," + positionAddress.getLongitude())
                .addParams("output", "json")
                .addParams("pois", "1")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.i("okhttp", "failed" + id);
                        if (id == 0) {
                            Toast.makeText(MyApplication.getInstance(), "获取地址信息失败，请检查网络", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MyApplication.getInstance(), "获取位置信息失败", Toast.LENGTH_SHORT).show();
                        }
                        getWeather();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("okhttp", "success" + id + "    " + response);
                        String json = response.substring(response.indexOf("(") + 1, response.lastIndexOf(")"));

                        JSONTokener jsonParser = new JSONTokener(json);
                        JSONObject result;
                        try {
                            result = (JSONObject) jsonParser.nextValue();
                            JSONObject result1 = result.getJSONObject("result")
                                    .getJSONObject("addressComponent");

                            String city = result1.getString("city");
                            String district = result1.getString("district");

                            if (!city.isEmpty())
                                cityTextView.setText(city);

                            Log.i("okhttp", city + "," + district);
                            getWeather();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    void getWeather() {
        String url = NetConst.URL_HEWEATHER;
        String ssssss = cityTextView.getText().toString().substring(0, cityTextView.getText().toString().length() - 1);
        OkHttpUtils.get()
                .url(url)
                .tag(this)
                .addHeader("apikey", NetConst.KEY_BAIDU_API_KEY)
                .addParams("city", cityTextView.getText().toString().substring(0, cityTextView.getText().toString().length() - 1))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.i("okhttp", "failed" + id);
                        Toast.makeText(MyApplication.getInstance(), "获取位置信息失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("okhttp", "success" + id + "    " + response);

                        JSONTokener jsonParser = new JSONTokener(response);
                        JSONObject result;
                        try {
                            result = (JSONObject) jsonParser.nextValue();
                            JSONArray result1 = result.getJSONArray("HeWeather data service 3.0");
                            JSONObject obj =  result1.getJSONObject(0);
                            JSONObject aqiObj = obj.getJSONObject("aqi");
                            JSONObject aqiCityObj = aqiObj.getJSONObject("city");

                            //单位：ug/m³
//                            String aqi = aqiCityObj.get("aqi").toString();
//                            String co = aqiCityObj.get("co").toString();
//                            String no2 = aqiCityObj.get("no2").toString();
//                            String o3 = aqiCityObj.get("o3").toString();
//                            String pm10 = aqiCityObj.get("pm10").toString();
                            String pm25 = aqiCityObj.get("pm25").toString();
                            pm25TextView.setText(String.format("%s", pm25));
                            mainDataTextView.setText(String.format("%s", pm25));
//                            String qlty = aqiCityObj.get("qlty").toString();
//                            String so2 = aqiCityObj.get("so2").toString();

                            JSONObject nowObj = (JSONObject) obj.get("now");
                            String tmp = nowObj.get("tmp").toString();
                            String hum = nowObj.get("hum").toString();

                            AirQuality.DataShow dataShow = AirQuality.getLvl(AirQuality.AirQualityType.Pm25,Integer.parseInt(pm25));
                            tmpTextView.setText(String.format("%s℃", tmp));
                            humTextView.setText(String.format("%s%%", hum));
                            pm25TextView.setText(String.format("%s", pm25));
                            mainDataTextView.setText(String.format("%s", pm25));
                            qltyTextView.setText(String.format("%s", dataShow.getLvlStr()));



                            if (dataShow.getLvlStr().equals("优")) {
                                qltyTextView.setBackground(getResources().getDrawable(R.drawable.qlty_style_top_1));
                            } else if (dataShow.getLvlStr().equals("良")) {
                                qltyTextView.setBackground(getResources().getDrawable(R.drawable.qlty_style_top_2));
                            } else if(dataShow.getLvlStr().equals("轻度污染")){
                                qltyTextView.setBackground(getResources().getDrawable(R.drawable.qlty_style_top_3));
                            } else if(dataShow.getLvlStr().equals("中度污染")){
                                qltyTextView.setBackground(getResources().getDrawable(R.drawable.qlty_style_top_4));
                            }
                            else if(dataShow.getLvlStr().equals("重度污染")){
                                qltyTextView.setBackground(getResources().getDrawable(R.drawable.qlty_style_top_5));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 获取当前用户下的设备列表
     */
    void getDeviceList() {
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_DEVICE_LIST;

        OkHttpUtils.get()
                .url(url)
                .tag(this)
                .addParams("token", MyApplication.USER_TOKEN_CURRENT)
                .addParams("pageIndex", pageIndex + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.i(Const.TAG, "failed" + id);
                        mSwipeLayout.setRefreshing(false);
                        Toast.makeText(MyApplication.getInstance(), "failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(Const.TAG, "success" + id + "    " + response);

                        JSONTokener jsonParser = new JSONTokener(response);
                        JSONObject result;
                        try {

                            result = (JSONObject) jsonParser.nextValue();
                            if (result.getInt("status") == NetConst.API_RESULT_SUCCESS) {
                                JSONArray data = (JSONArray) result.get("data");

                                List<HashMap<String, Object>> list = new ArrayList<>();
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject item = (JSONObject) data.get(i);
                                    HashMap<String, Object> device = new HashMap<>();
                                    device.put("sn", item.getString("sn"));
                                    device.put("name", item.getString("name"));
                                    device.put("onlineState", item.getString("onlineState"));
                                    device.put("type", item.getString("type"));
                                    list.add(device);
                                }

                                if (pageIndex == 0) deviceList.clear();
                                deviceList.addAll(list);

                                deviceAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MyApplication.getInstance(), result.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mSwipeLayout.setRefreshing(false);
                    }
                });
    }


    void getFirstPage() {
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_FIRST_PAGE;

        OkHttpUtils.get()
                .url(url)
                .tag(this)
                .addParams("token", MyApplication.USER_TOKEN_CURRENT)
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
                            if (pageIndex == 0) deviceList.clear();
                            result = (JSONObject) jsonParser.nextValue();
                            if (result.getInt("status") == NetConst.API_RESULT_SUCCESS) {
                                JSONObject data = (JSONObject) result.get("data");

                                final String picUrl1 = data.getString("Picture1");
                                String picUrl2 = data.getString("Picture2");

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            final Bitmap image1 = NetTool.getBitmap(picUrl1);

                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //bgImageView1.setImageBitmap(image1);
                                                }
                                            });

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }).start();
                            } else {
                                Toast.makeText(MyApplication.getInstance(), result.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.locationLayout:
                Intent intent = new Intent(getActivity(), ProvinceSelectActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT_CITY);
                break;
        }
    }

    @Override
    public void onRefresh() {
        getDeviceList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }




    public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.MyViewHolder> {

        RcListener.MyItemClickListener myItemClickListener;

        void setMyItemClickListener(RcListener.MyItemClickListener myItemClickListener) {
            this.myItemClickListener = myItemClickListener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_device_list, parent,
                    false), myItemClickListener);

            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.nameTv.setText(deviceList.get(position).get("name").toString());
            holder.snTv.setText(deviceList.get(position).get("sn").toString());
            holder.isOnlineTv.setText(deviceList.get(position).get("onlineState").toString().equals("1")? "在线":"离线");
            holder.isOnlineIv.setImageResource(deviceList.get(position).get("onlineState").toString().equals("1")? R.mipmap.wifi_4:R.mipmap.wifi_1);
            Drawable drawable = getResources().getDrawable(R.drawable.list_item_bg);
            holder.itemView.setBackground(drawable);
        }

        @Override
        public int getItemCount() {
            return deviceList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView nameTv;
            TextView isOnlineTv;
            TextView snTv;

            ImageView isOnlineIv;
            ImageView deviceIv;

            public MyViewHolder(View view, final RcListener.MyItemClickListener clickListener) {
                super(view);
                nameTv = (TextView) view.findViewById(R.id.titleTextView);
                isOnlineTv = (TextView) view.findViewById(R.id.isOnlineTextView);
                snTv = (TextView) view.findViewById(R.id.snTextView);
                isOnlineIv = (ImageView) view.findViewById(R.id.isOnlineImageView);
                deviceIv = (ImageView) view.findViewById(R.id.deviceImageView);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onItemClick(v, getAdapterPosition());
                    }
                });
            }
        }
    }
}
