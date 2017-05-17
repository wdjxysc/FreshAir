package com.rthc.freshair.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.rthc.freshair.R;
import com.rthc.freshair.db.DBManager;
import com.rthc.freshair.fragment.DeviceFragment;
import com.rthc.freshair.fragment.HomeFragment;
import com.rthc.freshair.fragment.MallFragment;
import com.rthc.freshair.fragment.UserFragment;
import com.rthc.freshair.widget.MainNavigateTabBar;

public class TabRootActivity extends FragmentActivity {

    private MainNavigateTabBar navigateTabBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DBManager dbManager = new DBManager(this);

        setContentView(R.layout.activity_tab_root);

        navigateTabBar = (MainNavigateTabBar) findViewById(R.id.navigateTabBar);
        //对应xml中的containerId
        navigateTabBar.setFrameLayoutId(R.id.main_container);
        //对应xml中的navigateTabTextColor
        navigateTabBar.setTabTextColor(getResources().getColor(R.color.tab_text_normal));
        //对应xml中的navigateTabSelectedTextColor
        navigateTabBar.setSelectedTabTextColor(getResources().getColor(R.color.tab_text_select));

        //恢复选项状态
        navigateTabBar.onRestoreInstanceState(savedInstanceState);

        navigateTabBar.addTab(HomeFragment.class, new MainNavigateTabBar.TabParam(R.color.base_color_gray_blue, R.mipmap.tab_home_off, R.mipmap.tab_home_on, "首页"));
        navigateTabBar.addTab(DeviceFragment.class, new MainNavigateTabBar.TabParam(R.color.base_color_gray_blue, R.mipmap.tab_list_off, R.mipmap.tab_list_on, "设备"));
        navigateTabBar.addTab(UserFragment.class, new MainNavigateTabBar.TabParam(R.color.base_color_gray_blue, R.mipmap.tab_user_off, R.mipmap.tab_user_on, "个人中心"));
        navigateTabBar.addTab(MallFragment.class, new MainNavigateTabBar.TabParam(R.color.base_color_gray_blue, R.mipmap.tab_mall_off, R.mipmap.tab_mall_on, "飘爱商城"));

        requestPermission();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //保存当前选中的选项状态
        navigateTabBar.onSaveInstanceState(outState);
    }


    final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    void requestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //未授权 请求用户授权
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {

            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i("rthc", "用户拒绝使用定位");
                    Toast.makeText(this, "您已拒绝使用定位服务，将无法把安装位置经纬度上传到服务器", Toast.LENGTH_SHORT).show();
                    this.finish();
                }
                return;
            }
        }
    }
}
