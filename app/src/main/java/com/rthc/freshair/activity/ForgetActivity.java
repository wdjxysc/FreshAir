package com.rthc.freshair.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.rthc.freshair.R;
import com.rthc.freshair.net.NetConst;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ForgetActivity extends Activity implements View.OnFocusChangeListener {

    Button getCheckCodeBtn;

    EditText phoneEditText;
    EditText checkCodeEditText;

    ImageView userIc;
    ImageView mailIc;

    Context context;

    String checkCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        context = this;

        initView();
    }

    void initView() {
        getCheckCodeBtn = (Button) findViewById(R.id.getCheckCodeBtn);

        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        checkCodeEditText = (EditText) findViewById(R.id.checkCodeEditText);
        userIc = (ImageView) findViewById(R.id.userIc);
        mailIc = (ImageView) findViewById(R.id.mailIc);

        phoneEditText.setOnFocusChangeListener(this);
        checkCodeEditText.setOnFocusChangeListener(this);
    }

    public void back(View view) {
        this.finish();
    }

    public void nextBtnOnClick(View view) {
        if(checkCode == null) return;

        if (checkCode.equals(checkCodeEditText.getText().toString())) {
            Intent intent = new Intent(this, SetNewPwdActivity.class);
            intent.putExtra("phone", phoneEditText.getText().toString());

            startActivity(intent);
        } else {
            Toast.makeText(ForgetActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
        }
    }

    public void getCheckCodeBtnOnClick(View view) {
        //TODO 获取验证码

        if (!phoneEditText.getText().toString().isEmpty()) {
            getCheckCode(phoneEditText.getText().toString());
        } else {
            Toast.makeText(ForgetActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        getCheckCodeBtn.setEnabled(false);
        getCheckCodeBtn.setBackgroundResource(R.drawable.button_bg_gray);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 10; i > 0; i--) {

                    final int second = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getCheckCodeBtn.setText(second + "秒后可重新发送");
                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getCheckCodeBtn.setText(getResources().getText(R.string.get_check_code));
                        getCheckCodeBtn.setBackgroundResource(R.drawable.button_bg_gray_blue);
                        getCheckCodeBtn.setEnabled(true);
                    }
                });
            }
        }).start();


    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.phoneEditText:
                userIc.setImageResource(R.mipmap.user_blue);
                mailIc.setImageResource(R.mipmap.mail_gray);
                break;
            case R.id.checkCodeEditText:
                userIc.setImageResource(R.mipmap.user_gray);
                mailIc.setImageResource(R.mipmap.mail_blue);
                break;
        }
    }

    void getCheckCode(String phone) {
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_CHECK_CODE;

        OkHttpUtils.get()
                .url(url)
                .addParams("phone", phone)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.i("okhttp", "failed" + id);

                        Toast.makeText(context, "获取验证码失败，请稍后再试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("okhttp", "success" + id + "    " + response);

                        JSONTokener jsonParser = new JSONTokener(response);
                        JSONObject result;
                        try {
                            result = (JSONObject) jsonParser.nextValue();
                            checkCode = result.get("data").toString();
                            Toast.makeText(context, "获取验证码成功" + checkCode, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
