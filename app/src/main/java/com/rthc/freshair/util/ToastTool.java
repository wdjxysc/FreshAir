package com.rthc.freshair.util;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/8/27.
 *
 * @author wdjxysc
 */
public class ToastTool {

    public static void ShowToast(final Activity activity,final String message){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getBaseContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
