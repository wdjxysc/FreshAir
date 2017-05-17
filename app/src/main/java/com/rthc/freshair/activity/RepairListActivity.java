package com.rthc.freshair.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rthc.freshair.MyApplication;
import com.rthc.freshair.R;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RepairListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout mSwipeLayout;

    RecyclerView recyclerView;

    ReportAdapter reportAdapter;

    List<HashMap<String, Object>> reportList = new ArrayList<>();

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_list);

        mContext = this;

        initData();

        initView();
    }

    public void back(View view) {
        this.finish();
    }

    void initData() {
        HashMap<String, Object> report = new HashMap<>();
        report.put("date", (new Date()).getTime());
        report.put("sn", "SN123456789123");
        report.put("reason", "烧坏了");
        report.put("isRepaired", true);
        report.put("reportId", "ID123456790");
        reportList.add(report);

        report = new HashMap<>();
        report.put("date", (new Date()).getTime());
        report.put("sn", "SN123456789");
        report.put("reason", "烧坏了");
        report.put("isRepaired", false);
        report.put("reportId", "ID123456789");
        reportList.add(report);

        getData(1);
    }

    void initView() {
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);

        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeColors(R.color.base_color_gray_blue);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        //设置adapter
        reportAdapter = new ReportAdapter();

        recyclerView.setAdapter(reportAdapter);
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        reportAdapter.setMyItemClickListener(new RcListener.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent(getApplication(), RepairReportDetailActivity.class);
                intent.putExtra("time", (Date) (reportList.get(position).get("time")));
                intent.putExtra("sn", reportList.get(position).get("sn").toString());
                intent.putExtra("reason", reportList.get(position).get("reason").toString());
                intent.putExtra("isRepaired", (Boolean) reportList.get(position).get("isRepaired"));
                intent.putExtra("reportId", reportList.get(position).get("reportId").toString());
                startActivity(intent);

                Log.i(Const.TAG, position + "");
            }
        });
    }


    /**
     * 获取对应页的数据
     *
     * @param page
     */
    void getData(int page) {
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_REPORT_REPAIR_LIST;

        OkHttpUtils.get()
                .url(url)
                .addParams("token", MyApplication.USER_TOKEN_CURRENT)
                .addParams("pageIndex", page + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.i("okhttp", "failed" + id);

                        Toast.makeText(mContext, "获取失败", Toast.LENGTH_SHORT).show();
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
//                                JSONObject data = result.getJSONObject("data");
                                JSONArray dataList = result.getJSONArray("data");
                                reportList.clear();

                                for (int i = 0; i < dataList.length(); i++) {
                                    JSONObject jsonObject = dataList.getJSONObject(i);
                                    HashMap<String, Object> map = new HashMap<String, Object>();
                                    map.put("sn", jsonObject.getString("sn"));
                                    map.put("reason", jsonObject.getString("reason"));
                                    map.put("date", jsonObject.getLong("date"));
                                    map.put("isRepaired", jsonObject.getBoolean("isRepaired"));
                                    map.put("reportId", jsonObject.getString("reportId"));
                                    reportList.add(map);
                                }

                                reportAdapter.notifyDataSetChanged();
                            } else {

                            }
                            String str = "";
                            if(reportList.size()==0){
                                str = ",暂无记录";
                            }
                            Toast.makeText(mContext, result.getString("message") + str, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mSwipeLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onRefresh() {
        getData(1);
    }

    class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder> {

        RcListener.MyItemClickListener myItemClickListener;

        void setMyItemClickListener(RcListener.MyItemClickListener myItemClickListener) {
            this.myItemClickListener = myItemClickListener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(RepairListActivity.this).inflate(R.layout.item_repair_report_list, parent,
                    false), myItemClickListener);

            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            HashMap<String, Object> data = reportList.get(position);
            long time = (long) data.get("date");
            Date date = new Date(time);
            holder.dayTv.setText(date.getDate() + "");
            holder.monthTv.setText(date.getMonth() + "");
            holder.timeTv.setText(Const.DATE_TIME_FORMAT.format(date));
            holder.snTv.setText(data.get("sn").toString());
            holder.reasonTv.setText(data.get("reason").toString());
            holder.isRepairedTv.setText(((Boolean) data.get("isRepaired")) ? "是" : "否");
            holder.isRepairedTv.setTextColor(((Boolean) data.get("isRepaired")) ? Color.LTGRAY : Color.RED);

            Drawable drawable = getResources().getDrawable(R.drawable.list_item_bg);
            holder.itemView.setBackground(drawable);
        }

        @Override
        public int getItemCount() {
            return reportList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView snTv;
            TextView timeTv;
            TextView reasonTv;
            TextView isRepairedTv;

            View headLayout;
            TextView dayTv;
            TextView monthTv;

            public MyViewHolder(View view, final RcListener.MyItemClickListener clickListener) {
                super(view);
                snTv = (TextView) view.findViewById(R.id.snTextView);
                timeTv = (TextView) view.findViewById(R.id.timeTextView);
                reasonTv = (TextView) view.findViewById(R.id.reasonTextView);
                isRepairedTv = (TextView) view.findViewById(R.id.isRepairedTextView);
                dayTv = (TextView) view.findViewById(R.id.dayTextView);
                monthTv = (TextView) view.findViewById(R.id.monthTextView);

                headLayout = view.findViewById(R.id.headLayout);

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
