package com.rthc.freshair.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rthc.freshair.R;
import com.rthc.freshair.listener.RcListener;
import com.rthc.freshair.util.Const;
import com.rthc.freshair.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends Activity {

    RecyclerView recyclerView;

    DeviceAdapter deviceAdapter;

    List<HashMap<String,Object>> deviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initData();


        initView();
    }

    void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        //设置adapter
        deviceAdapter = new DeviceAdapter();

        recyclerView.setAdapter(deviceAdapter);
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        deviceAdapter.setMyItemClickListener(new RcListener.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i(Const.TAG, position + "");
            }
        });


    }

    void initData() {
        for (int i = 0; i < 10000; i++) {
            HashMap<String, Object> device = new HashMap<>();
            device.put("name", "哈勃" + i);
            device.put("type", "检测器");
            deviceList.add(device);
        }
    }



    class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.MyViewHolder>{

        RcListener.MyItemClickListener myItemClickListener;

        void setMyItemClickListener(RcListener.MyItemClickListener myItemClickListener){
            this.myItemClickListener = myItemClickListener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    HomeActivity.this).inflate(R.layout.item_device_list, parent,
                    false),myItemClickListener);

            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tv.setText(deviceList.get(position).get("name").toString());
            Drawable drawable = getResources().getDrawable(R.drawable.list_item_bg);
            holder.itemView.setBackground(drawable);
        }

        @Override
        public int getItemCount() {
            return deviceList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder
        {
            TextView tv;

            public MyViewHolder(View view, final RcListener.MyItemClickListener clickListener)
            {
                super(view);
                tv = (TextView) view.findViewById(R.id.titleTextView);

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
