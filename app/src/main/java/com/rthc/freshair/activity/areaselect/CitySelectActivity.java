package com.rthc.freshair.activity.areaselect;

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

import com.rthc.freshair.MyApplication;
import com.rthc.freshair.R;
import com.rthc.freshair.bean.Area;
import com.rthc.freshair.db.DBManager;
import com.rthc.freshair.util.Const;

import java.util.ArrayList;
import java.util.List;

public class CitySelectActivity extends AppCompatActivity {

    DBManager dbManager;

    List<Area> cityList = new ArrayList<Area>();

    ListView listView;

    MyAreaBaseAdapter myAreaBaseAdapter;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_select);

        mContext = this;

        initData();

        listView = (ListView) findViewById(R.id.listView);

        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //设置adapter
        myAreaBaseAdapter = new MyAreaBaseAdapter(this, R.layout.item_area_listview, cityList);

        listView.setAdapter(myAreaBaseAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(Const.TAG, position + "");

                Intent intent = new Intent();
                intent.putExtra("city", cityList.get(position).getArea());
                setResult(RESULT_OK, intent);
                finish();
            }

        });
    }

    void initData() {
        dbManager = MyApplication.getInstance().getDbManager();
        String provinceCode = getIntent().getExtras().getString("code");
        cityList = dbManager.queryArea(provinceCode);
//        myAreaBaseAdapter.notifyDataSetChanged();
    }


    public void back(View view) {
        finish();
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
