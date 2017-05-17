package com.rthc.freshair.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import com.rthc.freshair.R;
import com.rthc.freshair.listener.RcListener;
import com.rthc.freshair.util.Const;
import com.rthc.freshair.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    MessageAdapter messageAdapter;

    List<HashMap<String, Object>> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);



        initView();

        new Thread(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        }).start();

    }
    public void back(View view){
        this.finish();
    }

    void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        //设置adapter
        messageAdapter = new MessageAdapter();

        recyclerView.setAdapter(messageAdapter);
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        messageAdapter.setMyItemClickListener(new RcListener.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i(Const.TAG, position + "");
                Toast.makeText(getApplication(), "这是第" + position + "条消息", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void initData() {


        for (int i = 0; i < 100; i++) {
            HashMap<String, Object> message = new HashMap<>();
            message.put("title", "我是标题" + i);
            message.put("detail", "今天天气真好啊啊啊啊啊");
            messageList.add(message);
        }

        messageAdapter.notifyDataSetChanged();
    }

    class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

        RcListener.MyItemClickListener myItemClickListener;

        void setMyItemClickListener(RcListener.MyItemClickListener myItemClickListener) {
            this.myItemClickListener = myItemClickListener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(MessageActivity.this).inflate(R.layout.item_message_list, parent,
                    false), myItemClickListener);

            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.titleTv.setText(messageList.get(position).get("title").toString());
            Drawable drawable = getResources().getDrawable(R.drawable.list_item_bg);
            holder.itemView.setBackground(drawable);
            holder.detailTv.setText(messageList.get(position).get("detail").toString());
        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView titleTv;
            TextView detailTv;

            public MyViewHolder(View view, final RcListener.MyItemClickListener clickListener) {
                super(view);
                titleTv = (TextView) view.findViewById(R.id.titleTextView);
                detailTv = (TextView) view.findViewById(R.id.detailTextView);

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
