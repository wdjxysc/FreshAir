package com.rthc.freshair.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.rthc.freshair.R;

public class ChooseAddDeviceActivity extends AppCompatActivity {

    final int RESULT_CODE_ADDED = 1;

    View addDetectDevice;
    View addClearDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_add_device);

        initView();
    }

    public void back(View view) {
        this.finish();
    }

    void initView() {

        addDetectDevice = findViewById(R.id.addDetectDevice);
        addClearDevice = findViewById(R.id.addClearDevice);

        addDetectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "添加检测设备", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplication(), AddDeviceActivity.class);
                startActivityForResult(intent, RESULT_CODE_ADDED);
            }
        });

        addClearDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "添加净化设备", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplication(), AddDeviceActivity.class);
                startActivityForResult(intent, RESULT_CODE_ADDED);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            finish();
        }
    }
}
