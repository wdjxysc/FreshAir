package com.rthc.freshair.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

public class SetNewPwdActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    EditText pwdEditText;
    ImageView lockIc;

    String phone;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_pwd);

        context = this;

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");

        initView();
    }

    void initView() {
        pwdEditText = (EditText) findViewById(R.id.pwdEditText);
        lockIc = (ImageView) findViewById(R.id.lockIc);
    }

    public void back(View view) {
        this.finish();
    }


    public void submitBtnOnClick(View view) {
        setNewPwd();


    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.pwdEditText) {
            if (hasFocus) {
                lockIc.setImageResource(R.mipmap.lock_blue);
            } else {
                lockIc.setImageResource(R.mipmap.lock_gray);
            }
        }
    }


    void setNewPwd() {
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_SET_NEW_PWD;

        OkHttpUtils.get()
                .url(url)
                .addParams("phone", phone)
                .addParams("pwd", pwdEditText.getText().toString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.i("okhttp", "failed" + id);

                        Toast.makeText(context, "修改密码失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("okhttp", "success" + id + "    " + response);

                        JSONTokener jsonParser = new JSONTokener(response);
                        JSONObject result;
                        try {
                            result = (JSONObject) jsonParser.nextValue();
                            if (result.getInt("status") == NetConst.API_RESULT_SUCCESS) {
                                Toast.makeText(context, "重置密码成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {
                                Toast.makeText(context, result.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
