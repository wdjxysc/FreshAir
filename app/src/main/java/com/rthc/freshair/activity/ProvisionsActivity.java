package com.rthc.freshair.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.rthc.freshair.R;

public class ProvisionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provisions);
    }

    public void back(View view){
        this.finish();
    }
}
