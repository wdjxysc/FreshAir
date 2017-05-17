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

public class RegisterActivity extends Activity implements View.OnFocusChangeListener {

    EditText phoneEditText;
    EditText checkCodeEditText;
    EditText pwdEditText;

    ImageView userIc;
    ImageView mailIc;
    ImageView lockIc;

    Button getCheckCodeBtn;

    String checkCode;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = this;

        initView();
    }

    void initView() {
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        checkCodeEditText = (EditText) findViewById(R.id.checkCodeEditText);
        pwdEditText = (EditText) findViewById(R.id.pwdEditText);

        userIc = (ImageView) findViewById(R.id.userIc);
        mailIc = (ImageView) findViewById(R.id.mailIc);
        lockIc = (ImageView) findViewById(R.id.lockIc);

        getCheckCodeBtn = (Button) findViewById(R.id.getCheckCodeBtn);

        phoneEditText.setOnFocusChangeListener(this);
        checkCodeEditText.setOnFocusChangeListener(this);
        pwdEditText.setOnFocusChangeListener(this);
    }

    public void back(View view) {
        this.finish();
    }

    public void getCheckCodeBtnOnClick(View view) {

        getCheckCode(phoneEditText.getText().toString());

        setBtnState();
    }

    public void registerBtnOnClick(View view) {

        if (!checkCodeEditText.getText().toString().equals(checkCode)) {
            Toast.makeText(this, "验证码错误", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneEditText.getText().toString().isEmpty() || checkCodeEditText.getText().toString().isEmpty() || pwdEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        register(phoneEditText.getText().toString(), pwdEditText.getText().toString());
    }

    public void provisionsTextViewOnClick(View view) {
        startActivity(new Intent(this, ProvisionsActivity.class));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.phoneEditText:
                userIc.setImageResource(R.mipmap.user_blue);
                mailIc.setImageResource(R.mipmap.mail_gray);
                lockIc.setImageResource(R.mipmap.lock_gray);
                break;
            case R.id.checkCodeEditText:
                userIc.setImageResource(R.mipmap.user_gray);
                mailIc.setImageResource(R.mipmap.mail_blue);
                lockIc.setImageResource(R.mipmap.lock_gray);
                break;
            case R.id.pwdEditText:
                userIc.setImageResource(R.mipmap.user_gray);
                mailIc.setImageResource(R.mipmap.mail_gray);
                lockIc.setImageResource(R.mipmap.lock_blue);
                break;
        }
    }

    void setBtnState() {
        getCheckCodeBtn.setEnabled(false);
        getCheckCodeBtn.setBackgroundResource(R.drawable.button_bg_gray);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 60; i > 0; i--) {

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
                            Toast.makeText(context, "获取验证码成功" + checkCode, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    void register(String phone, String pwd) {
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_REGISTER;

        OkHttpUtils.get()
                .url(url)
                .addParams("phone", phone)
                .addParams("pwd", pwd)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.i("okhttp", "failed" + id);

                        Toast.makeText(context, "注册失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("okhttp", "success" + id + "    " + response);

                        JSONTokener jsonParser = new JSONTokener(response);
                        JSONObject result;
                        try {
                            result = (JSONObject) jsonParser.nextValue();
                            if (result.getInt("status") == NetConst.API_RESULT_SUCCESS) {
                                Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
                                ((Activity) (context)).finish();
                            } else {
                                String message = result.getString("message");
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
