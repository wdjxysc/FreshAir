package com.rthc.freshair.activity.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.rthc.freshair.R;

import java.util.Locale;

public class SetTimingSubActivity extends AppCompatActivity {

    TextView turnOnTimeTextView;
    TextView turnOffTimeTextView;

    int index;

    String onTime;
    String offTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_timing_sub);

        Intent intent = getIntent();
        index = intent.getIntExtra("index", 1);
        onTime = intent.getStringExtra("onTime");
        offTime = intent.getStringExtra("offTime");

        initView();
    }

    void initView() {
        turnOnTimeTextView = (TextView) findViewById(R.id.turnOnTimeTextView);
        turnOffTimeTextView = (TextView) findViewById(R.id.turnOffTimeTextView);

        turnOnTimeTextView.setText(onTime);
        turnOffTimeTextView.setText(offTime);
    }

    public void back(View v) {
        setResult(RESULT_CANCELED);
        this.finish();
    }

    public void turnOnTime(View v) {

        final AlertDialog.Builder customDia = new AlertDialog.Builder(this);
        final View viewDia = LayoutInflater.from(this).inflate(R.layout.pick_time_dia_content, null);

        final TimePicker timePicker = (TimePicker) viewDia.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        customDia.setTitle("选择时间");
        customDia.setView(viewDia);
        customDia.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                turnOnTimeTextView.setText(String.format(Locale.CHINA, "%02d:%02d:00", hour, minute));
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        customDia.create().show();

    }

    public void turnOffTime(View v) {
        final AlertDialog.Builder customDia = new AlertDialog.Builder(this);
        final View viewDia = LayoutInflater.from(this).inflate(R.layout.pick_time_dia_content, null);

        final TimePicker timePicker = (TimePicker) viewDia.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        customDia.setTitle("选择时间");
        customDia.setView(viewDia);
        customDia.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                turnOffTimeTextView.setText(String.format(Locale.CHINA, "%02d:%02d:00", hour, minute));
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        customDia.create().show();
    }

    public void setTime(View view) {
        Intent intent = new Intent();
        intent.putExtra("onTime", turnOnTimeTextView.getText().toString());
        intent.putExtra("offTime", turnOffTimeTextView.getText().toString());
        intent.putExtra("index", index);
        setResult(RESULT_OK, intent);
        this.finish();
    }
}
