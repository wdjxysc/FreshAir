package com.rthc.freshair.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rthc.freshair.MyApplication;
import com.rthc.freshair.R;
import com.rthc.freshair.net.NetConst;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RepairReportDetailActivity extends AppCompatActivity {

    /**
     * 已维修时显示
     */
    LinearLayout repairDetailLayout;

    /**
     * 未维修时显示
     */
    LinearLayout writeRepairReportLayout;

    /**
     * 维修方法 普通维修时显示
     */
    LinearLayout repairMethodLayout;

    /**
     * 新设备ID 换设备时显示
     */
    LinearLayout newDeviceLayout;

    //维修方式
    RadioButton normal;
    RadioButton change;

    Button commitBtn;

    //维修结果
    EditText repairResultEditText;
    EditText newDeviceEditText;
    EditText repairMethodEditText;

    TextView departmentTextView;
    TextView reporterTextView;
    TextView reporterPhoneTextView;
    TextView reportDateTextView;
    TextView snTextView;
    TextView deviceNameTextView;
    TextView installDateTextView;
    TextView underWarrantyTextView;
    TextView streetTextView;
    TextView locationTextView;
    TextView badPartsTextView;
    TextView reasonTextView;
    TextView faultLevelTextView;
    TextView faultDescriptionTextView;
    TextView adviceTextView;
    TextView workmanTextView;
    TextView workmanPhoneTextView;
    TextView repairMethodTextView;


    Boolean isRepair;

    Context mContext;

    String reportId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_report_detail);

        mContext = this;

        initData();

        initView();
    }

    public void back(View view) {
        this.finish();
    }

    private void initData() {
        Bundle extras = getIntent().getExtras();
        reportId = extras.getString("reportId");
        String reason = extras.getString("reason");
        String sn = extras.getString("sn");
        Date date = (Date) extras.get("time");
        isRepair = extras.getBoolean("isRepaired");

        getDetail(reportId);
    }

    void initView() {
        repairDetailLayout = (LinearLayout) findViewById(R.id.repairDetailLayout);
        writeRepairReportLayout = (LinearLayout) findViewById(R.id.writeRepairReportLayout);
        newDeviceLayout = (LinearLayout) findViewById(R.id.newDeviceLayout);
        repairMethodLayout = (LinearLayout) findViewById(R.id.repairMethodLayout);

        normal = (RadioButton) findViewById(R.id.radioBtn1);
        change = (RadioButton) findViewById(R.id.radioBtn2);

        repairResultEditText = (EditText) findViewById(R.id.repairResultEditText);
        newDeviceEditText = (EditText) findViewById(R.id.newDeviceEditText);
        repairMethodEditText = (EditText) findViewById(R.id.repairMethodEditText);

        departmentTextView = (TextView) findViewById(R.id.departmentTextView);
        reporterTextView = (TextView) findViewById(R.id.reporterTextView);
        reporterPhoneTextView = (TextView) findViewById(R.id.reporterPhoneTextView);
        reportDateTextView = (TextView) findViewById(R.id.reportDateTextView);
        snTextView = (TextView) findViewById(R.id.snTextView);
        deviceNameTextView = (TextView) findViewById(R.id.deviceNameTextView);
        installDateTextView = (TextView) findViewById(R.id.installDateTextView);
        underWarrantyTextView = (TextView) findViewById(R.id.underWarrantyTextView);
        streetTextView = (TextView) findViewById(R.id.streetTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);
        badPartsTextView = (TextView) findViewById(R.id.badPartsTextView);
        reasonTextView = (TextView) findViewById(R.id.reasonTextView);
        faultLevelTextView = (TextView) findViewById(R.id.faultLevelTextView);
        faultDescriptionTextView = (TextView) findViewById(R.id.faultDescriptionTextView);
        adviceTextView = (TextView) findViewById(R.id.adviceTextView);
        workmanTextView = (TextView) findViewById(R.id.workmanTextView);
        workmanPhoneTextView = (TextView) findViewById(R.id.workmanPhoneTextView);
        repairMethodTextView = (TextView) findViewById(R.id.repairMethodTextView);

        commitBtn = (Button) findViewById(R.id.commitBtn);

        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitRepairReport(reportId);
            }
        });


        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.repairMethodRadioGroup);
        if (radioGroup != null)
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.radioBtn1:
                            //普通维修
                            repairMethodLayout.setVisibility(View.VISIBLE);
                            newDeviceLayout.setVisibility(View.GONE);
                            break;
                        case R.id.radioBtn2:
                            //更换设备
                            repairMethodLayout.setVisibility(View.GONE);
                            newDeviceLayout.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            });


        if (isRepair) {
            repairDetailLayout.setVisibility(View.VISIBLE);
            writeRepairReportLayout.setVisibility(View.GONE);
        } else {
            repairDetailLayout.setVisibility(View.GONE);
            writeRepairReportLayout.setVisibility(View.VISIBLE);
        }
    }

    void getDetail(String reportId) {
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_REPORT_REPAIR_DETAIL;

        OkHttpUtils.get()
                .url(url)
                .addParams("token", MyApplication.USER_TOKEN_CURRENT)
                .addParams("reportId", reportId)
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
                            JSONObject data = result.getJSONObject("data");

                            boolean underWarranty = data.getBoolean("underWarranty");
                            String sn = data.getString("sn");
                            String workmanPhone = data.getString("workmanPhone");
                            String badParts = data.getString("badParts");
                            String repairResult = data.getString("repairResult");
                            String reason = data.getString("reason");
                            String department = data.getString("department");
                            String street = data.getString("street");
                            int faultLevel = data.getInt("faultLevel");
                            String reporter = data.getString("reporter");
                            String deviceName = data.getString("deviceName");
                            String installDate = data.getString("installDate");
                            int methodCode = data.getInt("repairMethod");
                            String reporterPhone = data.getString("reporterPhone");
                            String workman = data.getString("workman");
                            String reportDate = data.getString("reportDate");
                            String faultDescription = data.getString("faultDescription");
                            String advice = data.getString("advice");

                            underWarrantyTextView.setText(underWarranty ? "是" : "否");
                            snTextView.setText(sn);
                            workmanPhoneTextView.setText(workmanPhone);
                            badPartsTextView.setText(badParts);
                            repairResultEditText.setText(repairResult);
                            reasonTextView.setText(reason);
                            departmentTextView.setText(department);
                            streetTextView.setText(street);
                            faultLevelTextView.setText(""+faultLevel + "");
                            reporterTextView.setText(reporter);
                            deviceNameTextView.setText(deviceName);
                            installDateTextView.setText(installDate);
                            repairMethodTextView.setText(methodCode == 3 ? "更换设备" : "普通维修");
                            reporterPhoneTextView.setText(reporterPhone);
                            workmanTextView.setText(workman);
                            reportDateTextView.setText(reportDate);
                            faultDescriptionTextView.setText(faultDescription);
                            adviceTextView.setText(advice);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(mContext, "获取列表成功", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    Map getRepairMethodFromView() {
        HashMap map = new HashMap<String, Object>();

        map.put("methodCode", change.isChecked() ? 3 : 1);
        map.put("methodStr", change.isChecked() ? "更换设备" : repairMethodEditText.getText().toString());

        return map;
    }


    void commitRepairReport(String reportId) {
        String url = NetConst.URL_PIAOAI + NetConst.API_USER_REPORT_REPAIR_RESULT;

        JSONObject repairMethod = new JSONObject(getRepairMethodFromView());

        GetBuilder getBuilder = OkHttpUtils.get()
                .url(url)
                .addParams("token", MyApplication.USER_TOKEN_CURRENT)
                .addParams("reportId", reportId)
                .addParams("repairMethod", repairMethod.toString())
                .addParams("repairResult", repairResultEditText.getText().toString());

        if (change.isChecked()) {
            getBuilder.addParams("newDeviceSn", newDeviceEditText.getText().toString());
        }

        getBuilder.build()
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

                            if (result.getInt("status") == NetConst.API_RESULT_SUCCESS) {

                            }
                            Toast.makeText(mContext, result.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
    }
}
