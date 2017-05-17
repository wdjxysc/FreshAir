package com.rthc.freshair.activity.areaselect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class ProvinceSelectActivity extends AppCompatActivity {
    int REQUEST_CODE_SELECT_CITY = 1;

    DBManager dbManager;

    List<Area> provinceList = new ArrayList<Area>();

    ListView listView;

    MyAreaBaseAdapter myAreaBaseAdapter;

    TextView cityTextView;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province_select);

        mContext = this;

        initData();

        cityTextView = (TextView)findViewById(R.id.cityTextView);
        getLocation();
        cityTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("city", cityTextView.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        listView = (ListView) findViewById(R.id.listView);

        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //设置adapter
        myAreaBaseAdapter = new MyAreaBaseAdapter(this, R.layout.item_area_listview, provinceList);

        listView.setAdapter(myAreaBaseAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(Const.TAG, position + "");

                Intent intent = new Intent(mContext, CitySelectActivity.class);
                intent.putExtra("code", provinceList.get(position).getCode());
                startActivityForResult(intent, REQUEST_CODE_SELECT_CITY);
            }
        });

//        initData();
    }

    void initData(){
        dbManager = MyApplication.getInstance().getDbManager();
        provinceList = dbManager.queryArea("1");
//        myAreaBaseAdapter.notifyDataSetChanged();
    }

    public void back(View view){
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_SELECT_CITY){
            switch (resultCode){
                case Activity.RESULT_OK:
                    String city = data.getStringExtra("city");
                    Intent intent = new Intent();
                    intent.putExtra("city", city);
                    setResult(RESULT_OK,intent);
                    finish();
                    break;
            }
        }
    }


    void getLocation() {
        String url = NetConst.URL_BAIDU_GEOCODER;

        UtilAndroid.PositionAddress positionAddress = UtilAndroid.getLocation(mContext);
        if (positionAddress == null || positionAddress.getLatitude() == 0 || positionAddress.getLongitude() == 0) {
            Toast.makeText(mContext, "定位失败", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpUtils.get()
                .url(url)
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
                            Toast.makeText(mContext, "获取地址信息失败，请检查网络", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "获取位置信息失败", Toast.LENGTH_SHORT).show();
                        }
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
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
}
