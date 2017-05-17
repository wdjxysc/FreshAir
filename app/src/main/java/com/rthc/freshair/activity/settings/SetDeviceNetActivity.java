package com.rthc.freshair.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.rthc.freshair.R;

public class SetDeviceNetActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_device_net);
    }

    public void nextBtnOnClick(View view){
        startActivity(new Intent(this, SetDeviceWifiParamActivity.class));
    }

    public void back(View view){
        finish();
    }
}
