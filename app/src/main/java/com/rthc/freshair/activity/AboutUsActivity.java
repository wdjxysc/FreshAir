package com.rthc.freshair.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rthc.freshair.MyApplication;
import com.rthc.freshair.R;
import com.rthc.freshair.net.NetConst;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class AboutUsActivity extends AppCompatActivity {

    TextView textView;
    ImageView imageView;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);

        mContext = this;

        getAboutUs();
    }

    public void back(View view){
        this.finish();
    }

    void getAboutUs() {
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_ABOUT_US;

        OkHttpUtils.get()
                .url(url)
                .addParams("token", MyApplication.USER_TOKEN_CURRENT)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.i("okhttp", "failed" + id);

                        Toast.makeText(mContext, "获取失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("okhttp", "success" + id + "    " + response);

                        JSONTokener jsonParser = new JSONTokener(response);
                        JSONObject result;
                        try {
                            result = (JSONObject) jsonParser.nextValue();
                            if(result.getInt("status") == NetConst.API_RESULT_SUCCESS){
                                JSONObject data = result.getJSONObject("data");
                                String picture = data.getString("Picture");
                                String content = data.getString("content");

                                textView.setText(content);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(mContext, "获取关于我们成功", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
