package com.rthc.freshair.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rthc.freshair.MyApplication;
import com.rthc.freshair.R;
import com.rthc.freshair.net.NetConst;
import com.rthc.freshair.widget.svprogresshud.SVProgressHUD;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class LoginActivity extends Activity implements View.OnClickListener, View.OnFocusChangeListener {

    TextView registerTextView;
    TextView forgetTextView;

    EditText usernameEditText;
    EditText pwdEditText;

    ImageView userIc;
    ImageView lockIc;

    Button loginBtn;



    View checkView;
    ImageView checkImageView;


    Boolean isRememberPwd = true;

    Context context;

    SharedPreferences preferences;


    SVProgressHUD svProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        context = this;

        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);

        initView();

        initData();
    }

    void initData() {
        if (preferences.getBoolean("remember", false)) {
            usernameEditText.setText(preferences.getString("phone", ""));
            pwdEditText.setText(preferences.getString("pwd", ""));

            checkImageView.setImageResource(R.mipmap.checked);
            isRememberPwd = true;
        }
    }

    private void initView() {
        registerTextView = (TextView) findViewById(R.id.registerTextView);
        forgetTextView = (TextView) findViewById(R.id.forgetTextView);
        usernameEditText = (EditText) findViewById(R.id.phoneEditText);
        pwdEditText = (EditText) findViewById(R.id.pwdEditText);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        userIc = (ImageView) findViewById(R.id.userIc);
        lockIc = (ImageView) findViewById(R.id.lockIc);

        checkView = findViewById(R.id.checkLayout);
        checkImageView = (ImageView) findViewById(R.id.checkImageView);

        registerTextView.setOnClickListener(this);
        forgetTextView.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        checkView.setOnClickListener(this);

        usernameEditText.setOnFocusChangeListener(this);
        pwdEditText.setOnFocusChangeListener(this);
    }

    void setPreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("remember", isRememberPwd);
        editor.putString("phone", usernameEditText.getText().toString());
        editor.putString("pwd", pwdEditText.getText().toString());
        editor.apply();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerTextView:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.forgetTextView:
                Intent intent1 = new Intent(this, ForgetActivity.class);
                startActivity(intent1);
                break;
            case R.id.loginBtn:

                if(usernameEditText.getText().toString().isEmpty()||pwdEditText.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "用户名密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                login();

                break;
            case R.id.weChatBtn:

                break;
            case R.id.qqBtn:

                break;
            case R.id.weiboBtn:

                break;
            case R.id.checkLayout:
                if (!isRememberPwd) {
                    checkImageView.setImageResource(R.mipmap.checked);
                    isRememberPwd = true;
                } else {
                    checkImageView.setImageResource(R.mipmap.checked_gray);
                    isRememberPwd = false;
                }

                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.phoneEditText:
                userIc.setImageResource(R.mipmap.user_blue);
                lockIc.setImageResource(R.mipmap.lock_gray);
                break;
            case R.id.pwdEditText:
                userIc.setImageResource(R.mipmap.user_gray);
                lockIc.setImageResource(R.mipmap.lock_blue);
                break;
        }
    }

    void login() {
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_LOGIN;

        SVProgressHUD.showWithStatus(this,"登录中...");

        OkHttpUtils.get()
                .url(url)
                .addParams("phone", usernameEditText.getText().toString())
                .addParams("pwd", pwdEditText.getText().toString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.i("okhttp", "failed" + id);
                        SVProgressHUD.showErrorWithStatus(context,"登录失败");

                        Toast.makeText(context, "登录失败,请检查网络", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, TabRootActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("okhttp", "success" + id + "    " + response);

                        JSONTokener jsonParser = new JSONTokener(response);
                        JSONObject result;
                        try {
                            result = (JSONObject) jsonParser.nextValue();
                            if (result.getInt("status") == NetConst.API_RESULT_SUCCESS) {
                                JSONObject data = (JSONObject) result.get("data");
                                MyApplication.USER_TOKEN_CURRENT = data.get("token").toString();
                                SVProgressHUD.showSuccessWithStatus(context,"登录成功");
                                Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(context, TabRootActivity.class);
                                startActivity(intent);

                                setPreferences();
                            } else {
                                SVProgressHUD.showErrorWithStatus(context, "登录失败");
                                Toast.makeText(context, result.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            svProgressHUD.dismiss();
                            e.printStackTrace();
                        }
                    }
                });

    }
}
