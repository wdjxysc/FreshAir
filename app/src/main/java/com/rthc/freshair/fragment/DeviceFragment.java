package com.rthc.freshair.fragment;


import android.app.Fragment;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rthc.freshair.MyApplication;
import com.rthc.freshair.R;
import com.rthc.freshair.activity.ChooseAddDeviceActivity;
import com.rthc.freshair.activity.DataAllActivity;
import com.rthc.freshair.listener.RcListener;
import com.rthc.freshair.net.NetConst;
import com.rthc.freshair.util.Const;
import com.rthc.freshair.view.DividerItemDecoration;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    ImageButton addDeviceBtn;

    View view;

    RecyclerView recyclerView;
    DeviceAdapter deviceAdapter;

    SwipeRefreshLayout mSwipeLayout;

    List<HashMap<String, Object>> deviceList = new ArrayList<>();

    int pageIndex = 0;

    public DeviceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_device, container, false);
        //initData();
        initView();


        getDeviceList();

        // Inflate the layout for this fragment
        return view;
    }

    void initView() {


        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.id_swipe_ly);

        mSwipeLayout.setOnRefreshListener(this);


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

        addDeviceBtn = (ImageButton) view.findViewById(R.id.addDeviceBtn);
        addDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChooseAddDeviceActivity.class);
                startActivity(intent);
            }
        });


    }

    void initData() {
        for (int i = 0; i < 10000; i++) {
            HashMap<String, Object> device = new HashMap<>();
            device.put("name", "检测器" + i);
            device.put("type", "检测器");
            deviceList.add(device);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        getDeviceList();
    }

    /**
     * 获取当前用户下的设备列表
     */
    void getDeviceList() {
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_DEVICE_LIST;

        OkHttpUtils.get()
                .url(url)
                .addParams("token", MyApplication.USER_TOKEN_CURRENT)
                .addParams("pageIndex", pageIndex + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.i(Const.TAG, "failed" + id);
                        mSwipeLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getActivity(), result.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mSwipeLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onRefresh() {
        getDeviceList();
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
