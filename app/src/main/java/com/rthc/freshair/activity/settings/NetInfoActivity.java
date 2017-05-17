package com.rthc.freshair.activity.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.rthc.freshair.R;

public class NetInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_info);
    }

    public void back(View view) {
        this.finish();
    }
}
